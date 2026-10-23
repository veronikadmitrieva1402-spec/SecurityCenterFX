package ru.mirea.project.service;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.User;
import ru.mirea.project.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД при получении списка пользователей: " + e.getMessage());
        }
    }

    public boolean isUserExists(int userId) {
        try {
            return userRepository.findById(userId) != null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД при проверке пользователя: " + e.getMessage());
        }
    }

    public void createUser(String fullName, String phone, String email) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new BusinessException("Бизнес-правило: ФИО не может быть пустым.");
        }
        if (phone == null || phone.length() < 5) {
            throw new BusinessException("Бизнес-правило: Некорректный номер телефона.");
        }

        User user = new User(0, fullName, phone, email);
        try {
            userRepository.save(user);
            System.out.println("Пользователь успешно добавлен.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД при создании пользователя (возможно, email или телефон уже заняты): " + e.getMessage());
        }
    }

    public User getUserById(int id) {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                throw new EntityNotFoundException("Пользователь с ID " + id + " не найден.");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка БД: " + e.getMessage());
        }
    }
}