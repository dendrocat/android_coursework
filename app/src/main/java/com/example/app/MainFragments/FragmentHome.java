package com.example.app.MainFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.app.Adapters.ProductHomeAdapter;
import com.example.app.Databases.Database;
import com.example.app.R;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentHomeBinding;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;

    private ProductHomeAdapter adapter;

    private Database db;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        db = Database.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db.updLiveResProducts();

        adapter = new ProductHomeAdapter();

        binding.productsHome.setAdapter(adapter);
        binding.productsHome.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        db.getLiveResProducts().observe(requireActivity(), arr -> {
            adapter.notifyDataSetChanged();
            if (arr.isEmpty()) {
                binding.placeholderEmpty.setVisibility(View.VISIBLE);
                if (db.hasProducts())
                    binding.placeholderEmpty.setText(R.string.product_home_placeholder_empty_search);
                else
                    binding.placeholderEmpty.setText(R.string.product_home_placeholder_empty);
            } else binding.placeholderEmpty.setVisibility(View.GONE);

        });


        binding.filters.setOnClickListener(v -> Navigator.getInstance()
                .navigateTo(R.id.action_fragmentHome_to_fragmentFilters));

        binding.searchField.setQuery(Database.getInstance().getCurrQuery(), false);
        binding.searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                db.filterQueryProduct(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) db.filterQueryProduct("");
                return false;
            }
        });

        binding.spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                db.sortResProducts(Database.TypeSort.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                db.sortResProducts(Database.TypeSort.RATE);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getContext().getSharedPreferences(Database.prefSignIn, Context.MODE_PRIVATE)
                                .edit().clear().apply();
                        requireActivity().finish();
                    }
                }
        );
    }
}