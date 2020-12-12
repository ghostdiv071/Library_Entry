package com.minakov.daos;

import com.minakov.entity.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AuthorDAO implements DAO<Author> {

    private final Connection connection;

    public AuthorDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Author get(long id) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT id, name, surname, second_name FROM authors WHERE id = " + id))
            {
                while (resultSet.next()){
                    return new Author(resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("second_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        throw new NoSuchElementException("Record with id " + id + " not found");
    }

    @Override
    public List<Author> getAll() {
        final List<Author> result = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT id, name, surname, second_name FROM authors"))
            {
                while (resultSet.next()){
                    result.add(new Author(resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("second_name"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @Override
    public void create(Author entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO authors(id, name, surname, second_name) VALUES(?,?,?,?)"))
        {
            int count = 1;
            preparedStatement.setLong(count++, entity.getId());
            preparedStatement.setString(count++, entity.getName());
            preparedStatement.setString(count++, entity.getSurname());
            preparedStatement.setString(count, entity.getSecondName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(long id, Author entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE authors SET name = ?, surname = ?, second_name = ? WHERE id = ?"))
        {
            int count = 1;
            preparedStatement.setString(count++, entity.getName());
            preparedStatement.setString(count++, entity.getSurname());
            preparedStatement.setString(count++, entity.getSecondName());
            preparedStatement.setLong(count, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM authors WHERE id = ?"))
        {
            preparedStatement.setLong(1, id);
            if (preparedStatement.executeUpdate() == 0) {
                throw new NoSuchElementException("Record with id = " + id + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
