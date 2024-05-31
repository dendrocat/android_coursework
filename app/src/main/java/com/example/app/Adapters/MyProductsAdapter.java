package com.example.app.Adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Utils.Listeners;
import com.example.app.ViewHolders.MyProductViewHolder;

import java.util.ArrayList;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductViewHolder> {

    private final ArrayList<ItemProduct> userProducts;

    private Listeners.ChecksListener listener;

    public MyProductsAdapter() {
        userProducts = Database.getInstance().getUserProducts();
    }


    public void setListener(Listeners.ChecksListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyProductViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductViewHolder holder, int position) {
        holder.bind(userProducts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return userProducts.size();
    }


}
