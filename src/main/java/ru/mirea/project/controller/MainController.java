package ru.mirea.project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        // Загружаем данные
        loadData();
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
    }

    @FXML
    private void onEdit() {
        SecurityRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите заявку для редактирования");
            return;
        }
    }

    @FXML
    private void onDelete() {
        SecurityRequest selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите заявку для удаления");
            return;
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
