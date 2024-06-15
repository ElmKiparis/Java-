package com.labs.lab1.servlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.labs.lab1.Config;
import com.labs.lab1.entity.Person;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


@WebServlet(name = "ObjectServlet", value = "/object")
@MultipartConfig
public class ObjectServlet extends HttpServlet {

    static {

        try {

            readPeople(Config.DATA_FILENAME);

            System.out.printf("People list already exists (%s\\%s)%n", System.getProperty("user.dir"), Config.DATA_FILENAME);

        } catch (ClassNotFoundException | IOException e1) {

            List<Person> people = new ArrayList<>();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Config.DATA_FILENAME))) {
                oos.writeObject(people);
                System.out.println("People list initialized successfully");
            } catch (IOException e2) {
                System.err.println("Error initializing people list");
            }

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            List<Person> people = readPeople(Config.DATA_FILENAME);

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

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(peopleJson);

        } catch (ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "File with data not found");
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

            writePerson(Config.DATA_FILENAME, new Person(name, age));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("{\"success\": true, \"message\": \"Person added successfully\"}");

        } catch (ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "File with data not found");
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

            Person person = readPerson(Config.DATA_FILENAME, id);

            if (person == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
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

                int updatedId = updatePersonAvatarFilename(Config.DATA_FILENAME, id, fileName);

                if (updatedId == -1) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
                    return;
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"File uploaded successfully\"}");

            } else {

                String name = getValue(request.getPart("name"));
                int age = Integer.parseInt(getValue(request.getPart("age")));

                int updatedId = updatePersonData(Config.DATA_FILENAME, id, name, age);

                if (updatedId == -1) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
                    return;
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"People list updated successfully\"}");

            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {

            int id = Integer.parseInt(request.getParameter("id"));

            int deletedId = deletePerson(Config.DATA_FILENAME, id);

            if (deletedId == -1) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Person with id %s does not exist", id));
            } else {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("{\"success\": true, \"message\": \"Person deleted successfully\"}");

            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }

    }

    private static ArrayList<Person> readPeople(String filename) throws ClassNotFoundException, IOException {

        ArrayList<Person> people;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            people = (ArrayList<Person>) ois.readObject();
        }

        return people;

    }

    private static Person readPerson(String filename, int id) throws ClassNotFoundException, IOException {

        ArrayList<Person> people;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            people = (ArrayList<Person>) ois.readObject();
        }

        Person person = null;

        for (Person p : people) {
            if (p.getId() == id) {
                person = p;
                break;
            }
        }

        return person;

    }

    private static void writePerson(String filename, Person person) throws ClassNotFoundException, IOException {

        ArrayList<Person> people = readPeople(filename);

        if (people.isEmpty()) {
            person.setId(1);
        } else {
            person.setId(people.get(people.size() - 1).getId() + 1);
        }

        people.add(person);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }

    }

    private static int updatePersonData(String filename, int id, String name, int age) throws ClassNotFoundException, IOException {

        ArrayList<Person> people = readPeople(filename);

        int updatedId = -1;

        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getId() == id) {
                people.get(i).setName(name);
                people.get(i).setAge(age);
                updatedId = id;
                break;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }

        return updatedId;

    }

    private static int updatePersonAvatarFilename(String filename, int id, String avatarFilename) throws ClassNotFoundException, IOException {

        ArrayList<Person> people = readPeople(filename);

        int updatedId = -1;

        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getId() == id) {
                people.get(i).setAvatarFilename(avatarFilename);
                updatedId = id;
                break;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }

        return updatedId;

    }

    private static int deletePerson(String filename, int id) throws ClassNotFoundException, IOException {

        ArrayList<Person> people = readPeople(filename);

        int personIndex = -1;

        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getId() == id) {
                personIndex = i;
                break;
            }
        }

        if (personIndex != -1) {
            people.remove(personIndex);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
        }

        if (personIndex != -1) {
            return id;
        } else {
            return -1;
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
