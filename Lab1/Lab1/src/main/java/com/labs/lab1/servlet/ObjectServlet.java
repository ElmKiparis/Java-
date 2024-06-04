package com.labs.lab1.servlet;

import java.io.*;

import com.labs.lab1.entity.Person;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


@WebServlet(name = "objectServlet", value = "/object")
public class ObjectServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Person person = new Person("John Doe", 30);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(person.toString());

    }

}
