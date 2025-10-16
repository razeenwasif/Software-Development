package com.example.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.TimestampFormatter;
import com.example.comp2100miniproject.dao.model.TimestampFormatterTimeSinceEnglish;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.reactions.ReactionDAO;
import com.example.comp2100miniproject.reactions.ReactionType;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Map;

import censor.CensorFacade;
import censor.CensorType;

/** RecyclerView adapter displaying message content along with a condensed reaction summary. */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
  private final ArrayList<Message> localDataSet;
  private OnClickListener onClickListener;

  /** Formats human-readable timestamps for message rows. */
  private final TimestampFormatter timestampFormatter;

  private final CensorFacade censorFacade;

  /**
   * Creates an adapter backed by the provided dataset.
   *
   * @param dataSet messages to display
   */
  public MessageAdapter(@NonNull ArrayList<Message> dataSet) {
    localDataSet = dataSet;
    this.timestampFormatter = new TimestampFormatterTimeSinceEnglish();
    this.censorFacade = new CensorFacade();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final Chip textViewAuthor;
    private final TextView textViewContent;
    private final TextView textViewTimestamp;
    private final TextView textViewReactionSummary;
    private final View buttonViewReactions;

    /** Creates a view holder that references row widgets. */
    public ViewHolder(@NonNull View view) {
      super(view);
      textViewAuthor = view.findViewById(R.id.textViewAuthor);
      textViewContent = view.findViewById(R.id.textViewContent);
      textViewTimestamp = view.findViewById(R.id.textViewTimestamp);
      textViewReactionSummary = view.findViewById(R.id.textViewReactionSummary);
      buttonViewReactions = view.findViewById(R.id.buttonViewReactions);
    }

    /**
     * Binds the supplied message to the row views while applying censoring and formatting.
     *
     * @param message message to display
     * @param formatter timestamp formatter used for the footer
     * @param censorFacade censor used for sanitising the message body
     */
    public void display(
        @NonNull Message message,
        @NonNull TimestampFormatter formatter,
        @NonNull CensorFacade censorFacade) {
      User messageAuthor = UserDAO.getInstance().getByUUID(message.poster());
      if (messageAuthor != null) {
        textViewAuthor.setText(messageAuthor.username());
      } else {
        textViewAuthor.setText(itemView.getContext().getString(R.string.unknown_user));
      }
      String content =
          message.message() == null
              ? ""
              : censorFacade.censorMessage(message.message(), CensorType.ALGORITHM);
      textViewContent.setText(content);
      textViewTimestamp.setText(formatter.format(message.timestamp()));

      Map<ReactionType, Long> counts = ReactionDAO.getInstance().getReactionCounts(message);
      if (counts.isEmpty()) {
        textViewReactionSummary.setVisibility(View.GONE);
      } else {
        textViewReactionSummary.setVisibility(View.VISIBLE);
        StringBuilder summaryBuilder = new StringBuilder();
        counts.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(3)
            .forEach(
                entry -> {
                  if (summaryBuilder.length() > 0) {
                    summaryBuilder.append(" • ");
                  }
                  summaryBuilder
                      .append(entry.getKey().getEmoji())
                      .append(' ')
                      .append(entry.getValue());
                });
        textViewReactionSummary.setText(summaryBuilder.toString());
      }
    }

    /** @return the button that triggers the detailed reactions screen */
    public View getButtonViewReactions() {
      return buttonViewReactions;
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view =
        LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.fragment_message, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
    Message message = localDataSet.get(position);
    viewHolder.display(message, this.timestampFormatter, this.censorFacade);

    if (onClickListener != null) {
      View.OnClickListener listener = v -> onClickListener.onClick(position, message);
      viewHolder.itemView.setOnClickListener(listener);
      viewHolder.getButtonViewReactions().setOnClickListener(listener);
    } else {
      viewHolder.itemView.setOnClickListener(null);
      viewHolder.getButtonViewReactions().setOnClickListener(null);
    }
  }

  @Override
  public int getItemCount() {
    return localDataSet.size();
  }

  /**
   * Registers a listener invoked when a message row or its reaction button is tapped.
   *
   * @param listener callback to notify
   */
  public void setOnClickListener(OnClickListener listener) {
    this.onClickListener = listener;
  }

  /** Listener notified when a message row is tapped. */
  public interface OnClickListener {
    void onClick(int i, @NonNull Message message);
  }
}
