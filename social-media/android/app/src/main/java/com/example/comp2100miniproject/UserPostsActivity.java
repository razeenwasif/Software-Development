package com.example.comp2100miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.model.Post;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Displays the posts authored by a specific user with navigation back to the search screen.
 * @author u7283652
 */
public class UserPostsActivity extends AppCompatActivity
    implements PostAdapter.OnPostClickListener {

  private TextView emptyStateView;
  private RecyclerView postsRecyclerView;

  /** Sets up the posts list for the selected user and configures navigation. */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_user_posts);

    ConstraintLayout postCreateMainLayout = findViewById(R.id.user_posts_root);
    ViewCompat.setOnApplyWindowInsetsListener(postCreateMainLayout, (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    MaterialToolbar toolbar = findViewById(R.id.userPostsToolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    toolbar.setNavigationOnClickListener(v -> finish());

    emptyStateView = findViewById(R.id.textNoPosts);
    postsRecyclerView = findViewById(R.id.recyclerViewUserPosts);
    postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    TextView heroTitle = findViewById(R.id.textUserHeroTitle);

    Intent intent = getIntent();
    String userIdString = intent.getStringExtra(UserSearchActivity.EXTRA_USER_ID);
    String username = intent.getStringExtra(UserSearchActivity.EXTRA_USERNAME);

    if (username != null) {
      toolbar.setTitle(getString(R.string.user_posts_title, username));
      heroTitle.setText(getString(R.string.user_posts_headline, username));
    } else {
      heroTitle.setText(R.string.user_posts_subtitle);
    }

    List<Post> userPosts = new ArrayList<>();
    if (userIdString != null) {
      try {
        UUID userId = UUID.fromString(userIdString);
        userPosts = PostDAO.getInstance().getPostsByUser(userId);
      } catch (IllegalArgumentException ignored) {
      }
    }

    emptyStateView.setVisibility(userPosts.isEmpty() ? View.VISIBLE : View.GONE);

    PostAdapter adapter = new PostAdapter(new ArrayList<>(userPosts), this);
    postsRecyclerView.setAdapter(adapter);
  }

  /** Routes the user to the standard post viewer screen for the selected post. */
  @Override
  public void onPostClick(Post post) {
    if (post == null) {
      return;
    }
    Intent intent = new Intent(this, PostViewerActivity.class);
    intent.putExtra("postId", post.getUUID());
    startActivity(intent);
  }
}
