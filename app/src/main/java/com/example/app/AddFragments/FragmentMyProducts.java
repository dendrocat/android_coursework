package com.example.app.AddFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.MyProductsAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.R;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentMyProductsBinding;

import java.util.ArrayList;

public class FragmentMyProducts extends Fragment {

    FragmentMyProductsBinding binding;

    MyProductsAdapter adapter;

    Database db;

    private void delAllChecked() {
        ArrayList<ItemProduct> delProds = new ArrayList<>();
        for (ItemProduct item : db.getUserProducts())
            if (item.isChecked()) delProds.add(item);

        if (delProds.size() == db.getUserProducts().size())
            binding.placeholderEmpty.setVisibility(View.VISIBLE);

        delProds.forEach(item -> db.deleteProduct(item.ProdID()));

        binding.recView.post(() -> adapter.notifyDataSetChanged());
        binding.checkAll.setChecked(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = Database.getInstance();

        binding = FragmentMyProductsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (db.getUserProducts().isEmpty()) {
            binding.placeholderEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.placeholderEmpty.setVisibility(View.GONE);

            adapter = new MyProductsAdapter();
            binding.recView.setLayoutManager(new LinearLayoutManager(requireContext()));

            Listeners.ChecksListener listener = new Listeners.ChecksListener(binding.checkAll,
                    binding.recView, binding.countCheck, Listeners.ChecksListener.TYPE.COUNT);

            binding.checkAll.setTag(new CheckInfo(
                    Listeners.OnFilterChangeStateListener.TYPE.TITLE,
                    binding.checkAll.getText().toString()));
            binding.checkAll.setOnCheckedChangeListener(listener);
            adapter.setListener(listener);

            binding.recView.setAdapter(adapter);

            binding.delChecked.setOnClickListener(l -> delAllChecked());
        }

        binding.countCheck.setText("Выбрано: " + Listeners.getCount());

        binding.addProduct.setOnClickListener(l -> {
            if (Boolean.TRUE.equals(db.getLiveLoading().getValue()))
                Listeners.failMsg(binding.getRoot(),
                        getString(R.string.product_info_loading_error));
            else Navigator.getInstance()
                    .navigateTo(R.id.action_fragmentMyProducts_to_fragmentProductInfo);
        });


        binding.btnBack.setOnClickListener(l -> exit());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exit();
                    }
                }
        );
    }


    private void exit() {
        if (Boolean.TRUE.equals(db.getLiveLoading().getValue()))
            Listeners.failMsg(binding.getRoot(),
                    getString(R.string.product_info_loading_error));
        else requireActivity().finish();
    }

}