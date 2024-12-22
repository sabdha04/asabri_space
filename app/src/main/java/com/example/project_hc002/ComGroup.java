package com.example.project_hc002;

public class ComGroup {
    String komun_name, deskripsi;
    Integer imgcom;
    private int komunitas_id;

    public ComGroup(String komun_name, String deskripsi, Integer imgcom, int komunitas_id) {
        this.komun_name = komun_name;
        this.deskripsi = deskripsi;
        this.imgcom = imgcom;
        this.komunitas_id = komunitas_id;
    }

    public String getKomun_name() {
        return komun_name;
    }

    public void setKomun_name(String komun_name) {
        this.komun_name = komun_name;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getImgcom() {
        return imgcom;
    }

    public void setImgcom(Integer imgcom) {
        this.imgcom = imgcom;
    }

    public int getKomunitas_id() {
        return komunitas_id;
    }

    public void setKomunitas_id(int komunitas_id) {
        this.komunitas_id = komunitas_id;
    }
}
