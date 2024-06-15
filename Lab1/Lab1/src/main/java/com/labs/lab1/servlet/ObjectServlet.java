package com.labs.lab1.servlet;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.labs.lab1.Config;
import com.labs.lab1.dao.PersonDAO;
import com.labs.lab1.entity.Person;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


@WebServlet(name = "ObjectServlet", value = "/object")
@MultipartConfig
public class ObjectServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            PersonDAO personDAO = new PersonDAO(Config.DB_HOST, Config.DB_PORT, Config.DB_USERNAME, Config.DB_PASSWORD, Config.DB_DATABASE);

            List<Person> people = personDAO.getAll();

            String peopleJson;

            if (people.isEmpty()) {
                peopleJson = "[]";
            } else {

                StringBuilder sb = new StringBuilder("[");

                for (int i = 0; i < people.size() - 1; i++) {
                    sb.append(people.get(i)).append(",");
                }

                sb.append(people.get(people.size() - 1)).append("]");

                peopleJson = sb.toString();

            }

            personDAO.close();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(peopleJson);

        } catch (SQLException | ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to work with database");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request content must be multipart/form-data");
                return;
            }

            String name = getValue(request.getPart("name"));
            int age = Integer.parseInt(getValue(request.getPart("age")));

            PersonDAO personDAO = new PersonDAO(Config.DB_HOST, Config.DB_PORT, Config.DB_USERNAME, Config.DB_PASSWORD, Config.DB_DATABASE);

            personDAO.add(new Person(name, age));

            personDAO.close();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("{\"success\": true, \"message\": \"Person added successfully\"}");

        } catch (SQLException | ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to work with database");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request content must be multipart/form-data");
                return;
            }

            int id = Integer.parseInt(request.getParameter("id"));

            PersonDAO personDAO = new PersonDAO(Config.DB_HOST, Config.DB_PORT, Config.DB_USERNAME, Config.DB_PASSWORD, Config.DB_DATABASE);

            if (!personDAO.exists(id)) {
                personDAO.close();
                response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
                return;
            }

            Part filePart = request.getPart("files");

            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {

                String fileName = filePart.getSubmittedFileName().trim();

                if (fileName.isEmpty()) {
                    personDAO.close();
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Filename is missing");
                    return;
                }

                fileName = id + "-" + fileName;

                String filePath = Config.STATIC_FILES_PATH + fileName;
                saveUploadedFile(filePart.getInputStream(), filePath);

                personDAO.updateAvatarFilename(id, fileName);

                personDAO.close();

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"File uploaded successfully\"}");

            } else {

                String name = getValue(request.getPart("name"));
                int age = Integer.parseInt(getValue(request.getPart("age")));

                personDAO.updateName(id, name);
                personDAO.updateAge(id, age);

                personDAO.close();

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"People list updated successfully\"}");

            }

        } catch (SQLException | ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to work with database");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            int id = Integer.parseInt(request.getParameter("id"));

            PersonDAO personDAO = new PersonDAO(Config.DB_HOST, Config.DB_PORT, Config.DB_USERNAME, Config.DB_PASSWORD, Config.DB_DATABASE);

            if (!personDAO.exists(id)) {
                personDAO.close();
                response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
                return;
            }

            personDAO.delete(id);

            personDAO.close();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("{\"success\": true, \"message\": \"Person deleted successfully\"}");

        } catch (SQLException | ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to work with database");
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
