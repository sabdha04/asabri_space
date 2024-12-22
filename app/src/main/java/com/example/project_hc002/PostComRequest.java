package com.example.project_hc002;

public class PostComRequest {
    String user_ktpa;
    int komunitas_id;
    String konten, media_url, media_type, created_at, updated_at;

    public PostComRequest(String user_ktpa, int komunitas_id, String konten, String media_url, String media_type, String created_at, String updated_at) {
        this.user_ktpa = user_ktpa;
        this.komunitas_id = komunitas_id;
        this.konten = konten;
        this.media_url = media_url;
        this.media_type = media_type;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public String getKonten() {
        return konten;
    }

    public void setKonten(String konten) {
        this.konten = konten;
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
}
