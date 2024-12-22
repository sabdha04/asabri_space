package com.example.project_hc002;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.List;

public class Notif {
    String notiftitle, notifdesc, notifloc, notifdate, notifkuota, notifpic;
//    private Notifpic notifpic;

//    Integer notifpic;

    public Notif() {
    }

    public Notif(String notiftitle, String notifdesc, String notifloc, String notifdate, String notifkuota, String notifpic) {
        this.notiftitle = notiftitle;
        this.notifdesc = notifdesc;
        this.notifloc = notifloc;
        this.notifdate = notifdate;
        this.notifkuota = notifkuota;
        this.notifpic = notifpic;
    }

    public String getNotiftitle() {
        return notiftitle;
    }

    public void setNotiftitle(String notiftitle) {
        this.notiftitle = notiftitle;
    }

    public String getNotifdesc() {
        return notifdesc;
    }

    public void setNotifdesc(String notifdesc) {
        this.notifdesc = notifdesc;
    }

    public String getNotifloc() {
        return notifloc;
    }

    public void setNotifloc(String notifloc) {
        this.notifloc = notifloc;
    }

    public String getNotifdate() {
        return notifdate;
    }

    public void setNotifdate(String notifdate) {
        this.notifdate = notifdate;
    }

    public String getNotifkuota() {
        return notifkuota;
    }

    public void setNotifkuota(String notifkuota) {
        this.notifkuota = notifkuota;
    }

    public String getNotifpic() {
        return notifpic;
    }

    public void setNotifpic(String notifpic) {
        this.notifpic = notifpic;
    }
}
    //    public Notif(String notiftitle, String notifdesc, String notifloc, String notifdate, String notifkuota, Notifpic notifpic) {
//        this.notiftitle = notiftitle;
//        this.notifdesc = notifdesc;
//        this.notifloc = notifloc;
//        this.notifdate = notifdate;
//        this.notifkuota = notifkuota;
//        this.notifpic = notifpic;
//    }
//
//    public String getNotiftitle() {
//        return notiftitle;
//    }
//
//    public void setNotiftitle(String notiftitle) {
//        this.notiftitle = notiftitle;
//    }
//
//    public String getNotifdesc() {
//        return notifdesc;
//    }
//
//    public void setNotifdesc(String notifdesc) {
//        this.notifdesc = notifdesc;
//    }
//
//    public String getNotifloc() {
//        return notifloc;
//    }
//
//    public void setNotifloc(String notifloc) {
//        this.notifloc = notifloc;
//    }
//
//    public String getNotifdate() {
//        return notifdate;
//    }
//
//    public void setNotifdate(String notifdate) {
//        this.notifdate = notifdate;
//    }
//
//    public String getNotifkuota() {
//        return notifkuota;
//    }
//
//    public void setNotifkuota(String notifkuota) {
//        this.notifkuota = notifkuota;
//    }
//
//    public Notifpic getNotifpic() {
//        return notifpic;
//    }
//
//    public void setNotifpic(Notifpic notifpic) {
//        this.notifpic = notifpic;
//    }
//
//    class Notifpic implements Serializable {
//        private String type;
//        private List<Integer> data;
//
//        public List<Integer> getData() {
//            return data;
//        }
//
//        public void setData(List<Integer> data) {
//            this.data = data;
//        }
//
//        public Bitmap getBitmap() {
//            if (data != null && !data.isEmpty()) {
//                byte[] imageData = new byte[data.size()];
//                for (int i = 0; i < data.size(); i++) {
//                    imageData[i] = data.get(i).byteValue();
//                }
//                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//            }
//            return null;
//        }
//    }

