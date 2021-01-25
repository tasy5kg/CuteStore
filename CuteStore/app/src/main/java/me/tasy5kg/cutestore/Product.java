package me.tasy5kg.cutestore;


import org.litepal.crud.LitePalSupport;

public class Product extends LitePalSupport {
    private final String barcode;
    private final String productName;
    private final String price;

    public Product(String barcode, String productName, String price) {
        this.barcode = barcode;
        this.productName = productName;
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

}
