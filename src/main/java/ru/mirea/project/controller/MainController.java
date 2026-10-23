package ru.mirea.project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.*;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mirea.project.model.SecurityRequest;
import ru.mirea.project.service.SecurityRequestService;

public class MainController {

    @FXML private TableView<SecurityRequest> requestTable;
    @FXML private TableColumn<SecurityRequest, Integer> colId;
    @FXML private TableColumn<SecurityRequest, String> colAddress;
    @FXML private TableColumn<SecurityRequest, String> colType;
    @FXML private TableColumn<SecurityRequest, String> colStatus;
    @FXML private TableColumn<SecurityRequest, String> colCreatedAt;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private final SecurityRequestService service = new SecurityRequestService();
    private final ObservableList<SecurityRequest> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настройка столбцов (связываем с полями класса SecurityRequest)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("objectAddress"));
        colType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Заполняем ComboBox статусов
        statusFilter.getItems().addAll("Все", "NEW", "IN_PROGRESS", "DONE", "CANCELLED");
        statusFilter.getSelectionModel().selectFirst();

        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Загружаем данные
        loadData();
    }

    private void applyFilters() {
        String query = searchField.getText();
        String status = statusFilter.getValue();

        List<SecurityRequest> filtered = service.getAllRequests().stream()
                .filter(r -> query == null || query.isEmpty() || r.getObjectAddress().toLowerCase().contains(query.toLowerCase()))
                .filter(r -> status == null || status.equals("Все") || r.getStatus().name().equals(status))
                .collect(Collectors.toList());

        data.setAll(filtered);
    }

    private void loadData() {
        data.clear();
        data.addAll(service.getAllRequests());
        requestTable.setItems(data);
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText();
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add-request.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Добавление заявки");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка при открытии окна: " + e.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        SecurityRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите заявку для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit-request.fxml"));
            Parent root = loader.load();

            EditRequestController controller = loader.getController();
            controller.setRequest(selected);

            Stage stage = new Stage();
            stage.setTitle("Редактирование заявки");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка при открытии окна: " + e.getMessage());
        }

    }

    @FXML
    private void onDelete() {
        SecurityRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите заявку для удаления");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить заявку?");
        alert.setContentText("Вы уверены, что хотите удалить заявку по адресу: " + selected.getObjectAddress() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.deleteRequest(selected.getId());
                loadData();
            } catch (Exception e) {
                showAlert("Ошибка при удалении: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onStatistics() {
    }

    @FXML
    private void onExport() {
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
