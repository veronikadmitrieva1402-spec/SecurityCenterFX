package ru.mirea.project.repository;

import ru.mirea.project.model.RequestStatus;
import ru.mirea.project.model.SecurityRequest;
import ru.mirea.project.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecurityRequestRepository implements GenericRepository<SecurityRequest, Integer> {

    @Override
    public void save(SecurityRequest req) throws SQLException {
        String sql = "INSERT INTO security_requests (user_id, object_address, service_type, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, req.getUserId());
            stmt.setString(2, req.getObjectAddress());
            stmt.setString(3, req.getServiceType());
            stmt.setString(4, req.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(req.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    @Override
    public List<SecurityRequest> findAll() throws SQLException {
        List<SecurityRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM security_requests ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public SecurityRequest findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM security_requests WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        }
        return null;
    }

    // НОВЫЙ МЕТОД ДЛЯ РЕДАКТИРОВАНИЯ
    public void update(SecurityRequest req) throws SQLException {
        String sql = "UPDATE security_requests SET user_id = ?, object_address = ?, service_type = ?, status = ?, created_at = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, req.getUserId());
            stmt.setString(2, req.getObjectAddress());
            stmt.setString(3, req.getServiceType());
            stmt.setString(4, req.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(req.getCreatedAt()));
            stmt.setInt(6, req.getId());
            stmt.executeUpdate();
        }
    }

    public void updateStatus(int id, RequestStatus status) throws SQLException {
        String sql = "UPDATE security_requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM security_requests WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private SecurityRequest mapRow(ResultSet rs) throws SQLException {
        return new SecurityRequest(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("object_address"),
                rs.getString("service_type"),
                RequestStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime());
    }
}