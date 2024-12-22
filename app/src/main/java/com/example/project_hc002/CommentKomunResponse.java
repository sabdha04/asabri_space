package com.example.project_hc002;

public class CommentKomunResponse {
    private int id;
    private int post_id, komunitas_id;
    private String ktpa;
    private String content;
    private String created_at;
    private String updated_at;
    private String nama;

    public CommentKomunResponse(int id, int post_id, int komunitas_id, String ktpa, String content,
                                String created_at, String updated_at, String nama) {
        this.id = id;
        this.post_id = post_id;
        this.komunitas_id = komunitas_id;
        this.ktpa = ktpa;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getKomunitas_id() {
        return komunitas_id;
    }

    public void setKomunitas_id(int komunitas_id) {
        this.komunitas_id = komunitas_id;
    }

    public String getKtpa() {
        return ktpa;
    }

    public void setKtpa(String ktpa) {
        this.ktpa = ktpa;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
