package com.example.comp2100miniproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.persistentdata.DataManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/** Presents a single post and its thread of messages. */
public class PostViewerActivity extends AppCompatActivity {

  private MessageAdapter adapter;
  private ArrayList<Message> messages;
  private Post currentPost;
  private EditText editTextComment;
  private UUID postId;
  private SharedPreferences appPreferences;

  /** Loads the post details, binds the replies, and wires the back navigation. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    appPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_post_viewer);

    MaterialToolbar toolbar = findViewById(R.id.postViewerToolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    Intent intent = getIntent();
    Serializable postIdExtra = intent.getSerializableExtra("postId");
    if (postIdExtra instanceof UUID) {
      postId = (UUID) postIdExtra;
    }

    // Initialize UI
    loadPostData();

    // Set up comment input
    editTextComment = findViewById(R.id.editTextComment);
    Button buttonAddComment = findViewById(R.id.buttonAddComment);
    buttonAddComment.setOnClickListener(v -> addComment());

    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Refresh the data when returning to this activity
    refreshPostData();
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  /** Loads post data and initializes the RecyclerView. */
  private void loadPostData() {
    TextView postTitle = findViewById(R.id.textViewPostTitle);
    Chip authorChip = findViewById(R.id.chipPostAuthor);
    RecyclerView recyclerView = findViewById(R.id.recyclerViewReplies);

    Post post = postId != null ? PostDAO.getInstance().getByUUID(postId) : null;
    messages = new ArrayList<>();

    if (post != null) {
      postTitle.setText(post.topic);
      User postAuthor = UserDAO.getInstance().getByUUID(post.poster);
      String authorName =
          postAuthor != null ? postAuthor.username() : getString(R.string.unknown_author);
      authorChip.setText(authorName);
      authorChip.setContentDescription(getString(R.string.post_author_label, authorName));

      Iterator<Message> messageIterator = post.messages.getAll();
      while (messageIterator.hasNext()) {
        messages.add(messageIterator.next());
      }
    } else {
      postTitle.setText(getString(R.string.no_posts_found));
      authorChip.setText("");
      authorChip.setContentDescription(null);
    }

    adapter = new MessageAdapter(messages);
    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    recyclerView.setAdapter(adapter);
    adapter.setOnClickListener(
        (position, message) -> {
          Intent reactionsIntent = new Intent(this, MessageReactionsActivity.class);
          reactionsIntent.putExtra(
              MessageReactionsActivity.EXTRA_MESSAGE_ID, message.id().toString());
          startActivity(reactionsIntent);
        });

    currentPost = post;
  }

  /** Refreshes the post data from the DAO and updates the UI. */
  private void refreshPostData() {
    if (postId == null) return;

    Post post = PostDAO.getInstance().getByUUID(postId);
    if (post == null) return;

    currentPost = post;
    messages.clear();

    Iterator<Message> messageIterator = post.messages.getAll();
    while (messageIterator.hasNext()) {
      messages.add(messageIterator.next());
    }

    adapter.notifyDataSetChanged();
  }

  /** Adds a new comment to the post and refreshes the RecyclerView. */
  private void addComment() {
    String commentText = editTextComment.getText().toString().trim();

    if (commentText.isEmpty()) {
      Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
      return;
    }

    if (currentPost == null) {
      Toast.makeText(this, "Cannot add comment to invalid post", Toast.LENGTH_SHORT).show();
      return;
    }

    // Get any user from UserDAO (simple approach)
    String username = appPreferences.getString("TestUsername", "Guest");
    String password = appPreferences.getString("TestPassword", null);
    User user = UserDAO.getInstance().get(new User(username));
    if (user == null) {
      user = new User(UUID.randomUUID(), User.Role.Member, username, password);
      UserDAO.getInstance().add(user);
    }

    // Create new message
    Message newMessage =
        new Message(
            UUID.randomUUID(),
            user.id(),
            currentPost.getUUID(),
            System.currentTimeMillis(),
            commentText);

    // Add message to post (this modifies the post object in memory)
    currentPost.messages.insert(newMessage);

    // Save to persistent storage
    DataManager.getInstance().writeAll();

    // Update local list and notify adapter
    messages.add(newMessage);
    adapter.notifyItemInserted(messages.size() - 1);

    // Clear input and scroll to bottom
    editTextComment.setText("");
    RecyclerView recyclerView = findViewById(R.id.recyclerViewReplies);
    recyclerView.smoothScrollToPosition(messages.size() - 1);

    // Hide keyboard
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
    }

    Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
  }
}
