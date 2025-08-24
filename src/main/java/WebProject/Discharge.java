package WebProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Discharge")
public class Discharge extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.getWriter().println("<h3>Use POST method to discharge a patient.</h3>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        double total = 0;

        // Validate input
        String pid = request.getParameter("pid");
        String daysStr = request.getParameter("days");
        String daycostStr = request.getParameter("daycost");
        String medData = request.getParameter("mc"); // format: "1,2;3,4"

        if (pid == null || daysStr == null || daycostStr == null ||
            pid.isEmpty() || daysStr.isEmpty() || daycostStr.isEmpty()) {
            out.println("<h3 style='color:red;'>Please provide all required details!</h3>");
            return;
        }

        try (Connection c = GetConnection.getConnection()) {

            if (c == null || c.isClosed()) {
                out.println("<h3 style='color:red;'>Database connection failed!</h3>");
                return;
            }

            int days = Integer.parseInt(daysStr);
            int daycost = Integer.parseInt(daycostStr);

            // 1️⃣ Delete patient
            String sqlDelete = "DELETE FROM patient WHERE id = ?";
            try (PreparedStatement psDel = c.prepareStatement(sqlDelete)) {
                psDel.setString(1, pid);
                psDel.executeUpdate();
            }

            // 2️⃣ Calculate medicine charges
            if (medData != null && !medData.isEmpty()) {
                String[] mcs = medData.split(";");
                for (String mc : mcs) {
                    String[] parts = mc.split(",");
                    if (parts.length == 2) {
                        String mid = parts[0].trim();
                        int count = Integer.parseInt(parts[1].trim());

                        String sqlPrice = "SELECT price FROM medicine WHERE mid = ?";
                        try (PreparedStatement psMed = c.prepareStatement(sqlPrice)) {
                            psMed.setString(1, mid);
                            try (ResultSet rs = psMed.executeQuery()) {
                                if (rs.next()) {
                                    double price = rs.getDouble("price");
                                    total += price * count;
                                }
                            }
                        }
                    }
                }
            }

            // 3️⃣ Add room charges
            total += days * daycost;

            // 4️⃣ Display result
            out.println("<h1>Discharge Summary</h1>");
            out.println("<h3>Patient ID: " + pid + "</h3>");
            out.println("<h3>Total Stay Days: " + days + "</h3>");
            out.println("<h3>Room Cost (per day): " + daycost + "</h3>");
            out.println("<h2 style='color:green;'>TOTAL MONEY TO PAY: " + total + "</h2>");

        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Days and Day Cost must be numeric!</h3>");
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>SQL Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        }
    }
}
