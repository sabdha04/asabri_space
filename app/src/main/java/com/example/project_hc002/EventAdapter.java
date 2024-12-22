package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    Context context;
    List<Event> eventList;
    private String layoutType;

    public  EventAdapter(Context context, List<Event> eventList, String layoutType){
        this.context = context;
        this.eventList = eventList;
        this.layoutType = layoutType;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if ("home".equals(layoutType)) {
            view = layoutInflater.inflate(R.layout.item_event2, null);
        } else {
            view = layoutInflater.inflate(R.layout.item_evcard, parent, false);
        }
        return new EventViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {

        Event event = eventList.get(position);

        if ("home".equals(layoutType)) {
            eventViewHolder.evtitle2.setText(event.getEvtitle());
//            eventViewHolder.eventpic.setImageDrawable(context.getResources().getDrawable(event.getEventpic()));
//            eventViewHolder.eventpic.setImageDrawable(context.getResources().getDrawable(R.drawable.addimage));
            setEventImage(event.getEventpic(), eventViewHolder.eventpic);

            eventViewHolder.evlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap eventImage = event.getEventpic().getBitmap();
                    if (eventImage != null) {
                        String imagePath = saveImageToInternalStorage(eventImage);
                        Log.d("EventAdapter", "Event Pic: " + imagePath);

                        if (imagePath != null) {
                            Intent it = new Intent(view.getContext(), EventPage.class);
                            it.putExtra("event_title", event.getEvtitle());
                            it.putExtra("event_desc", event.getEvdesc());
                            it.putExtra("event_kuota", event.getEvkuota());
                            it.putExtra("event_date", event.getEvdate());
                            it.putExtra("event_location", event.getEvloc());
                            it.putExtra("event_pic_uri", Uri.parse(imagePath).toString());
                            view.getContext().startActivity(it);
                        }
                    }
//                    view.getContext().startActivity(it);
                }
            });
            if (eventList.size() > 4) {
                eventList = new ArrayList<>(eventList.subList(0, 4));
            }
        }
        if ("eventAll".equals(layoutType)) {
            eventViewHolder.evtitle.setText(event.getEvtitle());
//            eventViewHolder.evpic.setImageDrawable(context.getResources().getDrawable(R.drawable.addimage));
            eventViewHolder.evdet.setText(event.getEvdesc());
            setEventImage(event.getEventpic(), eventViewHolder.evpic);

            eventViewHolder.cardev.setOnClickListener(view -> {
                Bitmap eventImage = event.getEventpic().getBitmap();
                if (eventImage != null) {
                    String imagePath = saveImageToInternalStorage2(eventImage);
                    if (imagePath != null) {
                        Intent intent = new Intent(view.getContext(), EventPage.class);
                        intent.putExtra("event_title", event.getEvtitle());
                        intent.putExtra("event_desc", event.getEvdesc());
                        intent.putExtra("event_kuota", event.getEvkuota());
                        intent.putExtra("event_date", event.getEvdate());
                        intent.putExtra("event_location", event.getEvloc());
                        intent.putExtra("event_pic_uri", Uri.parse(imagePath).toString());
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout evlay;
        public CardView evlay,cardev;
        TextView eventtitle, eventcategory, eventdet, evtitle, evdet, evtitle2;
        ImageView eventpic, evpic;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventtitle = itemView.findViewById(R.id.eventtitle);
            evtitle = itemView.findViewById(R.id.evTitle);
            evdet = itemView.findViewById(R.id.evDet);
            eventcategory = itemView.findViewById(R.id.eventcategory);
            eventdet = itemView.findViewById(R.id.eventdet);
            eventpic = itemView.findViewById(R.id.imgpost);
            evpic = itemView.findViewById(R.id.imgEv);
            evtitle2 = itemView.findViewById(R.id.evTitle2);

            evlay = (CardView) itemView.findViewById(R.id.eventparent);
            cardev = (CardView) itemView.findViewById(R.id.cardEv);
        }
    }
    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = context.getFilesDir();
        File file = new File(directory, "event_image.png");

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveImageToInternalStorage2(Bitmap bitmap) {
        // Save the image as a file inside internal storage
        File directory = context.getFilesDir();
        File file = new File(directory, "event_image_" + System.currentTimeMillis() + ".png");

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Save as PNG or JPG as preferred
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void setEventImage(Event.EventPic eventPic, ImageView imageView) {
        if (eventPic != null && eventPic.getData() != null) {
            byte[] imageData = new byte[eventPic.getData().size()];
            for (int i = 0; i < eventPic.getData().size(); i++) {
                imageData[i] = eventPic.getData().get(i).byteValue();
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.addimage)); // Default image
        }
    }

}
