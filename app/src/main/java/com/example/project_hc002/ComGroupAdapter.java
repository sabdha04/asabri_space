package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComGroupAdapter extends RecyclerView.Adapter<ComGroupAdapter.comgroup>{
    private List<ComGroup> listComGr;
    Context context;

    public ComGroupAdapter(List<ComGroup> listComGr, Context context) {
        this.listComGr = listComGr;
        this.context = context;
    }

    @NonNull
    @Override
    public ComGroupAdapter.comgroup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_com, parent, false);
        return new ComGroupAdapter.comgroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComGroupAdapter.comgroup holder, int position) {
        ComGroup comGroup = listComGr.get(position);
        holder.comname.setText(comGroup.getKomun_name());
        holder.comdesc.setText(comGroup.getDeskripsi());
        holder.comimg.setImageResource(R.drawable.avatar);
        holder.mainlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), CommGroupPage.class);
                it.putExtra("komun_name", comGroup.getKomun_name());
                it.putExtra("komun_desc", comGroup.getDeskripsi());
                it.putExtra("komun_id", comGroup.getKomunitas_id());
                v.getContext().startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listComGr.size();
    }

    public class comgroup extends RecyclerView.ViewHolder {
        TextView comname, comdesc;
        ImageView comimg;
        LinearLayout mainlay;
        public comgroup(@NonNull View itemView) {
            super(itemView);
            comname = itemView.findViewById(R.id.comname);
            comdesc = itemView.findViewById(R.id.comdesc);
            comimg = itemView.findViewById(R.id.imgcom);
            mainlay = itemView.findViewById(R.id.mainLayout);
        }
    }
}
