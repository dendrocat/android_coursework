package com.example.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Databases.Database;
import com.example.app.MainFragments.FragmentCatalog;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.Utils.Navigator;
import com.example.app.R;
import com.example.app.databinding.ItemCatalogBinding;

import java.util.ArrayList;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    ArrayList<ItemFilter> categories;

    FragmentCatalog.OnCategoryClickListener listener;

    public CatalogAdapter(FragmentCatalog.OnCategoryClickListener listener) {
        this.categories = Database.getInstance().getCategories();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CatalogViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        holder.bind(categories.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }



    public static class CatalogViewHolder extends RecyclerView.ViewHolder {

        ItemCatalogBinding binding;

        private CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCatalogBinding.bind(itemView);
        }

        public static CatalogViewHolder create(ViewGroup parent) {
            return new CatalogViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_catalog,
                                    parent,
                                    false));
        }

        public void bind(ItemFilter filter, FragmentCatalog.OnCategoryClickListener listener) {
            binding.titleCategory.setText(filter.getName());
            Database.loadImage(Database.getInstance().getCategoryImage(filter),
                    binding.imageCategory);

            binding.cardCatalog.setOnClickListener(listener);
        }

    }

}
