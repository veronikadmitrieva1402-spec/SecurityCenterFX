package ru.mirea.project.model;

import java.time.LocalDateTime;

public class SecurityRequest {
    private int id;
    private int userId;
    private String objectAddress;
    private String serviceType;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public SecurityRequest(int id, int userId, String objectAddress, String serviceType, RequestStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.objectAddress = objectAddress;
        this.serviceType = serviceType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getObjectAddress() { return objectAddress; }
    public String getServiceType() { return serviceType; }
    public RequestStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID: %d | Клиент ID: %d | Адрес: %s | Тип: %s | Статус: %s | Дата: %s",
                id, userId, objectAddress, serviceType, status, createdAt);
    }
}
