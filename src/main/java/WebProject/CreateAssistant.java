package WebProject;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/CreateAssistant")
public class CreateAssistant extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // 1. Get parameters from form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String pwd = request.getParameter("pwd");
        String joindate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // 2. Database connection
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL (same DB you see in phpMyAdmin)
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/hospital?useSSL=false&serverTimezone=UTC",
                "root",        // username (phpMyAdmin login username)
                "root@123"             // password (phpMyAdmin login password, keep empty if none)
            );

            // 3. Insert query
            String sql = "INSERT INTO assistant(name,email,phone,password,joindate) VALUES(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, pwd);
            ps.setString(5, joindate);

            // 4. Execute and redirect
            if (ps.executeUpdate() > 0) {
                response.sendRedirect("login.html");
            } else {
                showRetryMessage(out);
            }

        } catch (Exception e) {
            e.printStackTrace(out); // show error on page for debugging
            showRetryMessage(out);
        } finally {
            // Close resources
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    private void showRetryMessage(PrintWriter out) {
        out.println("""
            <div style='text-align:center; margin-top:50px;'>
                <h1 style='color:red;'>Registration Failed!</h1>
                <p>Redirecting back in 5 seconds...</p>
                <script>
                    setTimeout(() => window.location.href='newAssistant.html', 5000);
                </script>
            </div>""");
    }
}
