package com.example.project_hc002;

import java.io.Serializable;

public class Community implements Serializable {
    private String nama;
    private String post;
    private String imgpost;
    private String created_at;
    private String updated_at;
    private int id;
    private String ktpa;
    private String media_type;
    private int commentCount;

    public Community(String username, String post, String imgpost, String created_at, String media_type) {
        this.nama = username;
        this.post = post;
        this.imgpost = imgpost;
        this.created_at = created_at;
        this.media_type = media_type;
    }

//    public Community(String nama, String content, String imageUrl, String relativeTime) {
//    }

    public String getNama() {
        return nama != null ? nama : "";
    }

    public void setNama(String username) {
        this.nama = nama;
    }
    public String getdate() {
        return created_at != null ? created_at : "";
    }

    public void setdate(String created_at) {
        this.created_at = created_at;
    }

    public String getPost() {
        return post != null ? post : "";
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImgpost() {
        return imgpost;
    }

    public String getCreated_at() {
        return created_at != null ? created_at : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKtpa() {
        return ktpa;
    }

    public void setKtpa(String ktpa) {
        this.ktpa = ktpa;
    }

    public String getMediaType() {
        return media_type != null ? media_type : "";
    }

    public void setMediaType(String media_type) {
        this.media_type = media_type;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
