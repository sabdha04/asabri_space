package com.example.project_hc002;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentPostAdapter extends RecyclerView.Adapter<CommentPostAdapter.compholder> {

    private List<CommentPost> commentPosts;
    Context context;

    public CommentPostAdapter(List<CommentPost> commentPosts, Context context) {
        this.commentPosts = commentPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public compholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //View view = layoutInflater.inflate(R.layout.item_comments, null);
        View view = layoutInflater.inflate(R.layout.item_commentpost, parent, false);
        return new compholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull compholder holder, int position) {
//        Comment comment = commentList.get(position);
//        holder.comlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), EventComments.class);
//                view.getContext().startActivity(intent);
//            }
//        });
        CommentPost commentPost = commentPosts.get(position);
        holder.user.setText(commentPost.getUser());
        holder.commentp.setText(commentPost.getPcomment());
        holder.pfp.setImageResource(commentPost.getPfp());
        holder.datetime.setText(commentPost.getCreatedat());
//        holder.repcom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), )
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return commentPosts.size();
    }

    public class compholder extends RecyclerView.ViewHolder {
        public ConstraintLayout cpost;
        TextView user, commentp, repcom, datetime, commentcount;
        ImageView pfp;
        public compholder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            commentp = itemView.findViewById(R.id.commentp);
            pfp = itemView.findViewById(R.id.pfpuser);
            datetime = itemView.findViewById(R.id.datetime);
            repcom = itemView.findViewById(R.id.repcomment);
            commentcount = itemView.findViewById(R.id.commentcount);
            //cpost = (ConstraintLayout) itemView.findViewById(R.id.itemcommentp);
        }
    }

    public void addComment(CommentPost comment) {
        commentPosts.add(comment);
        notifyItemInserted(commentPosts.size() - 1);
    }
}
