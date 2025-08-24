package WebProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CreatePatient")
public class CreatePatient extends HttpServlet {
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
        String gender = request.getParameter("gender");
        String blood = request.getParameter("blood");
        String symptom = request.getParameter("symptom");
        String disease = request.getParameter("disease");
        String doctorStr = request.getParameter("doctor");
        String visited = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Validate inputs
        if (name == null || email == null || ageStr == null || doctorStr == null ||
            name.isEmpty() || email.isEmpty() || ageStr.isEmpty() || doctorStr.isEmpty()) {
            out.println("<h3 style='color:red;'>Please fill all required fields!</h3>");
            return;
        }

        try (Connection c = GetConnection.getConnection()) { // ✅ fresh connection for each request

            if (c == null || c.isClosed()) {
                out.println("<h3 style='color:red;'>Database connection failed!</h3>");
                return;
            }

            int age = Integer.parseInt(ageStr);
            int doctorId = Integer.parseInt(doctorStr);

            // 1️⃣ Insert patient
            String insertPatientSQL = "INSERT INTO patient(name,email,phone,age,gender,blood,visited,symptom,disease,doctor) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";

            int patientId = -1;

            try (PreparedStatement ps = c.prepareStatement(insertPatientSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, age);
                ps.setString(5, gender);
                ps.setString(6, blood);
                ps.setString(7, visited);
                ps.setString(8, symptom);
                ps.setString(9, disease);
                ps.setInt(10, doctorId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    out.println("<h3 style='color:red;'>Failed to insert patient!</h3>");
                    return;
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        patientId = rs.getInt(1); // ✅ get inserted patient id
                    }
                }
            }

            // 2️⃣ Update doctor's patient list
            String selectDoctorSQL = "SELECT patients FROM doctor WHERE did = ?";
            String patients = "-1";

            try (PreparedStatement ps = c.prepareStatement(selectDoctorSQL)) {
                ps.setInt(1, doctorId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        patients = rs.getString("patients");
                    }
                }
            }

            String newPatients = (patients == null || patients.equals("-1")) ? String.valueOf(patientId)
                    : patients + "," + patientId;

            String updateDoctorSQL = "UPDATE doctor SET patients = ? WHERE did = ?";
            try (PreparedStatement ps = c.prepareStatement(updateDoctorSQL)) {
                ps.setString(1, newPatients);
                ps.setInt(2, doctorId);
                ps.executeUpdate();
            }

            out.println("<h1 align='center' style='color:green;'>Patient Added Successfully!</h1>");

        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Age and Doctor ID must be numeric!</h3>");
        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>SQL Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found!</h3>");
            e.printStackTrace(out);
        }
    }
}
