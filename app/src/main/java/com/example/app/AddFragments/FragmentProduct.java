package com.example.app.AddFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Adapters.FeedbackAdapter;
import com.example.app.Databases.Database;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Models.Product;
import com.example.app.R;
import com.example.app.Utils.Formatter;
import com.example.app.Utils.Listeners;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentProductBinding;

public class FragmentProduct extends Fragment {
    enum TypeAnim {LEFT, RIGHT}

    FragmentProductBinding binding;

    FeedbackAdapter adapter;

    Database db;

    Product p;

    int pos = 0;

    Listeners.OpenImageListenerFactory factory;


    private static class ImageFactory implements ViewSwitcher.ViewFactory {

        private final Context ctx;

        public ImageFactory(Context context) {
            ctx = context;
        }

        @Override
        public View makeView() {
            ImageView v = new ImageView(ctx);
            v.setScaleType(ImageView.ScaleType.FIT_CENTER);
            v.setLayoutParams(new ImageSwitcher.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            return v;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = Database.getInstance();
        assert getArguments() != null;
        p = db.getProductOnID(getArguments().getString(Navigator.ID_PROD, ""));
        assert p != null;
        factory = new Listeners.OpenImageListenerFactory(requireActivity());

        binding = FragmentProductBinding.inflate(inflater);
        return binding.getRoot();
    }


    private void setInBacket() {
        binding.addToBacket.setText(R.string.product_page_btn_in_bucket);
        binding.addToBacket.setOnClickListener(v -> {
            Navigator.toBucket = true;
            exit();
        });
    }

    private void setImageListener() {
        binding.imageSwitcher.getCurrentView().setOnClickListener(factory.newListener(p.getImage(pos)));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.nameProduct.setText(p.getName());
        binding.priceProduct.setText(p.convertPriceToString());
        binding.catBrandProduct.setText(p.getCategory() + " " + p.getBrand());
        binding.ageProduct.setText(p.convertAgeString());
        binding.descProduct.setText(p.getDesc());

        binding.rateProduct.setText(Formatter.getFormattedRate(p.getRate()));
        binding.rateCount.setText("Отзывов: " + p.getRateCount());

        boolean inBucket = false;
        for (ItemProduct item : db.getBucket())
            if (item.ProdID().equals(p.getID())) {
                inBucket = true;
            }
        if (inBucket) setInBacket();
        else binding.addToBacket.setOnClickListener(v -> {
            db.addInBucket(p.getID());
            setInBacket();
        });

        binding.addFeedback.setOnClickListener(l -> {
            Bundle arg = new Bundle();
            arg.putString(Navigator.ID_PROD, p.getID());
            Navigator.getInstance()
                    .navigateTo(R.id.action_fragmentProduct_to_fragmentFeedback, arg);
        });


        adapter = new FeedbackAdapter(factory);
        binding.recView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recView.setHasFixedSize(true);
        binding.recView.setAdapter(adapter);

        binding.imageSwitcher.setFactory(new ImageFactory(requireContext()));
        setAnim(TypeAnim.RIGHT);
        setImageListener();
        Database.loadImage(p.getImage(0), (ImageView) binding.imageSwitcher.getCurrentView());

        binding.btnPrev.setOnClickListener(l -> prevImage());
        binding.btnPrev.setVisibility(View.GONE);
        binding.btnNext.setOnClickListener(l -> nextImage());
        if (p.getImages().size() == 1 || db.getImages().size() == 1)
            binding.btnNext.setVisibility(View.GONE);

        binding.btnBack.setOnClickListener(l -> exit());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exit();
                    }
                }
        );
    }


    private void exit() {
        Navigator nav = Navigator.getInstance();
        if (nav.getStackSize() == 1) {
            requireActivity().finish();
        } else {
            nav.popBackStack();
        }
        db.setLastProduct(null);
    }

    private void setAnim(TypeAnim type) {
        switch (type) {
            case LEFT:
                binding.imageSwitcher.setInAnimation(requireContext(), R.anim.slide_in_left);
                binding.imageSwitcher.setOutAnimation(requireContext(), R.anim.slide_out_right);
                break;
            case RIGHT:
                binding.imageSwitcher.setInAnimation(requireContext(), R.anim.slide_in_right);
                binding.imageSwitcher.setOutAnimation(requireContext(), R.anim.slide_out_left);
                break;
        }
    }

    private void nextImage() {
        ++pos;
        if (!p.getImages().isEmpty() && pos == p.getImages().size()
                || p.getImages().isEmpty() && pos == db.getImages().size()) {
            --pos;
            return;
        }
        if (!p.getImages().isEmpty() && pos == p.getImages().size() - 1
                || p.getImages().isEmpty() && pos == db.getImages().size() - 1)
            binding.btnNext.setVisibility(View.GONE);
        if (binding.btnPrev.getVisibility() == View.GONE)
            binding.btnPrev.setVisibility(View.VISIBLE);
        setAnim(TypeAnim.RIGHT);
        binding.imageSwitcher.setImageURI(Uri.parse(p.getImage(pos)));
        setImageListener();
        Database.loadImage(p.getImage(pos), (ImageView) binding.imageSwitcher.getCurrentView());
    }

    private void prevImage() {
        --pos;
        if (pos < 0) {
            pos = 0;
            return;
        }
        if (pos == 0) binding.btnPrev.setVisibility(View.GONE);
        if (binding.btnNext.getVisibility() == View.GONE)
            binding.btnNext.setVisibility(View.VISIBLE);
        setAnim(TypeAnim.LEFT);
        binding.imageSwitcher.setImageURI(Uri.parse(p.getImage(pos)));
        setImageListener();
        Database.loadImage(p.getImage(pos), (ImageView) binding.imageSwitcher.getCurrentView());
    }


}