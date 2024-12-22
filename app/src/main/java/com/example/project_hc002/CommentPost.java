package com.example.project_hc002;

public class CommentPost {
    String pcomment, user, createdat;
    Integer pfp;

    public CommentPost() {
    }

    public CommentPost(String pcomment, String user, String createdat, Integer pfp) {
        this.pcomment = pcomment;
        this.user = user;
        this.createdat = createdat;
        this.pfp = pfp;
    }

    public String getPcomment() {
        return pcomment;
    }

    public void setPcomment(String pcomment) {
        this.pcomment = pcomment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public Integer getPfp() {
        return pfp;
    }

    public void setPfp(Integer pfp) {
        this.pfp = pfp;
    }
}
