package com.example.app.AddFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentAboutAuthorBinding;

public class FragmentAboutAuthor extends Fragment {

    FragmentAboutAuthorBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutAuthorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String path = "https://firebasestorage.googleapis.com/v0/b/" +
                "course2024-93be0.appspot.com/o/Аватарка.png" +
                "?alt=media&token=7c922cd2-9300-4b44-97a3-0328a8a5357f";

        binding.imageAuthor.setOnClickListener(
                new Listeners.OpenImageListenerFactory(requireActivity())
                        .newListener(path)
        );

        Database.loadImage(path, binding.imageAuthor, Database.Type.PROFILE);

        binding.btnBack.setOnClickListener(l ->
                Navigator.getInstance().popBackStack());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Navigator.getInstance().popBackStack();
                    }
                }
        );

    }
}