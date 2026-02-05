package com.example.malennachzahlen.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private long registeredAt; // einfach zu speichern und in abfragen verwendbar, später in Date konvertierbar

    // Leerer Konstruktor damit Firebase Daten lesen kann
    public User() {}

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.registeredAt = System.currentTimeMillis();
    }

    // Getter
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    //Setter
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRegisteredAt(long registeredAt) {
        this.registeredAt = registeredAt;
    }

}
