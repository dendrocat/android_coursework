package com.example.app.MainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.ProductBucketAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.R;
import com.example.app.Utils.Formatter;
import com.example.app.Utils.Listeners;
import com.example.app.databinding.FragmentBucketBinding;

import java.util.ArrayList;

public class FragmentBucket extends Fragment {

    FragmentBucketBinding binding;
    ProductBucketAdapter adapter;

    Database db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = Database.getInstance();
        binding = FragmentBucketBinding.inflate(inflater);
        return binding.getRoot();
    }

    public void delAllChecks() {
        ArrayList<ItemProduct> delBucket = new ArrayList<>();
        for (ItemProduct item : db.getBucket())
            if (item.isChecked()) delBucket.add(item);

        if (delBucket.size() == db.getBucket().size())
            binding.placeholderEmpty.setVisibility(View.VISIBLE);

        delBucket.forEach(item -> db.deleteFromBucket(item.ProdID()));

        binding.recView.post(() -> adapter.notifyDataSetChanged());
        binding.checkAll.setChecked(false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (db.getBucket().isEmpty()) {
            binding.placeholderEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.placeholderEmpty.setVisibility(View.GONE);

            adapter = new ProductBucketAdapter();
            binding.recView.setLayoutManager(new LinearLayoutManager(requireContext()));

            Listeners.ChecksListener listener = new Listeners.ChecksListener(
                    binding.checkAll, binding.recView,
                    binding.resPayment, Listeners.ChecksListener.TYPE.PRICE);

            binding.checkAll.setTag(new CheckInfo(
                    Listeners.OnFilterChangeStateListener.TYPE.TITLE,
                    binding.checkAll.getText().toString()));
            binding.checkAll.setOnCheckedChangeListener(listener);

            adapter.setListener(listener);

            binding.recView.setAdapter(adapter);

            binding.delChecked.setOnClickListener(v -> delAllChecks());
        }


        binding.buttonPay.setOnClickListener(v -> {
            String price = binding.resPayment.getText().toString();

            if (price.equals(getResources().getString(R.string.example_bucket_price))) {
                Listeners.failMsg(binding.getRoot(),
                        "Оплата невозможна. Выберите товары.");
                return;
            }
            delAllChecks();
            Listeners.failMsg(binding.getRoot(), "Успешно!");
        });
        binding.resPayment.setText("К оплате: " + Formatter.getPriceBucket());
    }


}