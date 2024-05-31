package com.example.app.AddFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.ImagesAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Feedback;
import com.example.app.Models.Product;
import com.example.app.Utils.Formatter;
import com.example.app.Utils.ImageFileAdapter;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentFeedbackBinding;

public class FragmentFeedback extends Fragment {

    private static final String RATE = "rate";
    private static final String DESC = "desc";


    FragmentFeedbackBinding binding;
    ImagesAdapter adapter;
    String prodID;
    Database db;
    Product p;
    SharedPreferences file;
    Listeners.OpenImageListenerFactory factory;

    float rate;


    ActivityResultLauncher<Intent> doImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() != Activity.RESULT_OK) {
                    ImageFileAdapter.clearCurrUri();
                    return;
                }
                db.updateTempDB(ImageFileAdapter.getUri(requireContext()));
                binding.recView.post(() ->
                        adapter.notifyItemInserted(db.getImages().size() - 1));
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        file = requireContext().getSharedPreferences(Database.prefFeedInfo,
                Context.MODE_PRIVATE);
        assert getArguments() != null;
        prodID = getArguments().getString(Navigator.ID_PROD, "");
        db = Database.getInstance();
        p = db.getProductOnID(prodID);
        assert p != null;
        rate = 0;

        factory = new Listeners.OpenImageListenerFactory(requireActivity());

        binding = FragmentFeedbackBinding.inflate(inflater);
        return binding.getRoot();
    }


    public void saveData() {
        SharedPreferences.Editor edit = file.edit();

        edit.putFloat(RATE, binding.userRate.getRating());
        edit.putString(DESC, binding.feedback.getText().toString());

        edit.apply();
    }

    public void loadData() {
        rate = file.getFloat(RATE, 0);
        binding.userRate.setRating(rate);
        binding.rate.setText(Formatter.getFormattedRate(rate));
        binding.feedback.setText(file.getString(DESC, ""));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadData();

        binding.userRate.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            rate = rating;
            binding.rate.setText(Formatter.getFormattedRate(rating));
        });

        binding.addImage.setOnClickListener(l -> {
            saveData();

            Intent doImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            doImageIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    ImageFileAdapter.getUri(requireContext()));

            doImage.launch(doImageIntent);
        });


        adapter = new ImagesAdapter(binding.recView, factory);
        binding.recView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recView.setAdapter(adapter);


        binding.saveFeedback.setOnClickListener(l -> {
            Navigator.getInstance().popBackStack();

            p.setRate((p.getRate() * p.getRateCount() + rate) / (p.getRateCount() + 1));
            p.setRateCount(p.getRateCount() + 1);

            if (!binding.feedback.getText().toString().isEmpty()) {
                Feedback feedback = new Feedback();
                feedback.setFeedback(binding.feedback.getText().toString());
                feedback.setRate(binding.userRate.getRating());
                db.addFeedback(feedback, p);
            } else db.addFeedback(null, p);

            file.edit().clear().apply();

        });


        binding.btnBack.setOnClickListener(l -> exit());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exit();
                    }
                });

    }

    private void exit() {
        Navigator.getInstance().popBackStack();
        db.clearImages();
        file.edit().clear().apply();
    }

}