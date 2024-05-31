package com.example.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.app.Databases.Database;
import com.example.app.databinding.ImageDialogBinding;

public class ImageDialog extends DialogFragment {

    ImageDialogBinding binding;
    String path;

    public ImageDialog(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = ImageDialogBinding.inflate(getLayoutInflater());


        Database.loadImage(path, binding.image);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();

        binding.btnBack.setOnClickListener(l -> dialog.cancel());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
