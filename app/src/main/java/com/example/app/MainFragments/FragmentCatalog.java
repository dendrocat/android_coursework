package com.example.app.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.app.Adapters.CatalogAdapter;
import com.example.app.Databases.Database;
import com.example.app.MainActivity;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.R;
import com.example.app.databinding.FragmentCatalogBinding;
import com.example.app.databinding.ItemCatalogBinding;

public class FragmentCatalog extends Fragment {

    FragmentCatalogBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.recView.setAdapter(new CatalogAdapter(new OnCategoryClickListener()));

    }


    public final class OnCategoryClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String name = ItemCatalogBinding.bind((View) v.getParent())
                    .titleCategory.getText().toString();
            for (ItemFilter filter : Database.getInstance().getCategories())
                filter.setChecked(name.equals(filter.getName()));
            ((MainActivity) requireActivity()).navigateBottom(R.id.fragmentHome);
        }
    }


}


