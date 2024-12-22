package com.example.project_hc002;

public class LikeKomunRequest {
    private String ktpa;
    private int komunitasId;

    public LikeKomunRequest(String ktpa, int komunitasId) {
        this.ktpa = ktpa;
        this.komunitasId = komunitasId;
    }

    public String getKtpa() {
        return ktpa;
    }

    public void setKtpa(String ktpa) {
        this.ktpa = ktpa;
    }

    public int getKomunitasId() {
        return komunitasId;
    }

    public void setKomunitasId(int komunitasId) {
        this.komunitasId = komunitasId;
    }
}
