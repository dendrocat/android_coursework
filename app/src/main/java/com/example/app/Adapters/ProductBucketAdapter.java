package com.example.app.Adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Utils.Listeners;
import com.example.app.ViewHolders.ProductBucketHolder;

import java.util.ArrayList;

public class ProductBucketAdapter extends RecyclerView.Adapter<ProductBucketHolder> {


    private final ArrayList<ItemProduct> bucket;

    private Listeners.ChecksListener listener;



    public ProductBucketAdapter() {
        this.bucket = Database.getInstance().getBucket();
    }

    public void setListener(Listeners.ChecksListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductBucketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ProductBucketHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductBucketHolder holder, int position) {
        ItemProduct item = bucket.get(position);
        holder.bind(Database.getInstance().getProductOnID(item.ProdID()),
                item,
                listener);
    }

    @Override
    public int getItemCount() {
        return bucket.size();
    }
}
