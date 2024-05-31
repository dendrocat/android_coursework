package com.example.app.AddFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.R;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentAboutProgramBinding;

public class FragmentAboutProgram extends Fragment {

    FragmentAboutProgramBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutProgramBinding.inflate(inflater);
        return binding.getRoot();
    }

    private void exit() {
        requireActivity().finish();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(l -> exit());

        binding.settingAboutAuthor.setOnClickListener(l ->
                Navigator.getInstance().navigateTo(R.id.action_fragmentAboutProgram_to_fragmentAboutAuthor));
        binding.settingInstuction.setOnClickListener(l ->
                Navigator.getInstance().navigateTo(R.id.action_fragmentAboutProgram_to_fragmentInstruction));

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exit();
                    }
                }
        );

    }
}