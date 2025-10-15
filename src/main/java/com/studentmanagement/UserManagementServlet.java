package com.studentmanagement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/userManagement")
public class UserManagementServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("SUPERADMIN")) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/userManagement.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            int userId = Integer.parseInt(request.getParameter("id"));
            User user = userDAO.getUserById(userId);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/userManagement.jsp").forward(request, response);
        } else if (action.equals("delete")) {
            int userId = Integer.parseInt(request.getParameter("id"));
            userDAO.deleteUser(userId);
            response.sendRedirect("userManagement"); // Redirect to refresh the list
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        if (role == null || !role.equals("SUPERADMIN")) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userRole = request.getParameter("role");

        if (action.equals("add")) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(userRole);
            userDAO.addUser(newUser);
        } else if (action.equals("update")) {
            int userId = Integer.parseInt(request.getParameter("id"));
            User existingUser = userDAO.getUserById(userId);
            if (existingUser != null) {
                existingUser.setUsername(username);
                existingUser.setPassword(password); // Consider hashing in a real app
                existingUser.setRole(userRole);
                userDAO.updateUser(existingUser);
            }
        }
        response.sendRedirect("userManagement"); // Redirect to refresh the list
    }
}
