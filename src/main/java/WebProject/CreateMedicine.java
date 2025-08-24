package WebProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CreateMedicine")
public class CreateMedicine extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().append("Use POST method to add medicine.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form parameters
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String countStr = request.getParameter("count");

        // Input validation
        if (name == null || name.isEmpty() || priceStr == null || priceStr.isEmpty() ||
            countStr == null || countStr.isEmpty()) {
            out.println("<h3 style='color:red;'>Please provide valid medicine details!</h3>");
            return;
        }

        try (Connection c = GetConnection.getConnection()) { // ✅ fresh connection each request
            if (c == null) {
                out.println("<h3 style='color:red;'>Database connection failed!</h3>");
                return;
            }

            double price = Double.parseDouble(priceStr);
            int count = Integer.parseInt(countStr);

            String sql = "INSERT INTO medicine(name, price, count) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) { // ✅ auto-close
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setInt(3, count);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    out.println("<h1 style='color:green;text-align:center;'>Medicine added successfully!</h1>");
                } else {
                    out.println("<h1 style='color:red;text-align:center;'>Failed to add medicine!</h1>");
                }
            }

        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Price and Count must be numeric!</h3>");
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>SQL ERROR: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        }
    }
}
