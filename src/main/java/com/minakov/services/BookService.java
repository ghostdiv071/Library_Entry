package com.minakov.services;

import com.minakov.ConnectionUtils;
import com.minakov.daos.BookDAO;
import com.minakov.entity.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookService implements Service<Book> {

    private BookDAO dao;
    private long id;
    
    private final String nameFilter = "SELECT books.id AS id, books.name AS name,\n" +
            "\tauthors.name AS a_name, authors.surname AS a_surname,\n" +
            "\tgenres.name AS genre, books.publisher AS publisher, \n" +
            "\tbooks.year AS year, books.city AS city\n" +
            "\tFROM books JOIN authors ON authors.id = books.author\n" +
            "\tJOIN genres ON genres.id = books.genre\n" +
            "\tWHERE books.name LIKE '%";
    
    private final String authorFilter = "SELECT books.id AS id, books.name AS name,\n" +
            "\tauthors.name AS a_name, authors.surname AS a_surname,\n" +
            "\tgenres.name AS genre, books.publisher AS publisher, \n" +
            "\tbooks.year AS year, books.city AS city\n" +
            "\tFROM books JOIN authors ON authors.id = books.author\n" +
            "\tJOIN genres ON genres.id = books.genre\n" +
            "\tWHERE authors.name LIKE '%";
    
    private final String publisherFilter = "SELECT books.id AS id, books.name AS name,\n" +
            "\tauthors.name AS a_name, authors.surname AS a_surname,\n" +
            "\tgenres.name AS genre, books.publisher AS publisher, \n" +
            "\tbooks.year AS year, books.city AS city\n" +
            "\tFROM books JOIN authors ON authors.id = books.author\n" +
            "\tJOIN genres ON genres.id = books.genre\n" +
            "\tWHERE books.publisher LIKE '%";
    
    public BookService() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new BookDAO(connection);
            List<Book> all = dao.getAll();
            id = all.get(all.size()-1).getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book get(long id) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new BookDAO(connection);
            return dao.get(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Book> getAll() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new BookDAO(connection);
            return dao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Book> getAll(String filterText, int num){
        if (filterText == null || filterText.isEmpty()) {
            return getAll();
        } else {
            return findAllMatches(filterText, num);
        }
    }

    private List<Book> findAllMatches(String filterText, int num) {
        List<Book> books = new ArrayList<>();
        String query;
        switch (num) {
            case 1:
                query = nameFilter + filterText + "%';";
                break;
            case 2:
                query = authorFilter + filterText + "%';";
                break;
            case 3:
                query = publisherFilter + filterText + "%';";
                break;
            default:
                throw new IllegalStateException("Unexpected filtering value");
        }

        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query))
                {
                    while (resultSet.next()){
                        books.add(new Book(resultSet.getLong("id"),
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    @Override
    public void save(Book entity) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new BookDAO(connection);
            if (entity.getId() <= id & entity.getId() != 0) {
                dao.update(entity.getId(), entity);
            } else {
                id++;
                entity.setId(id);
                dao.create(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new BookDAO(connection);
            dao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
