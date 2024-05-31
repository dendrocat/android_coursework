package com.example.app.AddFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.Databases.Database;
import com.example.app.R;
import com.example.app.Utils.ImageFileAdapter;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentInformationBinding;
import com.google.android.material.snackbar.Snackbar;


public class FragmentInformation extends Fragment {

    FragmentInformationBinding binding;

    Database db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInformationBinding.inflate(inflater);
        db = Database.getInstance();
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> doImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() != Activity.RESULT_OK) {
                    ImageFileAdapter.clearCurrUri();
                    return;
                }
                if (!db.getImages().isEmpty()) db.getImages().clear();
                db.getUser().setTempUserImage(ImageFileAdapter.getUri(requireContext()));
            }
    );

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        binding.nikName.setText(db.getUser().getName());

        if (db.getUser().getTempUserImage() != null)
            Database.loadImage(db.getUser().getTempUserImage().toString(),
                    binding.personImage, Database.Type.PROFILE);
        else if (db.getUser().getImage() != null)
            Database.loadImage(db.getUser().getImage(),
                    binding.personImage, Database.Type.PROFILE);

        binding.delUser.setOnClickListener(l -> {
            if (!db.deleteUser()) {
                Listeners.failMsg(binding.getRoot(),
                        "Ошибка при удалении пользователя");
                return;
            }
            requireActivity().getSharedPreferences(Database.prefSignIn, Context.MODE_PRIVATE)
                    .edit().clear().apply();
            Navigator.getInstance()
                    .popBackStack(R.id.fragmentInformation, true);
            Navigator.getInstance().navigateTo(R.id.fragmentSingIn);
        });

        binding.doImage.setOnClickListener(l -> {

            Intent doImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            doImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    ImageFileAdapter.getUri(requireContext()));
            doImage.launch(doImageIntent);
        });

        binding.saveChanges.setOnClickListener(l -> {
            db.updUserName(binding.nikName.getText().toString());
            db.updUserImage();
            Listeners.failMsg(binding.getRoot(), "Успешно");
        });


        binding.btnBack.setOnClickListener(l -> {
            db.clearImages();
            requireActivity().finish();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        db.clearImages();
                        requireActivity().finish();
                    }
                });
    }
}