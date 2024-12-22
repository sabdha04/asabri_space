package com.example.project_hc002;

public class Comment {
    String evcomment, user;
    Integer pfp;

    public Comment(String evcomment, String user, Integer pfp) {
        this.evcomment = evcomment;
        this.user = user;
        this.pfp = pfp;
    }

    public Comment() {
    }

    public String getEvcomment() {
        return evcomment;
    }

    public void setEvcomment(String evcomment) {
        this.evcomment = evcomment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getPfp() {
        return pfp;
    }

    public void setPfp(Integer pfp) {
        this.pfp = pfp;
    }
}
