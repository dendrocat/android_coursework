package com.example.app.Models;

import com.example.app.Databases.Database;
import com.example.app.Utils.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Product {
    private String ID;
    private String name;
    private int price;
    private String brand;
    private String category;
    private int minAge;
    private float rate;
    private int rateCount;
    private String desc;
    private final ArrayList<String> images;

    private int index;

    public Product() {
        images = new ArrayList<>();
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() { return index; }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getImage(int pos) {
        if (images.isEmpty()) return Database.getInstance().getImages().get(0).toString();
        return images.get(pos);
    }

    public void addImage(String pathToImage) {
        images.add(pathToImage);
    }

    public int getPrice() {
        return price;
    }

    public String convertPriceToString() {
        return Formatter.getPriceRUB(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public String convertAgeString() {
        if (minAge % 10 == 1 && minAge % 100 != 11) return "от " + minAge + " года";
        return "от " + minAge + " лет";
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }


    public static class ComparatorRate implements Comparator<Product> {
        @Override
        public int compare(Product p1, Product p2) {
            if (p1.rate != p2.rate)
                return -(int) ((p1.rate - p2.rate) * 10000);
            else if (p1.rateCount != p2.rateCount)
                return -(p1.rateCount - p2.rateCount);
            return p1.name.compareTo(p2.name);
        }
    }

    public static class ComparatorOldToNew implements Comparator<Product> {
        @Override
        public Comparator<Product> reversed() {
            return Comparator.super.reversed();
        }

        @Override
        public int compare(Product p1, Product p2) {
            return p1.index - p2.index;
        }
    }

}
