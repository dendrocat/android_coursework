package com.example.app.MainFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.R;
import com.example.app.databinding.FragmentProfileBinding;

public class FragmentProfile extends Fragment {

    FragmentProfileBinding binding;

    Database db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);
        db = Database.getInstance();
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadUserData();

        binding.settingAboutPerson.setOnClickListener(v -> navigateTo(Navigator.CURR_TYPE.INFO));

        if (Database.getInstance().getUser().isSeller())
            binding.settingMyProducts.setOnClickListener(v -> navigateTo(Navigator.CURR_TYPE.MY_PROD));
        else binding.settingMyProducts.setVisibility(View.GONE);

        binding.settingAboutProgram.setOnClickListener(v -> navigateTo(Navigator.CURR_TYPE.ABOUT));

        binding.settinglogOut.setOnClickListener(v -> {
            requireActivity()
                    .getSharedPreferences(Database.prefSignIn,
                            Context.MODE_PRIVATE)
                    .edit().clear().apply();

            requireActivity().finish();
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void navigateTo(Navigator.CURR_TYPE fragmentType) {
        Navigator nav = Navigator.getInstance();
        if (nav.getStackSize() != 0) return;
        Bundle arg = new Bundle();
        arg.putInt(Navigator.TYPE_FRAG, fragmentType.ordinal());

        Navigator.getInstance()
                .navigateTo(R.id.action_fragmentProfile_to_addActivity, arg);
    }

    private void loadUserData() {
        binding.userName.setText(db.getUser().getName());

        String path = null;

        if (db.getUser().getTempUserImage() != null)
            path = db.getUser().getTempUserImage().toString();
        else if (db.getUser().getImage() != null)
            path = db.getUser().getImage();


        if (path != null) {
            Database.loadImage(path, binding.imageProf, Database.Type.PROFILE);
            binding.imageProf.setOnClickListener(
                    new Listeners.OpenImageListenerFactory(requireActivity())
                            .newListener(path));
        }

    }

}