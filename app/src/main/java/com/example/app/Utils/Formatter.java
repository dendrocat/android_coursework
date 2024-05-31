package com.example.app.Utils;

import com.example.app.Databases.Database;
import com.example.app.Models.Checks.ItemProduct;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Formatter {

    private static final Database db = Database.getInstance();

    public static String getFormattedRate(float rate) {
        DecimalFormatSymbols s = DecimalFormatSymbols.getInstance();
        s.setDecimalSeparator('.');
        return new DecimalFormat("0.0", s).format(rate);
    }

    public static String getPriceRUB(int price) {

        DecimalFormat df = new DecimalFormat();

        df.setGroupingUsed(true);
        df.setGroupingSize(3);

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');

        df.setDecimalFormatSymbols(dfs);

        return df.format(price) + " ₽";
    }

    public static String getPriceBucket() {
        int price = 0;

        for (ItemProduct item : db.getBucket())
            price += item.isChecked() ?
                    db.getProductOnID(item.ProdID()).getPrice() * item.getCount() : 0;

        return getPriceRUB(price);
    }
}
