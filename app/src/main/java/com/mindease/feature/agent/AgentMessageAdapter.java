package com.mindease.feature.agent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindease.R;
import com.mindease.domain.model.AgentMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgentMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ASSISTANT = 1;
    private static final int TYPE_USER = 2;
    private final List<AgentMessage> items = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public void submitList(List<AgentMessage> messages) {
        items.clear();
        if (messages != null) {
            items.addAll(messages);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return "user".equals(items.get(position).role) ? TYPE_USER : TYPE_ASSISTANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            return new UserMessageViewHolder(inflater.inflate(R.layout.item_agent_message_user, parent, false));
        }
        return new AssistantMessageViewHolder(inflater.inflate(R.layout.item_agent_message_assistant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AgentMessage message = items.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message, formatTime(message.createdAt));
        } else {
            ((AssistantMessageViewHolder) holder).bind(message, formatMeta(message));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatTime(long createdAt) {
        if (createdAt <= 0L) {
            return "";
        }
        return timeFormat.format(new Date(createdAt));
    }

    private String formatMeta(AgentMessage message) {
        String time = formatTime(message.createdAt);
        String status = message.status == null ? "" : message.status;
        if (status.contains("thinking")) {
            return time.isEmpty() ? "thinking..." : time + "  thinking...";
        }
        if (status.contains("fallback")) {
            return time.isEmpty() ? "fallback reply" : time + "  fallback reply";
        }
        if (status.contains("risk")) {
            return time.isEmpty() ? "safety notice included" : time + "  safety notice included";
        }
        return time.isEmpty() ? "companion reply" : time + "  companion reply";
    }

    private static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageView;
        private final TextView timeView;

        private UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.tv_user_message);
            timeView = itemView.findViewById(R.id.tv_user_message_time);
        }

        private void bind(AgentMessage message, String timeText) {
            messageView.setText(message.text);
            timeView.setText(timeText);
        }
    }

    private static class AssistantMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageView;
        private final TextView metaView;

        private AssistantMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.tv_assistant_message);
            metaView = itemView.findViewById(R.id.tv_assistant_message_meta);
        }

        private void bind(AgentMessage message, String metaText) {
            SimpleMarkdownFormatter.apply(messageView, message.text);
            metaView.setText(metaText);
        }
    }
}
