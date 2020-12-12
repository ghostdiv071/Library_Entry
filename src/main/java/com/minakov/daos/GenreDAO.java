package com.minakov.daos;

import com.minakov.entity.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GenreDAO implements DAO<Genre>{

    private final Connection connection;

    public GenreDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Genre get(long id) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT id, name FROM genres WHERE id = " + id))
            {
                while (resultSet.next()){
                    return new Genre(resultSet.getLong("id"),
                            resultSet.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        throw new NoSuchElementException("Record with id " + id + " not found");
    }

    @Override
    public List<Genre> getAll() {
        final List<Genre> result = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT id, name FROM genres"))
            {
                while (resultSet.next()){
                    result.add(new Genre(resultSet.getLong("id"),
                            resultSet.getString("name"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @Override
    public void create(Genre entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO genres(id, name) VALUES(?,?)"))
        {
            int count = 1;
            preparedStatement.setLong(count++, entity.getId());
            preparedStatement.setString(count, entity.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(long id, Genre entity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE genres SET name = ? WHERE id = ?"))
        {
            int count = 1;
            preparedStatement.setString(count++, entity.getName());
            preparedStatement.setLong(count, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM genres WHERE id = ?"))
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
