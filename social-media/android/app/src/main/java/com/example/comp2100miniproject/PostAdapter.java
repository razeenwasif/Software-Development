package com.example.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.dao.UserDAO;
import com.example.comp2100miniproject.dao.model.Post;
import com.example.comp2100miniproject.dao.model.User;
import com.example.comp2100miniproject.dao.model.Message;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** RecyclerView adapter backing the post list in the main feed. */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int VIEW_TYPE_HEADER = 0;
  private static final int VIEW_TYPE_POST = 1;

  private final ArrayList<Post> posts;
  private final OnPostClickListener listener;
  private View headerView;

  public interface OnPostClickListener {
    void onPostClick(Post post);
  }

  public PostAdapter(ArrayList<Post> posts, OnPostClickListener listener) {
    this.posts = posts;
    this.listener = listener;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_HEADER) {
      headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_header, parent, false);
      return new HeaderViewHolder(headerView);
    } else {
      View view =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
      return new PostViewHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == VIEW_TYPE_POST) {
      Post post = posts.get(position - 1);
      PostViewHolder postViewHolder = (PostViewHolder) holder;
      postViewHolder.textViewPostTopic.setText(post.topic);
      postViewHolder.chipTag.setText(post.tag);

      User postAuthor = UserDAO.getInstance().getByUUID(post.poster);
      String authorName =
          postAuthor != null
              ? postAuthor.username()
              : postViewHolder.itemView.getContext().getString(R.string.unknown_author);
      postViewHolder.authorChip.setText(authorName);

      String initial =
          authorName.trim().isEmpty()
              ? "•"
              : String.valueOf(Character.toUpperCase(authorName.trim().charAt(0)));
      postViewHolder.avatarInitial.setText(initial);
      postViewHolder.avatarContainer.setContentDescription(
          postViewHolder.itemView
              .getContext()
              .getString(R.string.post_author_avatar_description, authorName));

      int replyCount = 0;
      Iterator<Message> replies = post.messages.getAll();
      while (replies.hasNext()) {
        replies.next();
        replyCount++;
      }
      if (replyCount == 0) {
        postViewHolder.responseSummary.setText(R.string.post_meta_no_responses);
      } else {
        postViewHolder.responseSummary.setText(
            postViewHolder.itemView
                .getResources()
                .getQuantityString(R.plurals.post_meta_responses, replyCount, replyCount));
      }

      postViewHolder.itemView.setOnClickListener(
          v -> {
            if (listener != null) {
              listener.onPostClick(post);
            }
          });
    }
  }

  @Override
  public int getItemCount() {
    return posts.size() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_TYPE_HEADER;
    } else {
      return VIEW_TYPE_POST;
    }
  }

  /** Replaces the adapter's backing list and refreshes the feed. */
  public void submitPosts(List<Post> newPosts) {
    posts.clear();
    if (newPosts != null) {
      posts.addAll(newPosts);
    }
    notifyDataSetChanged();
  }

  public View getHeaderView() {
    return headerView;
  }

  public static class PostViewHolder extends RecyclerView.ViewHolder {
    final TextView textViewPostTopic;
    final Chip authorChip;
    final Chip chipTag;
    final TextView responseSummary;
    final TextView avatarInitial;
    final View avatarContainer;

    public PostViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewPostTopic = itemView.findViewById(R.id.textViewPostTopic);
      authorChip = itemView.findViewById(R.id.chipAuthor);
      chipTag = itemView.findViewById(R.id.chipTag);
      responseSummary = itemView.findViewById(R.id.textViewResponseCount);
      avatarInitial = itemView.findViewById(R.id.textViewAvatarInitial);
      avatarContainer = itemView.findViewById(R.id.postAvatarContainer);
    }
  }

  public static class HeaderViewHolder extends RecyclerView.ViewHolder {
    public HeaderViewHolder(@NonNull View itemView) {
      super(itemView);
    }
  }
}
