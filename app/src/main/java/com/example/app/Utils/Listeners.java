package com.example.app.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.Adapters.MyProductsAdapter;
import com.example.app.Databases.Database;
import com.example.app.ImageDialog;
import com.example.app.Models.Checks.CheckInfo;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.R;
import com.example.app.databinding.FragmentBucketBinding;
import com.example.app.databinding.FragmentMyProductsBinding;
import com.example.app.databinding.ItemProductBucketBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Listeners {

    private static final Navigator navigator = Navigator.getInstance();

    private static final Database db = Database.getInstance();


    public static class UpdateAfter {

        protected final RecyclerView recView;

        protected UpdateAfter(RecyclerView recView) {
            this.recView = recView;
        }

        protected void updateAfter() {
            recView.post(() -> recView.getAdapter().notifyDataSetChanged());
        }
    }

    public static int getCount() {
        int count = 0;
        for (ItemProduct item : db.getUserProducts())
            count += item.isChecked() ? 1 : 0;
        return count;
    }


    private static class DeleteFromListener extends UpdateAfter
            implements View.OnClickListener {

        protected CheckBox chx;

        public DeleteFromListener(RecyclerView recView, CheckBox chx) {
            super(recView);
            this.chx = chx;
        }

        @Override
        public void onClick(View v) {
            chx.setChecked(false);
            updateAfter();
        }
    }


    public static class DeleteFromBucketListener extends DeleteFromListener {

        public DeleteFromBucketListener(RecyclerView recView, CheckBox chx) {
            super(recView, chx);
        }

        @Override
        public void onClick(View v) {
            FragmentBucketBinding binding = FragmentBucketBinding.bind((View) recView.getParent().getParent());
            if (db.getBucket().size() - 1 == 0) {
                binding.placeholderEmpty.setVisibility(View.VISIBLE);
            }
            db.deleteFromBucket(((CheckInfo) chx.getTag()).getInfo());

            recView.post(() -> recView.getAdapter()
                    .notifyItemRemoved(recView
                            .getChildAdapterPosition((View) v.getParent())));
        }
    }

    public static class DeleteFromUserProductsListener extends DeleteFromListener {

        public DeleteFromUserProductsListener(RecyclerView recView, CheckBox chx) {
            super(recView, chx);
        }

        @Override
        public void onClick(View v) {
            FragmentMyProductsBinding binding = FragmentMyProductsBinding.bind((View) recView.getParent());
            if (Boolean.TRUE.equals(db.getLiveLoading().getValue())) {
                failMsg(v, recView.getContext().getString(R.string.product_info_loading_error));
                return;
            }
            if (db.getUserProducts().size() - 1 == 0) {
                binding.placeholderEmpty.setVisibility(View.VISIBLE);
            }
            db.deleteProduct(((CheckInfo) chx.getTag()).getInfo());

            recView.post(() -> recView.getAdapter()
                    .notifyItemRemoved(recView
                            .getChildAdapterPosition((View) v.getParent())));
        }
    }


    public static final class OnProductListener implements View.OnClickListener {


        private final Navigator.TypeAct type;

        private final String ID;

        public OnProductListener(Navigator.TypeAct type, String ID) {
            this.ID = ID;
            this.type = type;
        }

        private Dialog createDialog(View v) {
            ImageView img = new ImageView(v.getContext());

            Glide.with(v.getContext()).load(R.drawable.loading).into(img);

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            LinearLayout l = new LinearLayout(builder.getContext());
            l.addView(img);
            builder.setTitle(R.string.placeholder_loading).setView(l);

            Dialog d = builder.create();
            d.setCancelable(false);
            return d;
        }


        @Override
        public void onClick(View v) {
            if (Boolean.TRUE.equals(db.getLiveLoading().getValue())) {
                failMsg(v,
                        v.getContext().getString(R.string.product_info_loading_error));
                return;
            }


            Dialog dialog = createDialog(v);
            dialog.show();
            db.setLastProduct(ID);
            db.getFeedbacksFromDB(ID);
            db.getLiveLoading().observe((LifecycleOwner) v.getContext(), l -> {
                if (l || navigator.inStack(R.id.fragmentProduct)) return;
                if (db.getLastProduct() == null || !ID.equals(db.getLastProduct())) return;
                Bundle bundle = new Bundle();
                bundle.putString(Navigator.ID_PROD, ID);
                if (type == Navigator.TypeAct.MAIN) {
                    bundle.putInt(Navigator.TYPE_FRAG, Navigator.CURR_TYPE.PROD.ordinal());
                    navigator.navigateTo(R.id.addActivity, bundle);
                } else
                    navigator.navigateTo(R.id.action_fragmentMyProducts_to_fragmentProduct, bundle);
                dialog.cancel();
            });
        }
    }


    public static final class OnFilterChangeStateListener extends UpdateAfter
            implements CompoundButton.OnCheckedChangeListener {

        public enum TYPE {TITLE, ITEM, NONE}

        private final CheckBox titleCheckbox;

        private final ArrayList<? extends ItemFilter> filters;

        public OnFilterChangeStateListener(CheckBox titleCheckbox,
                                           RecyclerView recView,
                                           ArrayList<? extends ItemFilter> filters) {
            super(recView);
            this.titleCheckbox = titleCheckbox;
            this.filters = filters;
        }

        private void changeOneState(String name, boolean state) {
            for (ItemFilter filter : filters)
                if (filter.getName().equals(name)) {
                    filter.setChecked(state);
                    return;
                }
        }

        public boolean checkAllFilters() {
            for (ItemFilter filter : filters) if (!filter.isChecked()) return false;
            return !filters.isEmpty();
        }

        public void changeAllStates(boolean state) {
            for (ItemFilter filter : filters) filter.setChecked(state);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CheckInfo info = (CheckInfo) buttonView.getTag();
            switch (info.getTYPE()) {
                case TITLE:
                    changeAllStates(isChecked);
                    updateAfter();
                    break;
                case ITEM:
                    changeOneState(info.getInfo(), isChecked);

                    boolean allChecked = checkAllFilters();
                    if (allChecked != titleCheckbox.isChecked()) {
                        info = (CheckInfo) titleCheckbox.getTag();
                        titleCheckbox.setTag(new CheckInfo(TYPE.NONE, ""));
                        titleCheckbox.setChecked(allChecked);
                        titleCheckbox.setTag(info);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static class CountListener extends UpdateAfter implements View.OnClickListener {

        public enum SHIFT {INCR, DECR}

        private final int shift;


        public CountListener(RecyclerView recView, SHIFT shift) {
            super(recView);
            this.shift = shift == SHIFT.INCR ? 1 : -1;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            ItemProductBucketBinding binding = ItemProductBucketBinding.bind(((View) v.getParent().getParent()));

            int count = Integer.parseInt(binding.countProduct.getText().toString()) + shift;
            if (count == 0) {
                binding.btnClose.callOnClick();
                return;
            }
            binding.countProduct.setText(String.valueOf(count));

            String checkProdID = ((CheckInfo) binding.chx.getTag()).getInfo();
            for (ItemProduct item : db.getBucket())
                if (item.ProdID().equals(checkProdID))
                    item.setCount(count);


            if (binding.chx.isChecked()) {
                FragmentBucketBinding fragmentBucketBinding = FragmentBucketBinding.bind(
                        (View) binding.getRoot().getParent().getParent());
                fragmentBucketBinding.resPayment.setText("К оплате: " + Formatter.getPriceBucket());
            }
        }
    }

    public static final class CountProductListener implements TextWatcher {

        private final ItemProductBucketBinding binding;

        private final TextView res;

        public CountProductListener(EditText v, TextView res) {
            binding = ItemProductBucketBinding.bind(((View) v.getParent().getParent()));
            this.res = res;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (binding.chx.isChecked()) {
                res.setText("К оплате: " + Formatter.getPriceBucket());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public static final class ChecksListener implements CompoundButton.OnCheckedChangeListener {

        public enum TYPE {PRICE, COUNT}

        private final OnFilterChangeStateListener listener;

        private final TextView res;

        private final TYPE type;


        public ChecksListener(CheckBox titleChexbox,
                              RecyclerView recView,
                              TextView res, TYPE type) {
            this.res = res;
            this.type = type;
            this.listener = new OnFilterChangeStateListener(titleChexbox, recView,
                    type == TYPE.PRICE ? db.getBucket() : db.getUserProducts());
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            listener.onCheckedChanged(buttonView, isChecked);
            switch (type) {
                case PRICE:
                    res.setText("К оплате: " + Formatter.getPriceBucket());
                    break;
                case COUNT:
                    res.setText("Выбрано: " + getCount());
            }
        }
    }


    public static final class OpenImageListenerFactory {
        private final FragmentActivity activity;

        public OpenImageListenerFactory(FragmentActivity activity) {
            this.activity = activity;
        }

        public OpenImageListener newListener(String path) {
            return new OpenImageListener(path);
        }

        public final class OpenImageListener implements View.OnClickListener {
            private final String path;

            public OpenImageListener(String path) {
                this.path = path;
            }


            @Override
            public void onClick(View v) {
                new ImageDialog(path).show(activity.getSupportFragmentManager(), null);
            }
        }
    }


    public static void failMsg(View root, String msg) {
        Snackbar.make(root, msg, Snackbar.LENGTH_SHORT).show();
    }

}
