package com.example.app.Databases;

import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.app.AddFragments.FragmentRegister;
import com.example.app.AddFragments.FragmentSignIn;
import com.example.app.Models.Checks.BaseFilter;
import com.example.app.Models.Checks.ItemFilter;
import com.example.app.Models.Checks.ItemProduct;
import com.example.app.Models.Feedback;
import com.example.app.Models.Product;
import com.example.app.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FirebaseAdapter {

    private static class ComparatorImages implements Comparator<String> {

        @Override
        public int compare(String u1, String u2) {
            Uri uri1 = Uri.parse(u1), uri2 = Uri.parse(u2);
            String img1 = uri1.getPath().substring(uri1.getPath().lastIndexOf('/') + 1);
            String img2 = uri2.getPath().substring(uri2.getPath().lastIndexOf('/') + 1);
            return img1.compareTo(img2);
        }
    }


    private static final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getLiveLoading() {
        return loading;
    }


    private enum Type {
        PRODS("products"),
        CATEGORIES("category"),
        BRAND("brand"),
        BUCKET("bucket"),
        USER_PRODS("userProds"),
        FEEDBACK("feedbacks");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String get() {
            return name;
        }
    }

    public enum TypeUPD {ADD, DEL}

    private static final char isSeller = '_';
    private static final char notSeller = '\t';

    private static FirebaseAdapter INSTANCE = null;
    private final FirebaseAuth auth;
    private final StorageReference storage;
    private final DatabaseReference DB;
    private volatile User user;
    private final ArrayList<Product> products;
    private final ArrayList<ItemFilter> categories;
    private final ArrayList<ItemFilter> brands;
    private final ArrayList<ItemProduct> bucket;
    private final ArrayList<ItemProduct> userProducts;

    private FirebaseAdapter() {
        DB = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        products = new ArrayList<>();
        categories = new ArrayList<>();
        brands = new ArrayList<>();
        bucket = new ArrayList<>();
        userProducts = new ArrayList<>();
    }

    public static FirebaseAdapter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseAdapter();
        }
        return INSTANCE;
    }

    void getData() {
        if (!products.isEmpty()) return;
        DB.child(Type.PRODS.get())
                .get().addOnSuccessListener(snapshot -> {
                    loading.setValue(true);
                    for (DataSnapshot d : snapshot.getChildren()) {
                        products.add(d.getValue(Product.class));
                    }
                });

        DB.child(Type.CATEGORIES.get())
                .get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot d : snapshot.getChildren())
                        categories.add(d.getValue(ItemFilter.class));
                });

        DB.child(Type.BRAND.get())
                .get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot d : snapshot.getChildren())
                        brands.add(d.getValue(ItemFilter.class));
                });


        DB.child(Type.BUCKET.get()).child(user.getUID())
                .get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot d : snapshot.getChildren())
                        bucket.add(d.getValue(ItemProduct.class));
                    if (!user.isSeller())
                        loading.setValue(false);
                });

        if (user.isSeller())
            DB.child(Type.USER_PRODS.get()).child(user.getUID())
                    .get().addOnSuccessListener(snapshot -> {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            String uid = d.getKey();
                            String val = d.getValue(String.class);
                            ItemProduct item = new ItemProduct(val);
                            item.setUID(uid);
                            userProducts.add(item);
                        }
                        loading.setValue(false);
                    });
    }

    void clearData() {
        if (products != null) products.clear();
        if (bucket != null) bucket.clear();
        if (categories != null) categories.clear();
        if (brands != null) brands.clear();
        if (userProducts != null) userProducts.clear();
    }

    private int getNextIndex() {
        int index = 0;
        for (Product p : products)
            if (index < p.getIndex()) index = p.getIndex();
        return index + 1;
    }

    private void updProducts(TypeUPD typeUPD, Product p) {
        switch (typeUPD) {
            case ADD:
                DatabaseReference refNewProd = DB.child(Type.PRODS.get()).push();

                p.setIndex(getNextIndex());
                p.setID(refNewProd.getKey());
                products.add(p);

                refNewProd.setValue(p);
                break;
            case DEL:
                products.remove(p);
                DB.child(Type.PRODS.get()).child(p.getID()).removeValue();

                for (int i = 0; i < p.getImages().size(); ++i)
                    storage.child("prod_images/" + p.getID() + "/" + i + ".jpg").delete();
                break;
        }
    }


    private void updUserProducts(TypeUPD typeUPD, Product p) {
        switch (typeUPD) {
            case ADD:
                ItemProduct addItem = new ItemProduct(p.getID());
                userProducts.add(addItem);
                DatabaseReference redUsPr = DB.child(Type.USER_PRODS.get())
                        .child(user.getUID()).push();
                addItem.setUID(redUsPr.getKey());
                redUsPr.setValue(addItem.ProdID());
                break;
            case DEL:
                ItemProduct delItem = null;
                for (ItemProduct item : userProducts)
                    if (item.ProdID().equals(p.getID()))
                        delItem = item;
                if (delItem == null) return;

                userProducts.remove(delItem);
                DB.child(Type.USER_PRODS.get())
                        .child(user.getUID()).child(delItem.getUID())
                        .removeValue();
                break;
        }
    }

    private void updFilter(TypeUPD typeUPD, Type type, Product p) {
        ArrayList<ItemFilter> old_filters = type == Type.CATEGORIES ? categories : brands;
        String info = type == Type.CATEGORIES ? p.getCategory() : p.getBrand();


        switch (typeUPD) {
            case ADD:
                for (ItemFilter filter : old_filters)
                    if (info.equals(filter.getName())) return;
                DatabaseReference refNewFilter = DB.child(type.get()).push();

                ItemFilter newFilter = new ItemFilter(info);
                newFilter.setUID(refNewFilter.getKey());
                old_filters.add(newFilter);

                BaseFilter baseFilter = new BaseFilter(newFilter.getName());
                baseFilter.setUID(newFilter.getUID());
                refNewFilter.setValue(baseFilter);
                break;
            case DEL:
                for (Product dbProd : products) {
                    String compareField = type == Type.CATEGORIES ? dbProd.getCategory() : dbProd.getBrand();
                    if (compareField.equals(info) && !dbProd.getID().equals(p.getID())) return;
                }

                ItemFilter delItem = null;
                for (ItemFilter item : old_filters)
                    if (item.getName().equals(info)) {
                        delItem = item;
                        break;
                    }
                if (delItem == null) return;
                old_filters.remove(delItem);
                DB.child(type.get()).child(delItem.getUID()).removeValue();
                break;
        }
    }

    public void updFilters(TypeUPD typeUPD, Product p) {
        updFilter(typeUPD, Type.CATEGORIES, p);
        updFilter(typeUPD, Type.BRAND, p);
    }

    private void addImagesInFireProduct(Product p, ArrayList<Uri> temp) {
        p.getImages().sort(new ComparatorImages());
        DB.child(Type.PRODS.get())
                .child(p.getID()).child("images")
                .setValue(p.getImages()).addOnSuccessListener(l -> {
                    clearImages(temp);
                    loading.setValue(false);
                });
    }

    void addProduct(Product p, ArrayList<Uri> tempImages) {
        loading.setValue(true);
        updProducts(TypeUPD.ADD, p);
        for (int i = 0; i < tempImages.size(); ++i) {
            String path = "prod_images/" + p.getID() + "/" + i + ".jpg";
            Uri data = tempImages.get(i);
            storage.child(path).putFile(data).addOnSuccessListener(
                    taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                p.addImage(uri.toString());
                                if (p.getImages().size() != tempImages.size()) return;
                                addImagesInFireProduct(p, tempImages);
                            }));
        }
        updFilters(TypeUPD.ADD, p);
        updUserProducts(TypeUPD.ADD, p);
    }

    void deleteProduct(String ID) {
        Product delProd = getProductOnID(ID);

        updUserProducts(TypeUPD.DEL, delProd);
        updUserBucketDB(TypeUPD.DEL, delProd.getID());
        updProducts(TypeUPD.DEL, delProd);
        updFilters(TypeUPD.DEL, delProd);


        DB.child(Type.BUCKET.get()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userBucket : snapshot.getChildren())
                    for (DataSnapshot d : userBucket.getChildren())
                        if (d.getValue(ItemProduct.class)
                                .ProdID().equals(ID))
                            d.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DB.child(Type.FEEDBACK.get()).child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot feedbackData : snapshot.getChildren()) {
                    Feedback feedback = feedbackData.getValue(Feedback.class);
                    if (feedback == null || feedback.getImages() == null) continue;
                    for (int i = 0; i < feedback.getImages().size(); ++i)
                        storage.child("feedback_images/" +
                                ID + "/" + feedback.getUID() + "/" + i + ".jpg").delete();
                }
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    void updUserBucketDB(TypeUPD typeUPD, String prodID) {
        switch (typeUPD) {
            case ADD:
                for (ItemProduct item : bucket)
                    if (item.ProdID().equals(prodID)) return;

                ItemProduct item = new ItemProduct(prodID);

                DatabaseReference refPush = DB.child(Type.BUCKET.get())
                        .child(user.getUID()).push();
                item.setUID(refPush.getKey());
                bucket.add(item);
                refPush.setValue(item);
                break;
            case DEL:
                ItemProduct delItem = null;
                for (ItemProduct itemBucket : bucket)
                    if (itemBucket.ProdID().equals(prodID)) {
                        delItem = itemBucket;
                        break;
                    }
                if (delItem == null) return;

                bucket.remove(delItem);
                DB.child(Type.BUCKET.get())
                        .child(user.getUID()).child(delItem.getUID())
                        .removeValue();
        }
    }

    ArrayList<Feedback> getFeedbacksFromDB(String prodID) {
        loading.setValue(true);
        ArrayList<Feedback> feedbacks = new ArrayList<>();
        DB.child(Type.FEEDBACK.get()).child(prodID).get().addOnSuccessListener(
                dataSnapshot -> {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feedback nFeed = snapshot.getValue(Feedback.class);
                        if (nFeed.getImages().isEmpty()) nFeed.setImages(null);
                        feedbacks.add(nFeed);
                    }
                    Collections.reverse(feedbacks);
                    loading.setValue(false);
                }
        );
        return feedbacks;
    }

    private void addFeedbackImages(Feedback feedback, String prodID) {
        feedback.getImages().sort(new ComparatorImages());
        DB.child(Type.FEEDBACK.get())
                .child(prodID)
                .child(feedback.getUID())
                .child("images")
                .setValue(feedback.getImages())
                .addOnSuccessListener(l -> loading.setValue(false));
    }

    void addFeedback(Feedback feedback, Product p, ArrayList<Uri> tempImages) {
        HashMap<String, Object> rateMap = new HashMap<>();
        rateMap.put("rate", p.getRate());
        rateMap.put("rateCount", p.getRateCount());
        DB.child(Type.PRODS.get()).child(p.getID()).updateChildren(rateMap);
        if (feedback == null) return;
        if (tempImages.isEmpty() && feedback.getFeedback().isEmpty()) return;

        loading.setValue(true);
        DatabaseReference refNewProd = DB.child(Type.FEEDBACK.get()).child(p.getID()).push();
        feedback.setUID(refNewProd.getKey());
        refNewProd.setValue(feedback);

        for (int i = 0; i < tempImages.size(); ++i) {
            String path = "feedback_images/" + p.getID() + "/" + feedback.getUID() + "/" + i + ".jpg";
            storage.child(path).putFile(tempImages.get(i)).addOnSuccessListener(
                    taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(
                            uri -> {
                                feedback.getImages().add(uri.toString());
                                if (feedback.getImages().size() != tempImages.size())
                                    return;
                                addFeedbackImages(feedback, p.getID());
                                clearImages(tempImages);
                            })
            );
        }
    }

    ArrayList<Product> getProducts() {
        return products;
    }

    ArrayList<ItemFilter> getCategories() {
        return categories;
    }

    String getCategoryImage(ItemFilter filter) {
        for (Product p : products)
            if (filter.getName().equals(p.getCategory()))
                return p.getImages().isEmpty() ? null : p.getImage(0);
        return null;
    }

    ArrayList<ItemFilter> getBrands() {
        return brands;
    }

    ArrayList<ItemProduct> getUserProducts() {
        return userProducts;
    }

    ArrayList<ItemProduct> getBucket() {
        return bucket;
    }

    Product getProductOnID(String ID) {
        for (Product p : products)
            if (p.getID().equals(ID)) return p;
        return null;
    }

    User getUser() {
        return user;
    }

    private char getMarkerSeller() {
        return user.isSeller() ? isSeller : notSeller;
    }

    void updUserName(String newName) {
        if (!user.getName().equals(newName)) {
            FirebaseUser curUser = auth.getCurrentUser();
            if (curUser == null) return;
            curUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(getMarkerSeller() + newName).build())
                    .addOnSuccessListener(unused -> user.setName(newName));
        }
    }

    void updUserImage() {
        loading.setValue(true);
        String path = "user_photo/" + user.getUID() + ".jpg";
        storage.child(path).putFile(user.getTempUserImage()).addOnSuccessListener(
                taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(
                        uri -> auth.getCurrentUser()
                                .updateProfile(new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri).build())
                                .addOnSuccessListener(unused -> {
                                    user.setImage(uri.toString());
                                    clearImage(user.getTempUserImage());
                                    user.setTempUserImage(null);
                                    loading.setValue(false);
                                })
                )
        );
    }

    boolean deleteUser() {
        FirebaseUser fUser = auth.getCurrentUser();
        if (fUser == null) return false;
        fUser.delete();
        for (ItemProduct item : userProducts) {
            deleteProduct(item.ProdID());
        }
        return true;
    }

    void singIn(FragmentSignIn.SingInData data) {
        auth.signInWithEmailAndPassword(
                        data.getEmail(), data.getPass())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fireUser = auth.getCurrentUser();
                    String name = fireUser.getDisplayName();
                    user = new User(name.substring(1),
                            name.charAt(0) == isSeller);
                    if (fireUser.getPhotoUrl() != null)
                        user.setImage(fireUser.getPhotoUrl().toString());
                    user.setUID(fireUser.getUid());
                    data.afterSignIn();
                })
                .addOnFailureListener(data::failSignIn);
    }

    void registerUser(FragmentRegister.RegisterData data) {
        auth.createUserWithEmailAndPassword(
                        data.getEmail(), data.getPass()
                ).addOnSuccessListener(authResult -> {
                    FirebaseUser fireUser = authResult.getUser();
                    user = data.getNewUser();
                    user.setUID(fireUser.getUid());
                    fireUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(getMarkerSeller() + user.getName()).build());
                    data.afterRegister();
                })
                .addOnFailureListener(data::failRegister);
    }

    public void clearImage(Uri image) {
        if (image == null) return;
        String pathUri = image.getPath();
        String name = pathUri.substring(pathUri.lastIndexOf(File.separator));
        File f = new File(Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_PICTURES + name);
        if (f.exists()) f.delete();
    }

    public void clearImages(ArrayList<Uri> tempImages) {
        for (Uri filePath : tempImages) {
            clearImage(filePath);
        }
        tempImages.clear();
    }

    public boolean hasProducts() {
        return !products.isEmpty();
    }

}
