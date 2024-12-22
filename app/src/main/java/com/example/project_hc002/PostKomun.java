package com.example.project_hc002;

public class PostKomun {
    private int id, komunitas_id;
    private String user_ktpa;
    private String content;
    private String created_at;
    private String updated_at;
    private String media_url;
    private String media_type;
    private String nama;
    private int comment_count;

    public PostKomun(int id, int komunitas_id, String user_ktpa, String content, String created_at,
                     String updated_at, String media_url, String media_type, String nama, int comment_count) {
        this.id = id;
        this.komunitas_id = komunitas_id;
        this.user_ktpa = user_ktpa;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.media_url = media_url;
        this.media_type = media_type;
        this.nama = nama;
        this.comment_count = comment_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKomunitas_id() {
        return komunitas_id;
    }

    public void setKomunitas_id(int komunitas_id) {
        this.komunitas_id = komunitas_id;
    }

    public String getUser_ktpa() {
        return user_ktpa;
    }

    public void setUser_ktpa(String user_ktpa) {
        this.user_ktpa = user_ktpa;
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

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }
}
