package com.example.comp2100miniproject;

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
import censor.CensorFacade;
import censor.CensorType;
import com.example.comp2100miniproject.dao.PostDAO;
import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.TimestampFormatter;
import com.example.comp2100miniproject.dao.model.TimestampFormatterTimeSinceEnglish;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.persistentdata.DataManager;
import com.example.comp2100miniproject.reactions.Reaction;
import com.example.comp2100miniproject.reactions.ReactionDAO;
import com.example.comp2100miniproject.reactions.ReactionSummary;
import com.example.comp2100miniproject.reactions.ReactionSummaryAdapter;
import com.example.comp2100miniproject.reactions.ReactionType;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Displays the reactions associated with a specific message using Material components. */
public class MessageReactionsActivity extends AppCompatActivity {

  /** Intent extra that carries the message identifier whose reactions should be displayed. */
  public static final String EXTRA_MESSAGE_ID = "extra_message_id";

  private final TimestampFormatter timestampFormatter = new TimestampFormatterTimeSinceEnglish();
  private final CensorFacade censorFacade = new CensorFacade();

  private TextView messageAuthorView;
  private TextView messageTimestampView;
  private TextView messageContentView;
  private TextView emptyStateView;
  private RecyclerView recyclerView;
  private ReactionSummaryAdapter adapter;
  private Message currentMessage;
  private UUID currentMessageId;
  private MaterialButton addReactionButton;

  /** Initialises the reaction screen, resolves the target message, and loads reaction data. */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_message_reactions);

    ConstraintLayout postCreateMainLayout = findViewById(R.id.reaction_root);
    ViewCompat.setOnApplyWindowInsetsListener(postCreateMainLayout, (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    MaterialToolbar toolbar = findViewById(R.id.reactionToolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    toolbar.setNavigationOnClickListener(v -> finish());

    messageAuthorView = findViewById(R.id.messageAuthor);
    messageTimestampView = findViewById(R.id.messageTimestamp);
    messageContentView = findViewById(R.id.messageContent);
    emptyStateView = findViewById(R.id.emptyState);
    recyclerView = findViewById(R.id.recyclerViewReactions);
    addReactionButton = findViewById(R.id.buttonAddAllReactions);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new ReactionSummaryAdapter();
    recyclerView.setAdapter(adapter);

    String messageIdString = getIntent().getStringExtra(EXTRA_MESSAGE_ID);
    if (messageIdString == null) {
      finish();
      return;
    }

    UUID messageId;
    try {
      messageId = UUID.fromString(messageIdString);
    } catch (IllegalArgumentException e) {
      finish();
      return;
    }

    Message message = PostDAO.getInstance().findMessageById(messageId);
    if (message == null) {
      finish();
      return;
    }

    currentMessage = message;
    currentMessageId = messageId;

    bindMessage(message);
    bindReactions(messageId);

    addReactionButton.setOnClickListener(this::showReactionPicker);
  }

  /**
   * Populates the header card with author, timestamp, and censored content.
   *
   * @param message message selected by the user
   */
  private void bindMessage(Message message) {
    User author = UserDAO.getInstance().getByUUID(message.poster());
    messageAuthorView.setText(
        author != null ? author.username() : getString(R.string.unknown_user));
    messageTimestampView.setText(timestampFormatter.format(message.timestamp()));
    String content =
        message.message() == null
            ? ""
            : censorFacade.censorMessage(message.message(), CensorType.ALGORITHM);
    messageContentView.setText(content);
  }

  /**
   * Loads reaction summaries for the message and updates the list/empty state accordingly.
   *
   * @param messageId identifier of the message whose reactions should be shown
   */
  private void bindReactions(UUID messageId) {
    Map<ReactionType, Long> counts = ReactionDAO.getInstance().getReactionCounts(messageId);
    if (counts.isEmpty()) {
      emptyStateView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      return;
    }
    emptyStateView.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);

    List<ReactionSummary> summaries = new ArrayList<>();
    counts.forEach((type, count) -> summaries.add(new ReactionSummary(type, count)));
    summaries.sort((a, b) -> Long.compare(b.count(), a.count()));
    adapter.submitData(summaries);
  }

  private void showReactionPicker(View anchor) {
    if (currentMessage == null) {
      return;
    }
    CharSequence[] options = new CharSequence[ReactionType.values().length];
    for (int i = 0; i < ReactionType.values().length; i++) {
      ReactionType type = ReactionType.values()[i];
      options[i] = type.getEmoji() + " " + type.name();
    }

    new MaterialAlertDialogBuilder(this)
        .setTitle(R.string.choose_reaction)
        .setItems(options, (dialog, which) -> addReaction(anchor, ReactionType.values()[which]))
        .setNegativeButton(android.R.string.cancel, null)
        .show();
  }

  private void addReaction(View anchor, ReactionType type) {
    User user = UserDAO.getInstance().getByUUID(currentMessage.poster());
    if (user == null) {
      user = UserDAO.getInstance().getRandom();
      if (user == null) {
        Snackbar.make(anchor, R.string.no_users_found, Snackbar.LENGTH_SHORT).show();
        return;
      }
    }

    ReactionDAO reactionDAO = ReactionDAO.getInstance();
    reactionDAO.addReaction(new Reaction(user, currentMessage, type));
    DataManager.getInstance().writeAll();
    bindReactions(currentMessageId);
    Snackbar.make(anchor, getString(R.string.reaction_added, type.getEmoji()), Snackbar.LENGTH_SHORT)
        .show();
  }
}
