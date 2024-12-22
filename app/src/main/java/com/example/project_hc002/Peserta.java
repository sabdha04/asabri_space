package com.example.project_hc002;

public class Peserta {

    private String ktpa;
    private String email;

    private String nama;
    private String jenis_kelamin;
    private String bio;

    // Constructor
    public Peserta(String ktpa, String email, String nama, String jenis_kelamin, String bio) {

        this.ktpa = ktpa;
        this.email = email;
        this.nama = nama;
        this.jenis_kelamin = jenis_kelamin;
        this.bio = bio;
    }

    // Default constructor for JSON parsing
    public Peserta() {
    }

    // Getters and Setters
    public String getKtpa() {
        return ktpa;
    }

    public void setKtpa(String ktpa) {
        this.ktpa = ktpa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return nama;
    }

    public void setName(String name) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenis_kelamin;
    }

    public String getBio() {
        return bio;
    }

    public String setBio(String bio) { return bio; }
}