package com.example.project_hc002;

public class Staff {
    private int id;
    private String username;
    private String password;
    private String last_login;


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return username  ; }
    public void setNama(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLast_login() { return last_login; }
    public void setLast_login(String last_login) { this.last_login = last_login; }

}