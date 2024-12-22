package com.example.project_hc002;

import java.io.Serializable;

public class News implements Serializable {
    private int id;
    private String title;
    private String description;
    private String date;
    private String created_at;
    private String updated_at;

    private String author;

    private String image;

    public News() {
    }

    public News(int id, String title, String description, String created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
    }

    public News(String s, String s1, int vector003) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getImage() {return  image;}

    public String getAuthor() {return author;}

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getTruncatedTitle() {
        if (title == null) return "";
        String[] words = title.split("\\s+");
        if (words.length <= 5) return title;

        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            truncated.append(words[i]);
            if (i < 4) truncated.append(" ");
        }
        return truncated.toString() + "...";
    }
}
