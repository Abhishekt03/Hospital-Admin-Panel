package WebProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RetrievePatientsDID")
public class RetrievePatientsDID extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String doctorId = request.getParameter("did");
        
        if (doctorId == null || doctorId.trim().isEmpty()) {
            out.println("<h3 style='color:red;'>Doctor ID is required!</h3>");
            return;
        }

        try (Connection c = GetConnection.getConnection()) {
            
            // First get doctor name for display
            String doctorSql = "SELECT name FROM doctor WHERE did = ?";
            String doctorName = "Unknown Doctor";
            
            try (PreparedStatement doctorPs = c.prepareStatement(doctorSql)) {
                doctorPs.setInt(1, Integer.parseInt(doctorId));
                try (ResultSet doctorRs = doctorPs.executeQuery()) {
                    if (doctorRs.next()) {
                        doctorName = doctorRs.getString("name");
                    }
                }
            }

            // Get patients for this doctor
            String sql = "SELECT * FROM patient WHERE doctor = ?";
            
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(doctorId));
                
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    
                    out.println("<html><head><title>Patients List</title>");
                    out.println("<style>");
                    out.println("table { border-collapse: collapse; width: 90%; margin: 20px auto; }");
                    out.println("th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }");
                    out.println("th { background-color: #f2f2f2; }");
                    out.println(".back-btn { margin: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; }");
                    out.println("</style>");
                    out.println("</head><body>");
                    
                    out.println("<h2 align='center'>Patients under Dr. " + doctorName + " (ID: " + doctorId + ")</h2>");
                    out.println("<a href='RetrieveDoctor' class='back-btn'>Back to Doctors</a>");
                    
                    if (!rs.isBeforeFirst()) {
                        out.println("<p align='center'>No patients found for this doctor.</p>");
                        out.println("</body></html>");
                        return;
                    }
                    
                    out.println("<table>");
                    out.println("<tr>");
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        out.println("<th>" + meta.getColumnName(i) + "</th>");
                    }
                    out.println("</tr>");
                    
                    while (rs.next()) {
                        out.println("<tr>");
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            out.println("<td>" + rs.getString(i) + "</td>");
                        }
                        out.println("</tr>");
                    }
                    
                    out.println("</table>");
                    out.println("</body></html>");
                }
            }
            
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>Database error occurred. Check server logs.</h3>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>Database driver not found!</h3>");
            e.printStackTrace(out);
        } catch (NumberFormatException e) {
            out.println("<h3 style='color:red;'>Invalid Doctor ID format!</h3>");
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Unexpected error occurred!</h3>");
            e.printStackTrace(out);
        }
    }
}