package com.minakov.services;

import com.minakov.ConnectionUtils;
import com.minakov.daos.AuthorDAO;
import com.minakov.entity.Author;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class AuthorService implements Service<Author> {

    private AuthorDAO dao;
    private long id;

    public AuthorService() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new AuthorDAO(connection);
            List<Author> all = dao.getAll();
            id = all.get(all.size()-1).getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Author get(long id) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new AuthorDAO(connection);
            return dao.get(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Author> getAll() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new AuthorDAO(connection);
            return dao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Author entity) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new AuthorDAO(connection);
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
            dao = new AuthorDAO(connection);
            dao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
