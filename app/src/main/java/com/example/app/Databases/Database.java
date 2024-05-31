package com.example.app.Databases;

import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.app.AddFragments.FragmentRegister;
import com.example.app.AddFragments.FragmentSignIn;
import com.example.app.Databases.FirebaseAdapter.TypeUPD;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Models.Feedback;
import com.example.app.Models.Product;
import com.example.app.Models.User;
import com.example.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Database {

    public MutableLiveData<Boolean> getLiveLoading() {
        return db.getLiveLoading();
    }

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public enum TypeSort {RATE, OLD_TO_NEW, NEW_TO_OLD}

    private TypeSort currentSort = TypeSort.RATE;
    private String currQuery = "";

    public String getCurrQuery() {
        return currQuery;
    }

    private final Integer[] currentFilters = new Integer[]{null, null, null};

    public Integer[] getCurrentFilters() {
        return currentFilters;
    }

    public static final String prefSignIn = "signInData";
    public static final String prefProdInfo = "prodInfoData";
    public static final String prefFeedInfo = "feedInfoData";
    private static final Database INSTANCE = new Database();
    private final FirebaseAdapter db;
    private final MutableLiveData<ArrayList<Product>> liveResProducts;
    private final ArrayList<Product> resProducts;
    private String lastProduct;

    private final ArrayList<Uri> tempImages;
    private volatile ArrayList<Feedback> feedbacks;

    private Database() {
        db = FirebaseAdapter.getInstance();

        resProducts = new ArrayList<>();
        liveResProducts = new MutableLiveData<>(resProducts);
        tempImages = new ArrayList<>();

    }


    public static Database getInstance() {
        return INSTANCE;
    }

    public void getData() {
        if (executor.isShutdown()) executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            db.getData();
            updLiveResProducts();
        }, 0, 3 * 60, TimeUnit.SECONDS);
    }

    public void clearData() {
        if (liveResProducts != null) {
            resProducts.clear();
            liveResProducts.setValue(resProducts);
        }
        if (tempImages != null) tempImages.clear();
        db.clearData();
    }

    public void sortResProducts(TypeSort type) {
        if (resProducts == null) return;
        switch (type) {
            case RATE:
                resProducts.sort(new Product.ComparatorRate());
                break;
            case NEW_TO_OLD:
                resProducts.sort(new Product.ComparatorOldToNew().reversed());
                break;
            case OLD_TO_NEW:
                resProducts.sort(new Product.ComparatorOldToNew());
                break;
        }
        currentSort = type;
        liveResProducts.setValue(resProducts);
    }


    private boolean inRange(Product p,
                            int minPrice, int maxPrice, int minAge) {
        return p.getPrice() >= minPrice && p.getPrice() <= maxPrice
                && p.getMinAge() >= minAge;
    }

    private void filterResProducts() {
        filterResProducts(currentFilters[0], currentFilters[1], currentFilters[2]);
    }

    public void filterResProducts(Integer givenMinPrice,
                                  Integer givenMaxPrice,
                                  Integer givenMinAge) {
        currentFilters[0] = givenMinPrice;
        currentFilters[1] = givenMaxPrice;
        currentFilters[2] = givenMinAge;
        Integer minPrice = givenMinPrice, maxPrice = givenMaxPrice, minAge = givenMinAge;
        if (minPrice == null) minPrice = 0;
        if (maxPrice == null) maxPrice = Integer.MAX_VALUE;
        if (minAge == null) minAge = 0;

        ArrayList<Product> newResProducts = new ArrayList<>();
        ArrayList<ItemFilter> categories = db.getCategories();
        ArrayList<ItemFilter> brands = db.getBrands();
        for (Product p : db.getProducts())
            for (ItemFilter item : categories)
                if (item.isChecked() && p.getCategory().equals(item.getName())
                        && inRange(p, minPrice, maxPrice, minAge))
                    newResProducts.add(p);

        if (newResProducts.isEmpty())
            for (Product p : db.getProducts())
                if (inRange(p, minPrice, maxPrice, minAge))
                    newResProducts.add(p);

        ArrayList<String> brandNames = new ArrayList<>();
        brands.forEach(item -> { if (item.isChecked()) brandNames.add(item.getName()); });
        if (!brandNames.isEmpty())
            newResProducts.removeIf(p -> !brandNames.contains(p.getBrand()));

        resProducts.clear();
        resProducts.addAll(newResProducts);

        filterQueryProduct(currQuery.isEmpty() ? "\0" : currQuery);
    }

    public void filterQueryProduct(String query) {
        if (query.equals("\0")) {
            sortResProducts(currentSort);
            return;
        }
        currQuery = query;
        if (currQuery.isEmpty()) {
            resProducts.clear();
            filterResProducts();
            sortResProducts(currentSort);
            return;
        }
        ArrayList<String> words = new ArrayList<>(Arrays.asList(query
                .toLowerCase().split(" ")));

        ArrayList<Product> newResProduct = new ArrayList<>();
        for (Product p : resProducts) {
            if (words.contains(p.getCategory().toLowerCase())
                    || words.contains(p.getBrand().toLowerCase()))
                newResProduct.add(p);
            for (String word : p.getName().toLowerCase().split(" "))
                if (words.contains(word) && !newResProduct.contains(p)) {
                    newResProduct.add(p);
                    break;
                }
        }
        resProducts.clear();
        resProducts.addAll(newResProduct);

        sortResProducts(currentSort);
    }


    public void updLiveResProducts() {
        resProducts.clear();
        resProducts.addAll(db.getProducts());
        filterResProducts();
        filterQueryProduct(currQuery);
        sortResProducts(currentSort);
    }

    public LiveData<ArrayList<Product>> getLiveResProducts() {
        return liveResProducts;
    }

    public User getUser() {
        return db.getUser();
    }

    public boolean deleteUser() {
        return db.deleteUser();
    }

    public void addProduct(Product p) {
        db.addProduct(p, tempImages);
    }

    public Product getProductOnID(String ID) {
        return db.getProductOnID(ID);
    }


    public enum Type {PROFILE, ELSE}

    public static void loadImage(String path, ImageView resView) {
        loadImage(path, resView, Type.ELSE);
    }

    public static void loadImage(String path, ImageView resView, Type type) {
        if (path == null) return;
        Glide.with(resView.getContext())
                .load(Uri.parse(path))
                .placeholder(type == Type.PROFILE ?
                        R.drawable.profile
                        : R.drawable.loading).into(resView);
        resView.setPadding(0, 0, 0, 0);
    }


    public void deleteProduct(String prodID) {
        db.deleteProduct(prodID);
    }

    public ArrayList<ItemProduct> getUserProducts() {
        return db.getUserProducts();
    }

    public ArrayList<ItemProduct> getBucket() {
        return db.getBucket();
    }

    private void updUserBucketDB(TypeUPD typeUPD, String prodID) {
        db.updUserBucketDB(typeUPD, prodID);
    }

    public void addInBucket(String prodID) {
        updUserBucketDB(TypeUPD.ADD, prodID);
    }

    public void deleteFromBucket(String prodID) {
        updUserBucketDB(TypeUPD.DEL, prodID);
    }


    public ArrayList<ItemFilter> getCategories() {
        return db.getCategories();
    }

    public String getCategoryImage(ItemFilter filter) {
        String res = db.getCategoryImage(filter);
        if (res == null && !tempImages.isEmpty())
            return tempImages.get(0).toString();
        return res;
    }

    public ArrayList<ItemFilter> getBrands() {
        return db.getBrands();
    }


    public void singIn(FragmentSignIn.SingInData data) {
        db.singIn(data);
    }

    public void registerUser(FragmentRegister.RegisterData data) {
        db.registerUser(data);
    }

    public ArrayList<Uri> getImages() {
        return tempImages;
    }

    public void updateTempDB(Uri filePath) {
        tempImages.add(filePath);
    }

    public void getFeedbacksFromDB(String prodID) {
        feedbacks = db.getFeedbacksFromDB(prodID);
    }

    public void addFeedback(Feedback feedback, Product p) {
        if (feedback != null) feedbacks.add(0, feedback);
        db.addFeedback(feedback, p, tempImages);
    }

    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }


    public void updUserName(String newName) {
        db.updUserName(newName);
    }


    public void updUserImage() {
        if (db.getUser().getTempUserImage() == null) return;
        db.updUserImage();
    }

    public void clearImage(Uri image) {
        db.clearImage(image);
    }

    public void clearImages() {
        db.clearImage(db.getUser().getTempUserImage());
        db.clearImages(tempImages);
    }

    public void setLastProduct(String ID) {
        lastProduct = ID;
    }

    public String getLastProduct() {
        return lastProduct;
    }

    public boolean hasProducts() {
        return db.hasProducts();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clearImages();
    }
}
