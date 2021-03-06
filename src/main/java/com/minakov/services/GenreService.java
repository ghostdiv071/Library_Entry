package com.minakov.services;

import com.minakov.ConnectionUtils;
import com.minakov.daos.GenreDAO;
import com.minakov.entity.Genre;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class GenreService implements Service<Genre> {

    private GenreDAO dao;
    private long id;

    public GenreService() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new GenreDAO(connection);
            List<Genre> all = dao.getAll();
            id = all.get(all.size()-1).getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Genre get(long id) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new GenreDAO(connection);
            return dao.get(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Genre> getAll() {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new GenreDAO(connection);
            return dao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Genre entity) {
        try (Connection connection = DriverManager.getConnection(ConnectionUtils.URL.value,
                ConnectionUtils.USER.value, ConnectionUtils.PASSWORD.value))
        {
            dao = new GenreDAO(connection);
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
            dao = new GenreDAO(connection);
            dao.delete(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
