package com.example.comp2100miniproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.persistentdata.DataManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Displays the list of posts and exposes navigation plus theme controls. */
public class MainActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener {

  private SharedPreferences appPreferences;
  private final ArrayList<Post> allPosts = new ArrayList<>();
  private PostAdapter adapter;
  private RecyclerView recyclerViewPosts;
  private ChipGroup heroChips;
  private Chip chipTrending;
  private Chip chipPatterns;
  private Chip chipLive;
  private FeedCategory pendingCategory = FeedCategory.ALL;
  private ActivityResultLauncher<Intent> createPostLauncher;

  private enum FeedCategory {
    ALL,
    TRENDING,
    PATTERNS,
    LIVE
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);

    appPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);

    // Create ActivityResultLauncher to handle refreshing of data after creating new post
    createPostLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), o -> refreshPostsList());

    DrawerLayout drawerLayout = findViewById(R.id.mainDrawerLayout);
    ConstraintLayout mainContainer = findViewById(R.id.main);
    ViewCompat.setOnApplyWindowInsetsListener(
        mainContainer,
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    MaterialToolbar toolbar = findViewById(R.id.mainToolbar);
    setSupportActionBar(toolbar);

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_open_drawer,
            R.string.navigation_close_drawer);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.mainNavigationView);
    navigationView.setNavigationItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.menuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
          } else if (item.getItemId() == R.id.menuLogout) {
            finish();
          }
          drawerLayout.closeDrawers();
          return true;
        });

    View headerView = navigationView.getHeaderView(0);
    TextView usernameTextView = headerView.findViewById(R.id.usernameTextView);
    String username = appPreferences.getString("TestUsername", "Guest");
    usernameTextView.setText(username);

    drawerLayout.setScrimColor(Color.parseColor("#E6000000"));

    DataManager dataManager = DataManager.getInstance();
    dataManager.initialise(getApplicationContext());

    recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
    recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

    adapter = new PostAdapter(new ArrayList<>(allPosts), this);
    recyclerViewPosts.setAdapter(adapter);

    recyclerViewPosts.post(
        () -> {
          View postHeaderView = adapter.getHeaderView();
          if (postHeaderView != null) {
            MaterialButton exploreButton = postHeaderView.findViewById(R.id.buttonExplore);
            exploreButton.setOnClickListener(
                v -> {
                  if (pendingCategory == FeedCategory.ALL) {
                    Toast.makeText(this, R.string.main_select_tag_hint, Toast.LENGTH_SHORT).show();
                    showAllPosts();
                  } else {
                    runFilter(pendingCategory);
                    if (recyclerViewPosts != null) {
                      recyclerViewPosts.smoothScrollToPosition(0);
                    }
                  }
                });

            heroChips = postHeaderView.findViewById(R.id.heroChips);
            chipTrending = postHeaderView.findViewById(R.id.chipTrending);
            chipPatterns = postHeaderView.findViewById(R.id.chipPatterns);
            chipLive = postHeaderView.findViewById(R.id.chipLive);
            setupFilterChips();
          }
        });
  }

  @Override
  protected void onResume() {
    super.onResume();
    DataManager.getInstance().readAll();
    refreshPostsList();
  }

  private void setupFilterChips() {
    if (chipTrending != null) {
      chipTrending.setTag(FeedCategory.TRENDING);
    }
    if (chipPatterns != null) {
      chipPatterns.setTag(FeedCategory.PATTERNS);
    }
    if (chipLive != null) {
      chipLive.setTag(FeedCategory.LIVE);
    }

    if (heroChips != null) {
      heroChips.setSingleSelection(true);
      heroChips.setSelectionRequired(false);
      heroChips.clearCheck();
      heroChips.setOnCheckedStateChangeListener(
          (group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) {
              pendingCategory = FeedCategory.ALL;
              showAllPosts();
              return;
            }
            Chip selectedChip = group.findViewById(checkedIds.get(0));
            if (selectedChip != null && selectedChip.getTag() instanceof FeedCategory) {
              pendingCategory = (FeedCategory) selectedChip.getTag();
            }
          });
    }

    pendingCategory = FeedCategory.ALL;
    showAllPosts();
  }

  /** Filters the posts based on the selected category. */
  private void runFilter(FeedCategory category) {
    if (adapter == null) {
      return;
    }

    List<Post> filtered;
    switch (category) {
      case TRENDING:
        filtered = trendingPosts();
        break;
      case PATTERNS:
        filtered = tagFilter("design patterns");
        break;
      case LIVE:
        filtered = tagFilter("live collabs");
        break;
      case ALL:
      default:
        showAllPosts();
        return;
    }

    if (filtered.isEmpty()) {
      Toast.makeText(this, R.string.no_posts_found, Toast.LENGTH_SHORT).show();
      if (heroChips != null) {
        heroChips.clearCheck();
      }
      pendingCategory = FeedCategory.ALL;
      return;
    }

    adapter.submitPosts(filtered);
  }

  private List<Post> trendingPosts() {
    ArrayList<Post> sorted = new ArrayList<>(allPosts);
    sorted.sort((first, second) -> Integer.compare(replyCount(second), replyCount(first)));
    int limit = Math.min(10, sorted.size());
    return new ArrayList<>(sorted.subList(0, limit));
  }

  private List<Post> tagFilter(String... tags) {
    ArrayList<Post> filtered = new ArrayList<>();
    if (tags == null || tags.length == 0) {
      return filtered;
    }
    Set<String> matchers = new HashSet<>();
    for (String tag : tags) {
      if (tag != null && !tag.trim().isEmpty()) {
        matchers.add(normaliseTag(tag));
      }
    }
    if (matchers.isEmpty()) {
      return filtered;
    }
    for (Post post : allPosts) {
      String postTag = normaliseTag(post.tag);
      if (matchers.contains(postTag)) {
        filtered.add(post);
      }
    }
    return filtered;
  }

  private int replyCount(Post post) {
    if (post == null || post.messages == null) {
      return 0;
    }
    int count = 0;
    Iterator<Message> iterator = post.messages.getAll();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    return count;
  }

  private void showAllPosts() {
    if (adapter == null) {
      return;
    }
    adapter.submitPosts(allPosts);
    if (heroChips == null || heroChips.getCheckedChipIds().isEmpty()) {
      pendingCategory = FeedCategory.ALL;
    }
  }

  private String normaliseTag(String tag) {
    if (tag == null || tag.trim().isEmpty()) {
      return "general";
    }
    return tag.trim().toLowerCase(Locale.ROOT);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);

    MenuItem createPostItem = menu.findItem(R.id.action_create_post);
    if (createPostItem != null) {
      createPostItem.setOnMenuItemClickListener(
          item -> {
            Intent intent = new Intent(this, PostCreateActivity.class);

            // added intent
            Intent login_intent = getIntent();
            String username = login_intent.getStringExtra("saved_username");
            String password = login_intent.getStringExtra("saved_password");

            login_intent.putExtra("saved_username", username);
            login_intent.putExtra("saved_password", password);

            createPostLauncher.launch(intent);
            return true;
          });
    }

    MenuItem searchItem = menu.findItem(R.id.action_search_users);
    if (searchItem != null) {
      searchItem.setOnMenuItemClickListener(
          item -> {
            startActivity(new Intent(this, UserSearchActivity.class));
            return true;
          });
    }
    return true;
  }

  @Override
  public void onPostClick(Post post) {
    Intent intent = new Intent(this, PostViewerActivity.class);
    intent.putExtra("postId", post.id);
    startActivity(intent);
  }

  private void refreshPostsList() {
    allPosts.clear();
    Iterator<Post> postIterator = PostDAO.getInstance().getAll();
    while (postIterator.hasNext()) {
      allPosts.add(postIterator.next());
    }
    showAllPosts();
  }
}
