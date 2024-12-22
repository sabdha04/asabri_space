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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.commentholder>{

    private List<Comment> commentList;
    Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public commentholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View view = layoutInflater.inflate(R.layout.item_comments, null);
        View view = layoutInflater.inflate(R.layout.item_comments, parent, false);
        return new commentholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentholder holder, int position) {
//        Community com= listCom.get(position);
//        holder.comname.setText(com.getComname());
//        holder.comdetail.setText(com.getComdetail());
//        holder.imgcom.setImageDrawable(context.getResources().getDrawable(com.imgcom));
//        holder.mainlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it = new Intent(view.getContext(), CommunityPage.class);
//                view.getContext().startActivity(it);
//            }
//        });
        Comment comment = commentList.get(position);
        holder.txtcomment.setText(comment.getEvcomment());
        holder.txtuser.setText(comment.getUser());
        holder.pfpuser.setImageDrawable(context.getResources().getDrawable(comment.getPfp()));
//        holder.comlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), EventComments.class);
//                view.getContext().startActivity(intent);
//            }
//        });
        holder.repcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EventComments.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class commentholder extends RecyclerView.ViewHolder{
        //public ConstraintLayout comlayout;
        TextView txtcomment, txtuser, repcom;
        ImageView pfpuser;

        public commentholder(@NonNull View itemView) {
            super(itemView);

            txtcomment = itemView.findViewById(R.id.posting);
            txtuser = itemView.findViewById(R.id.username);
            pfpuser = itemView.findViewById(R.id.pfpuser);
            repcom = itemView.findViewById(R.id.repcomment);

//            comlayout = (ConstraintLayout) itemView.findViewById(R.id.itemcomment);
        }
    }
}
