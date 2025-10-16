package com.example.comp2100miniproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

/** Activity that lets users search for other members by username and navigate to their posts.
 * @author u7283652
 */
public class UserSearchActivity extends AppCompatActivity {

  public static final String EXTRA_USER_ID = "extra_user_id";
  public static final String EXTRA_USERNAME = "extra_username";

  private static final String PREFS_NAME = "user_search_prefs";
  private static final String KEY_LAST_QUERY = "last_query";

  private TextInputEditText searchInput;
  private TextView emptyStateView;
  private UserSearchAdapter adapter;
  private SharedPreferences preferences;

  /** Configures the search UI and wires listeners for live filtering. */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_user_search);

    preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    ConstraintLayout postCreateMainLayout = findViewById(R.id.user_search_root);
    ViewCompat.setOnApplyWindowInsetsListener(postCreateMainLayout, (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    MaterialToolbar toolbar = findViewById(R.id.userSearchToolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    toolbar.setNavigationOnClickListener(v -> finish());

    searchInput = findViewById(R.id.editTextSearch);
    emptyStateView = findViewById(R.id.textNoUsers);
    RecyclerView recyclerView = findViewById(R.id.recyclerViewUsers);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    adapter = new UserSearchAdapter();
    adapter.setOnUserClickListener(this::openUserPosts);
    recyclerView.setAdapter(adapter);

    searchInput.setOnEditorActionListener(
        (v, actionId, event) -> {
          if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch(searchInput.getText());
            return true;
          }
          return false;
        });

    searchInput.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            performSearch(s);
          }

          @Override
          public void afterTextChanged(Editable s) {}
        });

    String lastQuery = preferences.getString(KEY_LAST_QUERY, "");
    if (!lastQuery.isEmpty()) {
      searchInput.setText(lastQuery);
      searchInput.setSelection(lastQuery.length());
    } else {
      performSearch("");
    }
  }

  /** Filters the user list based on the provided search term and updates the RecyclerView. */
  private void performSearch(CharSequence query) {
    String trimmed = query == null ? "" : query.toString().trim();
    List<User> results;
    if (trimmed.isEmpty()) {
      results = new ArrayList<>();
    } else {
      results = UserDAO.getInstance().searchByUsername(trimmed);
    }

    emptyStateView.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
    adapter.submitUsers(results);
  }

  /** Navigates to the UserPostsActivity for the selected user. */
  private void openUserPosts(User user) {
    if (user == null || user.getUUID() == null) {
      return;
    }
    startActivity(
        new android.content.Intent(this, UserPostsActivity.class)
            .putExtra(EXTRA_USER_ID, user.getUUID().toString())
            .putExtra(EXTRA_USERNAME, user.username()));
  }

  @Override
  protected void onPause() {
    super.onPause();
    CharSequence text = searchInput.getText();
    preferences.edit().putString(KEY_LAST_QUERY, text == null ? "" : text.toString()).apply();
  }
}
