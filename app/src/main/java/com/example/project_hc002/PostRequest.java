package com.example.project_hc002;

public class PostRequest {
    private String ktpa;
    private String content;
    private String created_at;
    private String updated_at;
    private String media_url;
    private String media_type;

    public PostRequest(String ktpa, String content, String created_at, String updated_at, String media_url, String media_type) {
        this.ktpa = ktpa;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.media_url = media_url;
        this.media_type = media_type;
    }
}