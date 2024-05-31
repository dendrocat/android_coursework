package com.example.app;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.app.Databases.Database;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.ActivityAddBinding;

public class AddActivity extends AppCompatActivity {

    ActivityAddBinding binding;

    private NavController getNavController() {
        return ((NavHostFragment) binding.navHost.getFragment()).getNavController();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Navigator nav = Navigator.getInstance();
        nav.setNavController(getNavController(), Navigator.TypeAct.ADD);

        int type = getIntent().getIntExtra(Navigator.TYPE_FRAG, 0);
        Navigator.CURR_TYPE Type = Navigator.CURR_TYPE.values()[type];

        nav.pushStack(R.id.fragmentSingIn);
        nav.popBackStack();
        switch (Type) {
            case START:
                Database.getInstance().clearData();
                nav.navigateTo(R.id.fragmentSingIn);
                break;
            case INFO:
                nav.navigateTo(R.id.fragmentInformation);
                break;
            case PROD:
                boolean toFeedback = nav.getStackSize() == 2;
                nav.clearStack();

                Bundle args = getIntent().getExtras();
                nav.navigateTo(R.id.fragmentProduct, args);
                if (toFeedback) {
                    Bundle args_feed = new Bundle();
                    args_feed.putString(Navigator.ID_PROD,
                            Database.getInstance().getLastProduct());
                    nav.navigateTo(R.id.action_fragmentProduct_to_fragmentFeedback,
                            args_feed);
                }
                break;
            case MY_PROD:
                boolean toInfo = nav.getStackSize() == 2;
                boolean toFeedbackMyProd = nav.getStackSize() == 3;
                nav.clearStack();

                nav.navigateTo(R.id.fragmentMyProducts);
                if (toInfo)
                    nav.navigateTo(R.id.action_fragmentMyProducts_to_fragmentProductInfo);
                else if (toFeedbackMyProd) {
                    Bundle args_prod = getIntent().getExtras();
                    args_prod.putString(Navigator.ID_PROD,
                            Database.getInstance().getLastProduct());
                    nav.navigateTo(R.id.action_fragmentMyProducts_to_fragmentProduct, args_prod);
                    Bundle args_feed = new Bundle();
                    args_feed.putString(Navigator.ID_PROD,
                            Database.getInstance().getLastProduct());
                    nav.navigateTo(R.id.action_fragmentProduct_to_fragmentFeedback,
                            args_feed);
                }
                break;
            case ABOUT:
                nav.navigateTo(R.id.fragmentAboutProgram);
        }
    }


}