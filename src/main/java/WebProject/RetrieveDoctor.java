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

@WebServlet("/RetrieveDoctor")
public class RetrieveDoctor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RetrieveDoctor() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection c = GetConnection.getConnection()) {

            if (c == null || c.isClosed()) {
                out.println("<h3 style='color:red;'>Database connection failed!</h3>");
                return;
            }

            String sql = "SELECT * FROM doctor";
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                ResultSetMetaData meta = rs.getMetaData();

                out.println("<html><head><title>Doctor List</title></head><body>");
                out.println("<h2 align='center'>Doctor Details</h2>");
                out.println("<table border='1' cellpadding='8' cellspacing='0' align='center'>");

                // Table header
                out.println("<tr>");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    out.println("<th>" + meta.getColumnName(i) + "</th>");
                }
                out.println("<th>Patients Under</th>");
                out.println("</tr>");

                // Table rows
                while (rs.next()) {
                    out.println("<tr>");
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        out.println("<td>" + rs.getString(i) + "</td>");
                    }

                    // Button to view patients under this doctor
                    out.println("<td>");
                    out.println("<form method='GET' action='RetrievePatientsDID'>");
                    out.println("<input type='hidden' name='did' value='" + rs.getString("did") + "'>");
                    out.println("<input type='submit' value='View Patients'>");
                    out.println("</form>");
                    out.println("</td>");

                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("</body></html>");
            }

        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>ERROR: Unable to retrieve doctors!</h3>");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
