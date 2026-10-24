package ru.mirea.project.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.scene.control.*;
import java.io.File;

import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.mirea.project.model.SecurityRequest;
import ru.mirea.project.service.SecurityRequestService;
import ru.mirea.project.util.ExcelExporter;

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
    private FilteredList<SecurityRequest> filteredData;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("objectAddress"));
        colType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        statusFilter.getItems().addAll("Все", "NEW", "IN_PROGRESS", "DONE", "CANCELLED");
        statusFilter.getSelectionModel().selectFirst();

        // Инициализируем FilteredList на основе data
        filteredData = new FilteredList<>(data, p -> true);
        requestTable.setItems(filteredData);

        // Слушатели для поиска и фильтра
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        loadData();
    }

    private void applyFilters() {
        String query = searchField.getText();
        String status = statusFilter.getValue();

        filteredData.setPredicate(request -> {
            if (query != null && !query.isEmpty()) {
                if (!request.getObjectAddress().toLowerCase().contains(query.toLowerCase())) {
                    return false;
                }
            }

            if (status != null && !status.equals("Все")) {
                if (!request.getStatus().name().equals(status)) {
                    return false;
                }
            }

            return true;
        });
    }

    private void loadData() {
        data.setAll(service.getAllRequests());
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    @FXML
    private void onSearch() {
        applyFilters();
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
        List<SecurityRequest> all = service.getAllRequests();

        long total = all.size();
        long newCount = all.stream().filter(r -> r.getStatus().name().equals("NEW")).count();
        long inProgress = all.stream().filter(r -> r.getStatus().name().equals("IN_PROGRESS")).count();
        long done = all.stream().filter(r -> r.getStatus().name().equals("DONE")).count();
        long cancelled = all.stream().filter(r -> r.getStatus().name().equals("CANCELLED")).count();

        String message = String.format(
                "Всего заявок: %d%n" +
                        "Новых: %d%n" +
                        "В работе: %d%n" +
                        "Выполнено: %d%n" +
                        "Отменено: %d",
                total, newCount, inProgress, done, cancelled
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Статистика");
        alert.setHeaderText("Показатели системы");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные в CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("requests_export.csv");

        File file = fileChooser.showSaveDialog(requestTable.getScene().getWindow());

        if (file != null) {
            try {
                // Используем ваш существующий ExcelExporter (он умеет писать CSV)
                ExcelExporter.exportToCsv(data, file.getAbsolutePath());

                showAlert("Данные успешно экспортированы в файл:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Ошибка при экспорте: " + e.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
