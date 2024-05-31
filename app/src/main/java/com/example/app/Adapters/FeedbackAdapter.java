package com.example.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.Models.Feedback;
import com.example.app.R;
import com.example.app.databinding.ItemFeedbackBinding;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    ArrayList<Feedback> feedbacks;

    Listeners.OpenImageListenerFactory factory;

    public FeedbackAdapter(Listeners.OpenImageListenerFactory factory) {
        feedbacks = Database.getInstance().getFeedbacks();
        this.factory = factory;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedbackViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        holder.bind(feedbacks.get(position));
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private final ItemFeedbackBinding binding;

        private FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFeedbackBinding.bind(itemView);
        }


        public void bind(Feedback feedback) {
            Database db = Database.getInstance();

            binding.rateFeedback.setText(String.valueOf(feedback.getRate()));

            binding.textFeedback.setText(feedback.getFeedback());

            if (feedback.getImages() == null)
                binding.recView.setVisibility(View.GONE);
            else {
                ArrayList<String> images = new ArrayList<>(feedback.getImages());
                if (images.isEmpty())
                    db.getImages().forEach(uri -> images.add(uri.toString()));

                ImageFeedbackAdapter adapter = new ImageFeedbackAdapter(images, factory);

                LinearLayoutManager layoutManager = new LinearLayoutManager(binding
                        .getRoot().getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.recView.setLayoutManager(layoutManager);

                binding.recView.setAdapter(adapter);
            }
        }

    }


}
