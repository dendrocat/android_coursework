package com.example.app.AddFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.app.Databases.Database;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.R;
import com.example.app.databinding.FragmentSignInBinding;


public class FragmentSignIn extends Fragment {

    FragmentSignInBinding binding;

    Database db;

    SharedPreferences enterData;

    private static final String REMEMBER_KEY = "remember";

    private static final String EMAIL_KEY = "email";

    private static final String PASS_KEY = "pass";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater);
        db = Database.getInstance();
        enterData = requireActivity()
                .getSharedPreferences(Database.prefSignIn,
                        Context.MODE_PRIVATE);
        return binding.getRoot();
    }


    private void saveData() {
        SharedPreferences.Editor edit = enterData.edit();

        edit.putBoolean(REMEMBER_KEY, binding.checkRememberMe.isChecked());
        edit.putString(EMAIL_KEY, binding.enterEmail.getText().toString());
        edit.putString(PASS_KEY, binding.enterPass.getText().toString());
        edit.apply();
    }


    private boolean loadData() {
        binding.enterEmail.setText(enterData.getString(EMAIL_KEY, ""));
        binding.enterPass.setText(enterData.getString(PASS_KEY, ""));
        binding.checkRememberMe.setChecked(enterData
                .getBoolean(REMEMBER_KEY, false));
        return binding.checkRememberMe.isChecked();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnEnter.setOnClickListener(v -> {
                    if (binding.checkRememberMe.isChecked()) saveData();

                    InputMethodManager manager = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    //db.singIn(new SingInData());
                    db.singIn(new SingInData(
                            binding.enterEmail.getText().toString(),
                            binding.enterPass.getText().toString()));
                }
        );

        binding.btnReg.setOnClickListener(v ->
                Navigator.getInstance()
                        .navigateTo(R.id.action_fragmentSingIn_to_fragmentRegister));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (loadData()) {
            setState(false);
            binding.btnEnter.callOnClick();
        } else setState(true);
    }

    private void setState(boolean state) {
        binding.enterEmail.setEnabled(state);
        binding.enterPass.setEnabled(state);
        binding.btnEnter.setEnabled(state);
        binding.btnReg.setEnabled(state);
        binding.checkRememberMe.setEnabled(state);
    }


    public class SingInData {
        private final String email;

        private final String pass;

        public SingInData(String email, String pass) {
            this.email = email;
            this.pass = pass;
        }

        public String getEmail() {
            return email;
        }

        public String getPass() {
            return pass;
        }

        public void failSignIn(Exception e) {
            binding.enterEmail.setText("");
            binding.enterPass.setText("");

            Listeners.failMsg(binding.getRoot(),
                    e.getLocalizedMessage());
        }

        private Observer<Boolean> observer = loading -> {
            if (loading || Navigator.wasMain) return;
            Navigator.getInstance().navigateTo(R.id.mainActivity);
            Navigator.wasMain = true;
        };

        public void afterSignIn() {
            setState(false);
            Database db = Database.getInstance(); db.getData();
            db.getLiveLoading().setValue(true);

            db.getLiveLoading().observe(requireActivity(), loading -> {
                observer.onChanged(loading);
                if (!loading) db.getLiveLoading().removeObserver(observer);
            });

        }

    }

}