package WebProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CreateDoctor")
public class CreateDoctor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String ageStr = request.getParameter("age");
        String salStr = request.getParameter("sal");
        String spec = request.getParameter("spec");
        String joindate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String patients = "-1"; // initial patient list

        // Basic validation
        if (name == null || email == null || ageStr == null || salStr == null ||
            name.isEmpty() || email.isEmpty() || ageStr.isEmpty() || salStr.isEmpty()) {
            out.println("<h3 style='color:red;'>Please fill all required fields!</h3>");
            return;
        }

        try (Connection c = GetConnection.getConnection()) { // ✅ fresh connection each request

            if (c == null) {
                out.println("<h3 style='color:red;'>Database connection failed!</h3>");
                return;
            }

            int age = Integer.parseInt(ageStr);
            long salary = Long.parseLong(salStr);

            String sql = "INSERT INTO doctor(name,email,phone,age,joindate,salary,specialist,patients) VALUES (?,?,?,?,?,?,?,?)";

            try (PreparedStatement ps = c.prepareStatement(sql)) { // ✅ auto-close
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, age);
                ps.setString(5, joindate);
                ps.setLong(6, salary);
                ps.setString(7, spec);
                ps.setString(8, patients);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    out.println("<h1 align='center' style='color:green;'>Doctor Added Successfully!</h1>");
                } else {
                    out.println("<h1 align='center' style='color:red;'>Failed to Add Doctor!</h1>");
                }
            }

        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Age and Salary must be numeric!</h3>");
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>SQL Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out); // prints detailed error
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Optional: redirect GET to a form page
        response.sendRedirect("doctor_form.html");
    }
}
