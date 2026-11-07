// src/main/java/com/devopstrends/servlet/StatusServlet.java
package com.devopstrends.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet(name = "StatusServlet", urlPatterns = {"/status"})
public class StatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Server Status</title>");
            out.println("<style>body { font-family: Arial, sans-serif; margin: 20px; }</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Server Status</h1>");
            out.println("<p>Server is running</p>");
            out.println("<p>Current time: " + new Date() + "</p>");
            out.println("<p>Java version: " + System.getProperty("java.version") + "</p>");
            out.println("<p>OS: " + System.getProperty("os.name") + " " + 
                        System.getProperty("os.version") + "</p>");
            out.println("<a href=\"index.html\">Back to home</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}