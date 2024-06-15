package com.labs.lab1.dao;

import com.labs.lab1.entity.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    private Connection connection;

    public PersonDAO(String host, int port, String username, String password, String database) throws SQLException, ClassNotFoundException {

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;

        connect();

    }

    private void connect() throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        this.connection = DriverManager.getConnection(
                String.format("jdbc:postgresql://%s:%d/%s", host, port, database),
                this.username,
                this.password
        );

    }

    public boolean exists(int id) throws SQLException {

        String query = "SELECT 1 FROM public.person WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            return rs.next();

        }

    }


    public List<Person> getAll() throws SQLException {

        List<Person> people = new ArrayList<>();

        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM public.person;");

        while (rs.next()) {
            people.add(
                    new Person(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("avatar_filename")
                    )
            );
        }

        return people;

    }

    public void add(Person person) throws SQLException {

        String query = "INSERT INTO public.person (name, age, avatar_filename) VALUES (?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, person.getName());
            statement.setInt(2, person.getAge());
            statement.setString(3, person.getAvatarFilename());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next()) {
                person.setId(rs.getInt(1));
            }

        }

    }

    public void updateName(int id, String name) throws SQLException {

        String query = "UPDATE public.person SET name = ? WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
        }

    }

    public void updateAge(int id, int age) throws SQLException {

        String query = "UPDATE public.person SET age = ? WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, age);
            statement.setInt(2, id);
            statement.executeUpdate();
        }

    }

    public void updateAvatarFilename(int id, String avatarFilename) throws SQLException {

        String query = "UPDATE public.person SET avatar_filename = ? WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, avatarFilename);
            statement.setInt(2, id);
            statement.executeUpdate();
        }

    }

    public void delete(int id) throws SQLException {

        String query = "DELETE FROM public.person WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

    }

    public void close() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

    }

}
