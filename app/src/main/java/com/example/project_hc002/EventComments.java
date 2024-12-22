package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventComments extends AppCompatActivity {

    private RecyclerView eventcomment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    EditText commentadd;
    ImageView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_comments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar tb = findViewById(R.id.toolbar_comments);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Comment Section");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventcomment = findViewById(R.id.eventcomment);
        commentList = new ArrayList<>();
        commentList.add(new Comment("Mari ramaikan","chkchk",R.drawable.vector003));
        commentList.add(new Comment("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit. Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit.","asabri_space",R.drawable.vector002));
        commentAdapter = new CommentAdapter(commentList, this);
        eventcomment.setAdapter(commentAdapter);
        eventcomment.setLayoutManager(new LinearLayoutManager(this));

        commentadd = findViewById(R.id.commentadd);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setColorFilter(ContextCompat.getColor(this, R.color.grey));

        // Add text change listener to EditText
        commentadd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Change color of send button based on whether text is entered
                if (charSequence.length() > 0) {
                    sendButton.setColorFilter(ContextCompat.getColor(EventComments.this, R.color.hanblue));
                } else {
                    sendButton.setColorFilter(ContextCompat.getColor(EventComments.this, R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        // Add OnClickListener to the send button
        sendButton.setOnClickListener(v -> {
            // Clear the text when send button is clicked
            commentadd.setText("");
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.comment_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//        if (id == R.id.comments){
//            Intent intent = new Intent(this, EventComments.class);
//            startActivity(intent);
//            return true;
//        }
//        return true;
//    }

}