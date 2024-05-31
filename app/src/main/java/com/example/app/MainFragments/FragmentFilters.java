package com.example.app.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.FilterHomeAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentFiltersBinding;

public class FragmentFilters extends Fragment {

    FragmentFiltersBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFiltersBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Navigator nav = Navigator.getInstance();
        Database db = Database.getInstance();

        binding.btnClose.setOnClickListener(v -> nav.popBackStack());

        binding.confirmFilter.setOnClickListener(v -> {
            Integer minPrice = getInteger(binding.minPrice);
            Integer maxPrice = getInteger(binding.maxPrice);
            Integer minAge = getInteger(binding.minAge);
            db.filterResProducts(minPrice, maxPrice, minAge);
            nav.popBackStack();
        });

        Listeners.OnFilterChangeStateListener categoryListener = new Listeners.OnFilterChangeStateListener(
                binding.checkAllCategory,
                binding.categoryFilters,
                db.getCategories()
        );
        FilterHomeAdapter categoryAdapter = new FilterHomeAdapter(db.getCategories());
        categoryAdapter.setListener(categoryListener);
        binding.categoryFilters.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.categoryFilters.setAdapter(categoryAdapter);

        binding.checkAllCategory.setTag(new CheckInfo(
                Listeners.OnFilterChangeStateListener.TYPE.TITLE,
                binding.checkAllCategory.getText().toString()));
        binding.checkAllCategory.setOnCheckedChangeListener(categoryListener);

        Listeners.OnFilterChangeStateListener brandListener = new Listeners.OnFilterChangeStateListener(
                binding.checkAllBrand,
                binding.brandFilters,
                db.getBrands()
        );
        FilterHomeAdapter brandAdapter = new FilterHomeAdapter(db.getBrands());
        brandAdapter.setListener(brandListener);
        binding.brandFilters.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.brandFilters.setAdapter(brandAdapter);

        binding.checkAllBrand.setTag(new CheckInfo(
                Listeners.OnFilterChangeStateListener.TYPE.TITLE,
                binding.checkAllBrand.getText().toString()));
        binding.checkAllBrand.setOnCheckedChangeListener(brandListener);

        setCurrFilters();
    }

    private Integer getInteger(EditText text) {
        String newInt = text.getText().toString();
        if (newInt.isEmpty()) return null;
        return Integer.parseInt(newInt);
    }

    private void setCurrFilters() {
        Integer[] currFilters = Database.getInstance().getCurrentFilters();
        EditText[] editTexts = new EditText[]{binding.minPrice,
                binding.maxPrice,
                binding.minAge};
        for (int i = 0; i < currFilters.length; ++i)
            editTexts[i].setText(currFilters[i] == null ? "" : currFilters[i].toString());
    }
}