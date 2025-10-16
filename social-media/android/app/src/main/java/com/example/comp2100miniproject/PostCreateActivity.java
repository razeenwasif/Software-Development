package com.example.comp2100miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.persistentdata.DataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;


public class PostCreateActivity extends AppCompatActivity {

    private SharedPreferences appPreferences;
    private Spinner tagSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_create);

        // Setup layouts and listener
        ConstraintLayout postCreateMainLayout = findViewById(R.id.postCreateMainLayout);
        ViewCompat.setOnApplyWindowInsetsListener(postCreateMainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.postCreateToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Setup tag spinner
        tagSpinner = findViewById(R.id.tagSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(adapter);

        // Setup listener for create post button
        MaterialButton postCreateButton = findViewById(R.id.postCreateButton);
        postCreateButton.setOnClickListener(view -> createPost());
    }

    private void createPost() {


        TextInputEditText postCreateTextInputEditText = findViewById(R.id.postCreateTextInputEditText);
        String topic = postCreateTextInputEditText.getText().toString();
        String tag = tagSpinner.getSelectedItem().toString();

        if (topic.isEmpty()) {
            postCreateTextInputEditText.setError("Post topic is required!");
            return;
        }

        // Create temp user
        String username= appPreferences.getString("TestUsername", "Guest");
        String password= appPreferences.getString("TestPassword", null);
        User user = UserDAO.getInstance().get(new User(username));
        if (user == null) {
            user = new User(UUID.randomUUID(), User.Role.Member, username, password);
            UserDAO.getInstance().add(user);
        }

        // Create new post and save
        Post post = new Post(UUID.randomUUID(), user.getUUID(), topic, tag);
        PostDAO.getInstance().add(post);
        DataManager.getInstance().writeAll();

        // Notification of creating post successfully
        Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show();

        // Return to previous activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back button press
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}