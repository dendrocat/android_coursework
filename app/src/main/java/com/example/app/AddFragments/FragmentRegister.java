package com.example.app.AddFragments;

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
import com.example.app.Models.User;
import com.example.app.databinding.FragmentRegisterBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FragmentRegister extends Fragment {

    FragmentRegisterBinding binding;
    Navigator nav;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);
        nav = Navigator.getInstance();
        return binding.getRoot();
    }


    private void setState(boolean enabled) {
        binding.enterName.setEnabled(enabled);
        binding.enterEmail.setEnabled(enabled);
        binding.enterPass.setEnabled(enabled);
        binding.repeatPass.setEnabled(enabled);
        binding.checkSeller.setEnabled(enabled);

        binding.btnReg.setEnabled(enabled);
        binding.btnBack.setEnabled(enabled);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Database db = Database.getInstance();

        binding.btnBack.setOnClickListener(v -> nav.popBackStack());

        binding.btnReg.setOnClickListener(v -> {
            if (!binding.enterPass.getText().toString()
                    .equals(binding.repeatPass.getText().toString())) {
                Listeners.failMsg(binding.getRoot(), "Пароли не совпадают");
                return;
            }
            setState(false);
            db.registerUser(new RegisterData(binding.enterName.getText().toString(),
                    binding.enterEmail.getText().toString(),
                    binding.enterPass.getText().toString(),
                    binding.checkSeller.isChecked()));
        });

    }

    public class RegisterData {

        private final User newUser;

        private final String email;

        private final String pass;

        public RegisterData(String name, String email, String pass, boolean seller) {
            newUser = new User(name, seller);
            this.email = email;
            this.pass = pass;
        }

        public User getNewUser() {
            return newUser;
        }

        public String getEmail() {
            return email;
        }

        public String getPass() {
            return pass;
        }

        public void failRegister(Exception e) {
            binding.checkSeller.setChecked(false);
            binding.enterName.setText("");
            binding.enterEmail.setText("");
            binding.enterPass.setText("");
            binding.repeatPass.setText("");
            setState(true);

            Listeners.failMsg(binding.getRoot(), e.getLocalizedMessage());
        }



        public void afterRegister() {
            setState(false);
            Database db = Database.getInstance(); db.getData();
            db.getLiveLoading().setValue(true);

            db.getLiveLoading().observe(requireActivity(), loading -> {
                if (loading) return;
                nav.popBackStack(R.id.fragmentSingIn, false);
                ScheduledExecutorService a = Executors.newSingleThreadScheduledExecutor();
                a.schedule(() -> {
                    nav.navigateTo(R.id.action_fragmentSingIn_to_mainActivity);
                    Navigator.wasMain = true;
                        },
                        getResources().getInteger(android.R.integer.config_shortAnimTime),
                        TimeUnit.MILLISECONDS);
            });

        }
    }

}