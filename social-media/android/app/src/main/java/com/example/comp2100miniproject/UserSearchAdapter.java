package com.example.comp2100miniproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.comp2100miniproject.dao.model.User;
import java.util.ArrayList;
import java.util.List;

/** RecyclerView adapter that lists users matching a search term.
 * @author u7283652
 */
public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

  /** Click listener invoked when the row or button is pressed. */
  public interface OnUserClickListener {
    void onUserSelected(@NonNull User user);
  }

  private final List<User> users = new ArrayList<>();
  private OnUserClickListener listener;

  /** Updates the data set displayed by the adapter. */
  public void submitUsers(List<User> newUsers) {
    users.clear();
    if (newUsers != null) {
      users.addAll(newUsers);
    }
    notifyDataSetChanged();
  }

  /** Assigns a listener to be notified when a user is chosen. */
  public void setOnUserClickListener(OnUserClickListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user_search_result, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(users.get(position));
  }

  @Override
  public int getItemCount() {
    return users.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView usernameView;
    private final TextView roleView;
    private final View viewPostsButton;

    ViewHolder(@NonNull View itemView) {
      super(itemView);
      usernameView = itemView.findViewById(R.id.textUsername);
      roleView = itemView.findViewById(R.id.textUserRole);
      viewPostsButton = itemView.findViewById(R.id.buttonViewPosts);
    }

    /** Binds the given user to the row views. */
    void bind(User user) {
      usernameView.setText(user.username());
      roleView.setText(user.role().name());
      View.OnClickListener clickListener =
          v -> {
            if (listener != null) {
              listener.onUserSelected(user);
            }
          };
      itemView.setOnClickListener(clickListener);
      viewPostsButton.setOnClickListener(clickListener);
    }
  }
}
