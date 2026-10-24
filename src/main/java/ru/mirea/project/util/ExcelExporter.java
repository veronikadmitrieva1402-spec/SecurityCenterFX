package ru.mirea.project.util;

import ru.mirea.project.model.SecurityRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    public static void exportToCsv(List<SecurityRequest> requests, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("ID;UserID;Address;Type;Status;CreatedAt\n");
            for (SecurityRequest r : requests) {
                writer.write(String.format("%d;%d;%s;%s;%s;%s\n",
                        r.getId(), r.getUserId(), r.getObjectAddress(), r.getServiceType(), r.getStatus(), r.getCreatedAt()));
            }
            System.out.println("Данные экспортированы в " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка экспорта: " + e.getMessage());
        }
    }
}