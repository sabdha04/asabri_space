package com.example.project_hc002;

public class JoinRequest {
    private String user_ktpa;
    private int komunitas_id;

    public JoinRequest(String user_ktpa, int komunitas_id) {
        this.user_ktpa = user_ktpa;
        this.komunitas_id = komunitas_id;
    }

    public String getUser_ktpa() {
        return user_ktpa;
    }

    public void setUser_ktpa(String user_ktpa) {
        this.user_ktpa = user_ktpa;
    }

    public int getKomunitas_id() {
        return komunitas_id;
    }

    public void setKomunitas_id(int komunitas_id) {
        this.komunitas_id = komunitas_id;
    }
}
