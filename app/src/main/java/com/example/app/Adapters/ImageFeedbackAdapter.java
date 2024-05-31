package com.example.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.R;
import com.example.app.databinding.ItemImageFeedbackBinding;

import java.util.ArrayList;

public class ImageFeedbackAdapter
        extends RecyclerView.Adapter<ImageFeedbackAdapter.ImageFeedbackViewHolder> {

    ArrayList<String> images;

    Listeners.OpenImageListenerFactory factory;

    public ImageFeedbackAdapter(ArrayList<String> images,
                                Listeners.OpenImageListenerFactory factory) {
        this.images = images;
        this.factory = factory;
    }


    @NonNull
    @Override
    public ImageFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageFeedbackViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFeedbackViewHolder holder, int position) {
        holder.bind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }



    public class ImageFeedbackViewHolder extends RecyclerView.ViewHolder {

        private final ItemImageFeedbackBinding binding;

        public ImageFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageFeedbackBinding.bind(itemView);
        }

        public void bind(String path) {
            Database.loadImage(path, binding.imageFeedback);
            binding.imageFeedback.setOnClickListener(factory.newListener(path));
        }

    }
}
