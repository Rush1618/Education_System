package com.studentmanagement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class StudentServlet extends HttpServlet {
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check for user session
        if (request.getSession(false) == null || request.getSession(false).getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String branch = request.getParameter("branch");
        String division = request.getParameter("division");

        List<String> branches = studentDAO.getBranches();
        request.setAttribute("branches", branches);

        List<String> divisions = null;
        if (branch != null && !branch.isEmpty()) {
            divisions = studentDAO.getDivisionsForBranch(branch);
        }
        request.setAttribute("divisions", divisions);


        if (branch != null && division != null) {
            List<StudentDetails> studentDetails = studentDAO.getStudentsByBranchAndDivision(branch, division);
            request.setAttribute("studentDetails", studentDetails);
        }
        
        // Forward to a new JSP for displaying the view
        request.getRequestDispatcher("/WEB-INF/views/studentView.jsp").forward(request, response);
    }
}
