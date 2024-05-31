package com.example.app.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.R;
import com.example.app.databinding.ItemImageBinding;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    final ArrayList<Uri> uris;

    Listeners.OpenImageListenerFactory factory;


    public ImagesAdapter(RecyclerView recView,
                         Listeners.OpenImageListenerFactory factory) {
        uris = Database.getInstance().getImages();
        new ItemTouchHelper(new DeleteImage()).attachToRecyclerView(recView);
        this.factory = factory;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(uris.get(position));
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }


    public class DeleteImage extends ItemTouchHelper.SimpleCallback {
        public DeleteImage() {
            super(0, ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int index = viewHolder.getAdapterPosition();
            uris.remove(index);
            ImagesAdapter.this.notifyItemRemoved(index);
        }
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemImageBinding binding;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageBinding.bind(itemView);
        }


        public void bind(Uri uri) {
            Database.loadImage(uri.toString(), binding.image);
            binding.image.setOnClickListener(factory.newListener(uri.toString()));
        }


    }


}
