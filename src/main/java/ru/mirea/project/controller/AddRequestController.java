package ru.mirea.project.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.mirea.project.model.User;
import ru.mirea.project.service.SecurityRequestService;
import ru.mirea.project.service.UserService;

public class AddRequestController {

    @FXML private ComboBox<User> clientCombo;
    @FXML private TextField addressField;
    @FXML private TextField typeField;

    private final UserService userService = new UserService();
    private final SecurityRequestService requestService = new SecurityRequestService();

    @FXML
    public void initialize() {
        // Загружаем клиентов в ComboBox
        clientCombo.setItems(FXCollections.observableArrayList(userService.getAllUsers()));

        // Настраиваем, как клиент будет отображаться в выпадающем списке (ФИО)
        clientCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getFullName();
            }
            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void onSave() {
        try {
            User selectedUser = clientCombo.getValue();
            if (selectedUser == null) {
                showAlert("Выберите клиента!");
                return;
            }

            String address = addressField.getText();
            String type = typeField.getText();

            // Вызываем сервис для создания заявки
            requestService.createRequest(selectedUser.getId(), address, type);

            // Закрываем окно
            Stage stage = (Stage) addressField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) addressField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}