package ru.mirea.project.repository;

import java.sql.SQLException;
import java.util.List;

public interface GenericRepository<T, ID> {
    List<T> findAll() throws SQLException;
    T findById(ID id) throws SQLException;
    void save(T entity) throws SQLException;
    void delete(ID id) throws SQLException;
}