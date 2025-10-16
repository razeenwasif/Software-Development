package com.example.comp2100miniproject.reactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100miniproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter that renders reaction type/count pairs. All implementation details were
 * created specifically for this project.
 * @author u7283652
 */
public class ReactionSummaryAdapter extends RecyclerView.Adapter<ReactionSummaryAdapter.ViewHolder> {

    /** Backing list for the reaction summaries. */
    private final List<ReactionSummary> summaries = new ArrayList<>();

    /**
     * Replaces the current dataset with the supplied list and refreshes the view.
     *
     * @param data new data to display (may be {@code null})
     */
    public void submitData(List<ReactionSummary> data) {
        summaries.clear();
        if (data != null) {
            summaries.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reaction_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(summaries.get(position));
    }

    @Override
    public int getItemCount() {
        return summaries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView emojiView;
        private final TextView nameView;
        private final TextView countView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiView = itemView.findViewById(R.id.textEmoji);
            nameView = itemView.findViewById(R.id.textReactionName);
            countView = itemView.findViewById(R.id.textReactionCount);
        }

        /**
         * Binds the summary information to the row views.
         *
         * @param summary reaction summary to render
         */
        void bind(ReactionSummary summary) {
            emojiView.setText(summary.type().getEmoji());
            nameView.setText(summary.type().name().replace('_', ' '));
            countView.setText(String.valueOf(summary.count()));
        }
    }
}
