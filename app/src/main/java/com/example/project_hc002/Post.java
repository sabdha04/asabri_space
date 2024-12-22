package com.example.project_hc002;

public class Post {
    private int id;
    private String ktpa;
    private String content;
    private String created_at;
    private String updated_at;
    private String media_url;
    private String media_type;
    private String nama;
    private int comment_count;

    // Constructor
    public Post(int id, String ktpa, String content, String created_at, String updated_at, 
                String media_url, String media_type, String nama, int comment_count) {
        this.id = id;
        this.ktpa = ktpa;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.media_url = media_url;
        this.media_type = media_type;
        this.nama = nama;
        this.comment_count = comment_count;
    }

    // Getters
    public int getId() { return id; }
    public String getKtpa() { return ktpa; }
    public String getContent() { return content; }
    public String getCreatedAt() { return created_at; }
    public String getUpdatedAt() { return updated_at; }
    public String getImageUrl() { return media_url; }
    public String getMediaUrl() { return media_url; }
    public String getMediaType() { return media_type; }
    public String getNama() { return nama; }
    public int getCommentCount() { return comment_count; }
    
    // Setters
    public void setCommentCount(int comment_count) {
        this.comment_count = comment_count;
    }
}