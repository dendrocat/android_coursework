package com.example.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.Models.Product;
import com.example.app.R;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.ItemProductHomeBinding;

import java.util.ArrayList;


public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.ProductHomeHolder> {

    LiveData<ArrayList<Product>> products;

    public ProductHomeAdapter() {
        this.products = Database.getInstance().getLiveResProducts();
    }


    @NonNull
    @Override
    public ProductHomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ProductHomeHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHomeHolder holder, int position) {
        if (products.getValue() == null) return;
        holder.bind(products.getValue().get(position));
    }

    @Override
    public int getItemCount() {
        return products.getValue() == null ? 0 : products.getValue().size();
    }

    public static class ProductHomeHolder extends RecyclerView.ViewHolder {

        ItemProductHomeBinding binding;

        public ProductHomeHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductHomeBinding.bind(itemView);
        }

        public void bind(Product p) {
            binding.nameProduct.setText(p.getName());
            binding.priceProduct.setText(p.convertPriceToString());

            Database db = Database.getInstance();
            if (p.getImages().isEmpty())
                if (db.getImages().isEmpty())
                    binding.imageProduct.setImageResource(R.drawable.placeholder);
                else Database.loadImage(db.getImages().get(0).toString(), binding.imageProduct);
            else Database.loadImage(p.getImage(0), binding.imageProduct);

            binding.getRoot().setOnClickListener(new Listeners.OnProductListener(
                    Navigator.TypeAct.MAIN, p.getID()));
        }

        public static ProductHomeHolder create(ViewGroup parent) {
            return new ProductHomeHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_home, parent, false));

        }
    }
}
