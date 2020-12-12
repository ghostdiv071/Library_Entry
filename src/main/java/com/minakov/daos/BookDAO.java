package com.minakov.daos;

import com.minakov.entity.Author;
import com.minakov.entity.Book;
import com.minakov.entity.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class BookDAO implements DAO<Book> {

    private final Connection connection;
    private final AuthorDAO authorDAO;
    private final GenreDAO genreDAO;

    public BookDAO(Connection connection) {
        this.connection = connection;
        authorDAO = new AuthorDAO(connection);
        genreDAO = new GenreDAO(connection);
    }

    @Override
    public Book get(long id) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT books.id AS id, books.name AS name,\n" +
                            "\tauthors.name AS a_name, authors.surname AS a_surname,\n" +
                            "\tgenres.name AS genre, books.publisher AS publisher, \n" +
                            "\tbooks.year AS year, books.city AS city\n" +
                            "\tFROM books JOIN authors ON authors.id = books.author\n" +
                            "\tJOIN genres ON genres.id = books.genre\n" +
                            "\tWHERE books.id = " + id))
            {
                while (resultSet.next()) {
                    return new Book(resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("a_name") + " "
                                    + resultSet.getString("a_surname"),
                            resultSet.getString("genre"),
                            resultSet.getString("publisher"),
                            resultSet.getInt("year"),
                            resultSet.getString("city")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        throw new NoSuchElementException("Record with id " + id + " not found");
    }

    @Override
    public List<Book> getAll() {
        final List<Book> result = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "SELECT books.id AS id, books.name AS name,\n" +
                            "\tauthors.name AS a_name, authors.surname AS a_surname,\n" +
                            "\tgenres.name AS genre, books.publisher AS publisher, \n" +
                            "\tbooks.year AS year, books.city AS city\n" +
                            "\tFROM books JOIN authors ON authors.id = books.author\n" +
                            "\tJOIN genres ON genres.id = books.genre\n"))
            {
                while (resultSet.next()) {
                    result.add(new Book(resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("a_name") + " "
                                    + resultSet.getString("a_surname"),
                            resultSet.getString("genre"),
                            resultSet.getString("publisher"),
                            resultSet.getInt("year"),
                            resultSet.getString("city"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    @Override
    public void create(Book entity) {
        long genreId = 0, authorId = 0;
        authorId = getAuthorId(entity, authorId);
        genreId = getGenreId(entity, genreId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO books(id, name, author, genre, publisher, year, city)" +
                        " VALUES (?,?,?,?,?,?,?)"))
        {
            int count = 1;
            preparedStatement.setLong(count++, entity.getId());
            preparedStatement.setString(count++, entity.getName());
            preparedStatement.setLong(count++, authorId);
            preparedStatement.setLong(count++, genreId);
            preparedStatement.setString(count++, entity.getPublisher());
            preparedStatement.setInt(count++, entity.getYear());
            preparedStatement.setString(count, entity.getCity());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(long id, Book entity) {
        long genreId = 0, authorId = 0;
        authorId = getAuthorId(entity, authorId);
        genreId = getGenreId(entity, genreId);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE books SET name = ?, author = ?, genre = ?, " +
                        "publisher = ?, year = ?, city = ? WHERE id = ?"))
        {
            int count = 1;
            preparedStatement.setString(count++, entity.getName());
            preparedStatement.setLong(count++, authorId);
            preparedStatement.setLong(count++, genreId);
            preparedStatement.setString(count++, entity.getPublisher());
            preparedStatement.setInt(count++, entity.getYear());
            preparedStatement.setString(count++, entity.getCity());
            preparedStatement.setLong(count, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM books WHERE id = ?"))
        {
            preparedStatement.setLong(1, id);
            if (preparedStatement.executeUpdate() == 0) {
                throw new NoSuchElementException("Record with id = " + id + " not found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private long getAuthorId(Book entity, long authorId) {
        for (Author author : authorDAO.getAll()) {
            if (entity.getAuthor().equals(author.getName() + " " + author.getSurname())){
                authorId = author.getId();
                break;
            }
        }
        return authorId;
    }

    private long getGenreId(Book entity, long genreId) {
        for (Genre genre : genreDAO.getAll()) {
            if (entity.getGenre().equals(genre.getName())) {
                genreId = genre.getId();
                break;
            }
        }
        return genreId;
    }
}
