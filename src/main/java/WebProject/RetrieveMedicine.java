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

@WebServlet("/RetrieveMedicine")
public class RetrieveMedicine extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection c = GetConnection.getConnection()) {  // ✅ fresh connection each time
            String sql = "SELECT * FROM medicine";
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet r = ps.executeQuery()) {

                ResultSetMetaData rms = r.getMetaData();

                out.println("<html><body>");
                out.println("<h2>Medicine List</h2>");
                out.println("<table border='1' cellpadding='5' cellspacing='0'>");

                // Table header
                out.println("<tr>");
                for (int i = 1; i <= rms.getColumnCount(); i++) {
                    out.println("<th>" + rms.getColumnName(i) + "</th>");
                }
                out.println("</tr>");

                // Table rows
                while (r.next()) {
                    out.println("<tr>");
                    for (int i = 1; i <= rms.getColumnCount(); i++) {
                        out.println("<td>" + r.getString(i) + "</td>");
                    }
                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("</body></html>");
            }

        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>Unable to retrieve medicines. Check DB connection!</h3>");
            e.printStackTrace(out);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
