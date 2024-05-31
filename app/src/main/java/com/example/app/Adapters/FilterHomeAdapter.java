package com.example.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.Utils.Listeners;
import com.example.app.R;
import com.example.app.databinding.FilterCheckBinding;

import java.util.ArrayList;

public class FilterHomeAdapter extends RecyclerView.Adapter<FilterHomeAdapter.FilterHomeHolder> {
    private final ArrayList<ItemFilter> filters;
    private Listeners.OnFilterChangeStateListener listener;

    public FilterHomeAdapter(ArrayList<ItemFilter> filters) {
        this.filters = filters;
    }

    public void setListener(Listeners.OnFilterChangeStateListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public FilterHomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterHomeHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_check, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHomeHolder holder, int position) {
        holder.bind(filters.get(position));
    }


    @Override
    public int getItemCount() {
        return filters.size();
    }


    public class FilterHomeHolder extends RecyclerView.ViewHolder {

        FilterCheckBinding binding;

        private FilterHomeHolder(@NonNull View itemView) {
            super(itemView);
            binding = FilterCheckBinding.bind(itemView);
        }


        public void bind(ItemFilter filter) {
            binding.getRoot().setTag(new CheckInfo(
                    Listeners.OnFilterChangeStateListener.TYPE.ITEM,
                    filter.getName()));

            binding.getRoot().setText(filter.getName());

            binding.getRoot().setOnCheckedChangeListener(listener);
            binding.getRoot().setChecked(filter.isChecked());
        }

    }
}
