package com.example.project_hc002;

public class CommentKomunRequest {
    private int post_id, komunitas_id;
    private String ktpa;
    private String content;

    public CommentKomunRequest(int post_id, int komunitas_id, String ktpa, String content) {
        this.post_id = post_id;
        this.komunitas_id = komunitas_id;
        this.ktpa = ktpa;
        this.content = content;
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
}
