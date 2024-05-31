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
import com.example.app.databinding.ItemProductMyBinding;

public class MyProductViewHolder extends RecyclerView.ViewHolder {

    ItemProductMyBinding binding;

    private RecyclerView recView = null;

    private MyProductViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemProductMyBinding.bind(itemView);
    }

    public static MyProductViewHolder create(ViewGroup parent) {
        MyProductViewHolder p = new MyProductViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_my, parent, false));
        p.recView = (RecyclerView) parent;
        return p;
    }

    public void bind(ItemProduct itemProduct, Listeners.ChecksListener listener) {
        Database db = Database.getInstance();
        Product p = db.getProductOnID(itemProduct.ProdID());

        if (p.getImages().isEmpty())
            if (db.getImages().isEmpty())
                binding.imageProduct.setImageResource(R.drawable.placeholder);
            else Database.loadImage(db.getImages().get(0).toString(), binding.imageProduct);
        else Database.loadImage(p.getImage(0), binding.imageProduct);

        Listeners.OnProductListener productListener = new Listeners.OnProductListener(
                Navigator.TypeAct.ADD, p.getID());

        binding.imageProduct.setOnClickListener(productListener);

        binding.nameProduct.setText(p.getName());
        binding.nameProduct.setOnClickListener(productListener);

        binding.priceProduct.setText(p.convertPriceToString());

        binding.chxProductMy.setTag(new CheckInfo(
                Listeners.OnFilterChangeStateListener.TYPE.ITEM,
                String.valueOf(p.getID()) ));
        binding.chxProductMy.setChecked(itemProduct.isChecked());
        binding.chxProductMy.setOnCheckedChangeListener(listener);

        binding.btnClose.setOnClickListener(new Listeners.DeleteFromUserProductsListener(
                recView, binding.chxProductMy));
    }



}
