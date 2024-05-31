package com.example.app.ViewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Databases.Database;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Utils.Listeners;
import com.example.app.Models.Product;
import com.example.app.R;
import com.example.app.Utils.Navigator;
import com.example.app.databinding.FragmentBucketBinding;
import com.example.app.databinding.ItemProductBucketBinding;

public class ProductBucketHolder extends RecyclerView.ViewHolder {

    ItemProductBucketBinding binding;

    RecyclerView recView;

    private ProductBucketHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemProductBucketBinding.bind(itemView);
    }

    public static ProductBucketHolder create(ViewGroup parent) {
        ProductBucketHolder p = new ProductBucketHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_bucket, parent, false));
        p.recView = (RecyclerView) parent;
        return p;
    }

    public void bind(Product p, ItemProduct itemProduct,
                     Listeners.ChecksListener listener) {
        Database db = Database.getInstance();
        if (p.getImages().isEmpty())
            if (db.getImages().isEmpty())
                binding.imageProduct.setImageResource(R.drawable.placeholder);
            else Database.loadImage(db.getImages().get(0).toString(), binding.imageProduct);
        else Database.loadImage(p.getImage(0), binding.imageProduct);

        Listeners.OnProductListener productListener = new Listeners.OnProductListener(
                Navigator.TypeAct.MAIN, p.getID());

        binding.imageProduct.setOnClickListener(productListener);

        binding.nameProduct.setText(p.getName());
        binding.nameProduct.setOnClickListener(productListener);

        binding.priceProduct.setText(p.convertPriceToString());

        binding.chx.setTag(new CheckInfo(
                Listeners.OnFilterChangeStateListener.TYPE.ITEM,
                String.valueOf(p.getID())));
        binding.chx.setChecked(itemProduct.isChecked());
        binding.chx.setOnCheckedChangeListener(listener);


        FragmentBucketBinding bucketBinding = FragmentBucketBinding.bind((View) recView.getParent().getParent());

        binding.btnIncr.setOnClickListener(new Listeners.CountListener(recView,
                Listeners.CountListener.SHIFT.INCR));


        binding.countProduct.setText(String.valueOf(itemProduct.getCount()));
        binding.countProduct.addTextChangedListener(new Listeners.CountProductListener(
                binding.countProduct, bucketBinding.resPayment));

        binding.btnDecr.setOnClickListener(new Listeners.CountListener(recView,
                Listeners.CountListener.SHIFT.DECR));

        binding.btnClose.setOnClickListener(new Listeners.DeleteFromBucketListener(
                recView, binding.chx));
    }


}
