package com.example.app.AddFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.ImagesAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Product;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentProductInfoBinding;
import com.google.android.material.snackbar.Snackbar;

public class FragmentProductInfo extends Fragment {

    private static final String NAME = "NAME";
    private static final String PRICE = "PRICE";
    private static final String CAT = "CAT";
    private static final String BRAND = "BRAND";
    private static final String AGE = "AGE";
    private static final String DESC = "DESC";


    FragmentProductInfoBinding binding;
    ImagesAdapter adapter;
    Database db;
    Product p;
    SharedPreferences file;
    Listeners.OpenImageListenerFactory factory;

    ActivityResultLauncher<Intent> startActivityPickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() != Activity.RESULT_OK) return;
                Intent data = res.getData();
                if (data != null) {
                    db.updateTempDB(data.getData());
                    binding.recView.post(() ->
                            adapter.notifyItemInserted(db.getImages().size() - 1));
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = Database.getInstance();
        p = new Product();
        file = requireContext().getSharedPreferences(Database.prefProdInfo,
                Context.MODE_PRIVATE);

        factory = new Listeners.OpenImageListenerFactory(requireActivity());

        binding = FragmentProductInfoBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadData();


        adapter = new ImagesAdapter(binding.recView, factory);
        binding.recView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recView.setAdapter(adapter);

        binding.addImage.setOnClickListener(l -> {
            saveData();
            startActivityPickImage.launch(new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        });

        binding.saveProduct.setOnClickListener(l -> {
            if (!checkFields()) return;
            Product p = new Product();
            p.setName(binding.nameProduct.getText().toString());
            p.setPrice(Integer.parseInt(binding.priceProduct.getText().toString()));
            p.setCategory(binding.categoryProduct.getText().toString());
            p.setBrand(binding.brandProduct.getText().toString());
            p.setMinAge(Integer.parseInt(binding.ageProduct.getText().toString()));
            p.setDesc(binding.descProduct.getText().toString());

            db.addProduct(p);
            file.edit().clear().apply();

            Navigator.getInstance().popBackStack();
        });


        binding.containerBack.setOnClickListener(v -> {
            Navigator.getInstance().popBackStack();
            db.clearImages();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        db.clearImages();
                    }
                });

    }

    public void saveData() {
        SharedPreferences.Editor edit = file.edit();

        edit.putString(NAME, binding.nameProduct.getText().toString());
        edit.putString(PRICE, binding.priceProduct.getText().toString());
        edit.putString(CAT, binding.categoryProduct.getText().toString());
        edit.putString(BRAND, binding.brandProduct.getText().toString());
        edit.putString(AGE, binding.ageProduct.getText().toString());
        edit.putString(DESC, binding.descProduct.getText().toString());

        edit.apply();
    }

    public void loadData() {
        binding.nameProduct.setText(file.getString(NAME, ""));
        binding.priceProduct.setText(file.getString(PRICE, ""));
        binding.categoryProduct.setText(file.getString(CAT, ""));
        binding.brandProduct.setText(file.getString(BRAND, ""));
        binding.ageProduct.setText(file.getString(AGE, ""));
        binding.descProduct.setText(file.getString(DESC, ""));
    }


    private boolean checkFields() {
        if (binding.nameProduct.getText().toString().isEmpty()) {
            showMessage("Название товара не может быть пустым");
            return false;
        }
        if (binding.priceProduct.getText().toString().isEmpty()) {
            showMessage("Цена товара не может отсутствовать");
            return false;
        }
        if (binding.categoryProduct.getText().toString().isEmpty()) {
            showMessage("Категория товара не может отсутствовать");
            return false;
        }
        if (binding.brandProduct.getText().toString().isEmpty()) {
            showMessage("Бренд товара не может отсутствовать");
            return false;
        }
        if (binding.descProduct.getText().toString().isEmpty()) {
            showMessage("Описание товара не может быть пустым");
            return false;
        }
        if (binding.ageProduct.getText().toString().isEmpty()) {
            showMessage("Минимальный возраст не может отсутствовать");
            return false;
        }
        if (db.getImages().isEmpty()) {
            showMessage("Добавьте хотя бы одну фотографию");
            return false;
        }
        return true;
    }

    private void showMessage(String msg) {
        Snackbar bar = Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT);
        View v = bar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
        params.gravity = Gravity.TOP;
        v.setLayoutParams(params);
        bar.show();
    }


}