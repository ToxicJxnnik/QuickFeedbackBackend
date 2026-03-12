package com.quickfeedback.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quickfeedback.app.R;
import com.quickfeedback.app.model.Feedback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<Feedback> feedbackList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Feedback feedback);
    }

    public FeedbackAdapter(List<Feedback> feedbackList, OnItemClickListener listener) {
        this.feedbackList = feedbackList;
        this.listener = listener;
    }

    public void updateData(List<Feedback> newList) {
        this.feedbackList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.tvName.setText(feedback.getName());
        holder.tvEmail.setText(feedback.getEmail());
        holder.tvMessage.setText(feedback.getMessage());
        holder.tvDate.setText(formatDate(feedback.getCreatedAt()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(feedback));
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        // Strip trailing Z or timezone offset before parsing
        String normalized = isoDate.endsWith("Z") ? isoDate : isoDate + "Z";
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSX",
                "yyyy-MM-dd'T'HH:mm:ss.SX",
                "yyyy-MM-dd'T'HH:mm:ssX"
        };
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy  HH:mm", Locale.US);
        for (String fmt : formats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(fmt, Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(normalized);
                if (date != null) return outputFormat.format(date);
            } catch (ParseException ignored) {
            }
        }
        return isoDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvMessage, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
