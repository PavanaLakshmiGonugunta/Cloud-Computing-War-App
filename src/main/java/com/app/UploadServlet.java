package com.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // 1MB
    maxFileSize = 1024 * 1024 * 10,    // 10MB
    maxRequestSize = 1024 * 1024 * 50  // 50MB
)
public class UploadServlet extends HttpServlet {

    private S3Service s3Service = new S3Service();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🚀 UploadServlet triggered");

        try {
            Part filePart = request.getPart("file");

            if (filePart == null) {
                System.out.println("❌ filePart is NULL");
                response.getWriter().println("No file received!");
                return;
            }

            String fileName = filePart.getSubmittedFileName();
            long fileSize = filePart.getSize();

            System.out.println("📄 File Name: " + fileName);
            System.out.println("📏 File Size: " + fileSize);

            if (fileName == null || fileName.isEmpty()) {
                System.out.println("❌ File name is empty");
                response.getWriter().println("Invalid file!");
                return;
            }

            System.out.println("⬆️ Uploading to S3...");

            s3Service.uploadFile(fileName, filePart.getInputStream(), fileSize);

            System.out.println("✅ Upload successful!");

            response.sendRedirect(request.getContextPath() + "/success.jsp");

        } catch (Exception e) {
            System.out.println("❌ ERROR in UploadServlet:");
            e.printStackTrace();

            response.setContentType("text/plain");
            response.getWriter().println("Upload failed: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("📡 GET /upload called");
        response.getWriter().println("Servlet is alive!");
    }
}