package ru.mirea.project.model;

public class User {
    private int id;
    private String fullName;
    private String phone;
    private String email;

    public User(int id, String fullName, String phone, String email) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {return id;}
    public String getFullName() {return fullName;}
    public String getPhone() {return phone;}
    public String getEmail() {return email;}

    @Override
    public String toString() {
        return String.format("ID: %d | ФИО: %s | Тел: %s | Email: %s", id, fullName, phone, email);
    }
}
