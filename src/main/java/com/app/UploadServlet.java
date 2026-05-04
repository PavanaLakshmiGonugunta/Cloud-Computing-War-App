package com.app;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    private S3Service s3Service = new S3Service();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        
        try {
            s3Service.uploadFile(fileName, filePart.getInputStream(), filePart.getSize());
            // Redirect to your success.jsp after completion
            response.sendRedirect("success.jsp");
        } catch (Exception e) {
            response.getWriter().println("Upload failed: " + e.getMessage());
        }
    }
}