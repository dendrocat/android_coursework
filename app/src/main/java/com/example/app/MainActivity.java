package com.example.app;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.app.Utils.Navigator;
import com.example.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Navigator nav = Navigator.getInstance();
        nav.setNavController(getNavController(), Navigator.TypeAct.MAIN);

        NavigationUI.setupWithNavController(binding.bottomNavigationView,
                nav.getNavController());


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Navigator nav = Navigator.getInstance();
        nav.setNavController(getNavController(), Navigator.TypeAct.MAIN);
        if (Navigator.toBucket) {
            Navigator.toBucket = false;
            navigateBottom(R.id.fragmentBucket);
        }
    }

    public void navigateBottom(int ID) {
        NavigationUI.onNavDestinationSelected(
                binding.bottomNavigationView
                        .getMenu().findItem(ID),
                Navigator.getInstance().getNavController());
    }

    private NavController getNavController() {
        return ((NavHostFragment) binding.navHost.getFragment()).getNavController();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Navigator.wasMain = false;
    }
}