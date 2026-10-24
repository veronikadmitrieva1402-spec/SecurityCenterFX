package ru.mirea.project.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.mirea.project.model.RequestStatus;
import ru.mirea.project.model.SecurityRequest;
import ru.mirea.project.model.User;
import ru.mirea.project.service.SecurityRequestService;
import ru.mirea.project.service.UserService;

public class EditRequestController {

    @FXML private ComboBox<User> clientCombo;
    @FXML private TextField addressField;
    @FXML private TextField typeField;

    private final UserService userService = new UserService();
    private final SecurityRequestService requestService = new SecurityRequestService();
    private SecurityRequest currentRequest;

    @FXML
    public void initialize() {
        clientCombo.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        clientCombo.setConverter(new StringConverter<>() {
            @Override public String toString(User user) { return user == null ? "" : user.getFullName(); }
            @Override public User fromString(String string) { return null; }
        });
    }

    public void setRequest(SecurityRequest request) {
        this.currentRequest = request;

        addressField.setText(request.getObjectAddress());
        typeField.setText(request.getServiceType());

        for (User user : clientCombo.getItems()) {
            if (user.getId() == request.getUserId()) {
                clientCombo.setValue(user);
                break;
            }
        }
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
            if (address == null || address.trim().isEmpty()) {
                showAlert("Ошибка: Адрес объекта не может быть пустым!");
                return;
            }

            String type = typeField.getText();
            if (type == null || type.trim().isEmpty()) {
                showAlert("Ошибка: Укажите тип охраны!");
                return;
            }

            currentRequest.setUserId(selectedUser.getId());
            currentRequest.setObjectAddress(address);
            currentRequest.setServiceType(type);

            requestService.updateRequest(currentRequest);

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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}