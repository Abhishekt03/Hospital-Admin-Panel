package WebProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("pwd");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.html?error=1"); // missing fields
            return;
        }

        try (Connection c = GetConnection.getConnection()) {

            if (c == null || c.isClosed()) {
                response.sendRedirect("login.html?error=2"); // DB connection failed
                return;
            }

            String sql = "SELECT aid, name, password FROM assistant WHERE email = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String dbPass = rs.getString("password");
                        if (dbPass != null && dbPass.equals(password)) {
                            HttpSession session = request.getSession();
                            session.setAttribute("name", rs.getString("name"));
                            session.setAttribute("aid", rs.getInt("aid"));

                            response.sendRedirect("welcome.html");
                        } else {
                            response.sendRedirect("login.html?error=1"); // wrong password
                        }
                    } else {
                        response.sendRedirect("login.html?error=1"); // email not found
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=2"); // SQL error
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=2"); // driver not found
        }
    }
}
