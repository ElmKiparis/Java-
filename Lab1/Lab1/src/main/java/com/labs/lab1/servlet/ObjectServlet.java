package com.labs.lab1.servlet;

import java.io.*;

import com.labs.lab1.Config;
import com.labs.lab1.entity.Person;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


@WebServlet(name = "ObjectServlet", value = "/object")
@MultipartConfig
public class ObjectServlet extends HttpServlet {

    private static final Person person = new Person(1, "John Doe", 30, "1-john-doe.jpg");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(person.toString());

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request content must be multipart/form-data");
                return;
            }

            Part filePart = request.getPart("files");

            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {


                String fileName = filePart.getSubmittedFileName().trim();

                if (fileName.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Filename is missing");
                    return;
                }

                fileName = person.getId() + "-" + fileName;

                String filePath = Config.STATIC_FILES_PATH + fileName;

                saveUploadedFile(filePart.getInputStream(), filePath);

                person.setAvatarFilename(fileName);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"File uploaded successfully\"}");

            } else {

                String name = getValue(request.getPart("name"));
                int age = Integer.parseInt(getValue(request.getPart("age")));

                person.setName(name);
                person.setAge(age);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"Object updated successfully\"}");

            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private void saveUploadedFile(InputStream uploadedInputStream, String filePath) throws IOException {

        try (OutputStream out = new FileOutputStream(filePath)) {

            int read;

            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();

        }

    }

    private String getValue(Part part) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder value = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            value.append(line);
        }
        return value.toString().trim();
    }

}
