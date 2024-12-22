package com.example.project_hc002;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {
    //    int id;
    String evtitle, evdesc, evdate, evloc, evkuota;

    private EventPic eventpic;
    public Event() {
    }

    public Event(String evtitle, String evdesc, String evdate, String evloc, String evkuota) {
        this.evtitle = evtitle;
        this.evdesc = evdesc;
        this.evdate = evdate;
        this.evloc = evloc;
        this.evkuota = evkuota;
    }

    public EventPic getEventpic() {
        return eventpic;
    }

    public void setEventpic(EventPic eventpic) {
        this.eventpic = eventpic;
    }

    //    public String getEventpic() {
//        return eventpic;
//    }
//
//    public void setEventpic(String eventpic) {
//        this.eventpic = eventpic;
//    }
    //    public Event(String evtitle, String evdesc, String evdate, String evloc, String evkuota) {
//        this.evtitle = evtitle;
//        this.evdesc = evdesc;
//        this.evdate = evdate;
//        this.evloc = evloc;
//        this.evkuota = evkuota;
//    }

    public String getEvtitle() {
        return evtitle;
    }

    public void setEvtitle(String evtitle) {
        this.evtitle = evtitle;
    }

    public String getEvdesc() {
        return evdesc;
    }

    public void setEvdesc(String evdesc) {
        this.evdesc = evdesc;
    }

    public String getEvdate() {
        return evdate;
    }

    public void setEvdate(String evdate) {
        this.evdate = evdate;
    }

    public String getEvloc() {
        return evloc;
    }

    public void setEvloc(String evloc) {
        this.evloc = evloc;
    }

    public String getEvkuota() {
        return evkuota;
    }

    public void setEvkuota(String evkuota) {
        this.evkuota = evkuota;
    }

    //    class EventPic {
//        private String type;
//        private List<Integer> data;  // Array byte dalam bentuk List<Integer>
//
//        // Getter and Setter methods
//        public List<Integer> getData() {
//            return data;
//        }
//
//        public void setData(List<Integer> data) {
//            this.data = data;
//        }
//    }
    class EventPic implements Serializable {
        private String type;
        private List<Integer> data;

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }

        public Bitmap getBitmap() {
            if (data != null && !data.isEmpty()) {
                byte[] imageData = new byte[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    imageData[i] = data.get(i).byteValue();
                }
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            }
            return null;
        }
    }

}

