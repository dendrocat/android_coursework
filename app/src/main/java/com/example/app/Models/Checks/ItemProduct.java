package com.example.app.Models.Checks;

public class ItemProduct extends ItemFilter {

    private int count;

    public ItemProduct() {}

    public ItemProduct(String name) {
        super(name);
        count = 1;
    }

    public String ProdID() { return getName(); }


    public void setCount(int count) { this.count = count; }

    public int getCount() { return count; }

}
