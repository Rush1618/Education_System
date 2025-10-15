package com.studentmanagement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Attempting login with username: " + username + ", password: " + password);

        // Hardcoded check for superadmin
        if ("superadmin".equals(username) && "admin123".equals(password)) {
            System.out.println("Superadmin authentication successful (hardcoded).");
            HttpSession session = request.getSession();
            User superadminUser = new User();
            superadminUser.setUsername("superadmin");
            superadminUser.setRole("SUPERADMIN"); // Ensure the role is set correctly
            session.setAttribute("user", superadminUser);
            session.setAttribute("role", "SUPERADMIN");
            response.sendRedirect("dashboard.jsp");
            return;
        }

        StudentDAO studentDAO = new StudentDAO();
        User user = studentDAO.authenticateUser(username, password);

        if (user != null) {
            System.out.println("Authentication successful for user: " + user.getUsername() + ", role: " + user.getRole());
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            response.sendRedirect("dashboard.jsp");
            return;
        } else {
            System.out.println("Authentication failed for username: " + username);
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}

