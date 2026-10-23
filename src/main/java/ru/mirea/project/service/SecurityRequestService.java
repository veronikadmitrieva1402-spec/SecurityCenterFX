package ru.mirea.project.service;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.RequestStatus;
import ru.mirea.project.model.SecurityRequest;
import ru.mirea.project.repository.SecurityRequestRepository;
import ru.mirea.project.util.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityRequestService {
    private final SecurityRequestRepository repository = new SecurityRequestRepository();

    public void createRequest(int userId, String address, String type) {
        if (address == null || address.trim().isEmpty()) {
            throw new BusinessException("Бизнес-правило: Адрес объекта не может быть пустым.");
        }
        if (userId <= 0) {
            throw new BusinessException("Бизнес-правило: Некорректный ID пользователя.");
        }

        SecurityRequest req = new SecurityRequest(0, userId, address, type, RequestStatus.NEW, LocalDateTime.now());
        try {
            repository.save(req);
            System.out.println("Заявка успешно создана.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД при создании заявки: " + e.getMessage());
        }
    }

    public List<SecurityRequest> getAllRequests() {
        try {
            return repository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД: " + e.getMessage());
        }
    }


    public SecurityRequest getRequestById(int id) {
        try {
            SecurityRequest req = repository.findById(id);
            if (req == null) {
                throw new EntityNotFoundException("Заявка с ID " + id + " не найдена.");
            }
            return req;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД: " + e.getMessage());
        }
    }

    public void updateStatus(int id, String newStatusStr) {
        try {
            SecurityRequest req = repository.findById(id);
            if (req == null) {
                throw new EntityNotFoundException("Заявка с ID " + id + " не найдена.");
            }

            RequestStatus newStatus;
            try {
                newStatus = RequestStatus.valueOf(newStatusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Бизнес-правило: Некорректный статус.");
            }

            if (req.getStatus() == RequestStatus.DONE && newStatus == RequestStatus.NEW) {
                throw new BusinessException("Бизнес-правило: Нельзя вернуть выполненную заявку в статус NEW.");
            }

            repository.updateStatus(id, newStatus);
            System.out.println("Статус обновлен.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД: " + e.getMessage());
        }
    }

    public void deleteRequest(int id) {
        try {
            SecurityRequest req = repository.findById(id);
            if (req == null) {
                throw new EntityNotFoundException("Заявка с ID " + id + " не найдена.");
            }
            repository.delete(id);
            System.out.println("Заявка успешно удалена.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД при удалении: " + e.getMessage());
        }
    }

    public List<SecurityRequest> searchAndFilter(String addressPart, RequestStatus statusFilter, boolean sortByDate) {
        List<SecurityRequest> all = getAllRequests();

        return all.stream()
                .filter(r -> addressPart == null
                        || r.getObjectAddress().toLowerCase().contains(addressPart.toLowerCase()))
                .filter(r -> statusFilter == null || r.getStatus() == statusFilter)
                .sorted(sortByDate ? Comparator.comparing(SecurityRequest::getCreatedAt)
                        : Comparator.comparingInt(SecurityRequest::getId))
                .collect(Collectors.toList());
    }

    public void printStatistics() {
        List<SecurityRequest> all = getAllRequests();
        long total = all.size();
        long newCount = all.stream().filter(r -> r.getStatus() == RequestStatus.NEW).count();
        long inProgress = all.stream().filter(r -> r.getStatus() == RequestStatus.IN_PROGRESS).count();
        long done = all.stream().filter(r -> r.getStatus() == RequestStatus.DONE).count();
        long cancelled = all.stream().filter(r -> r.getStatus() == RequestStatus.CANCELLED).count();

        System.out.println("=== СТАТИСТИКА ===");
        System.out.println("Всего заявок: " + total);
        System.out.println("Новых: " + newCount);
        System.out.println("В работе: " + inProgress);
        System.out.println("Выполнено: " + done);
        System.out.println("Отменено: " + cancelled);
    }
}