package app_.smartreceipt.digitaldocs.model;

import java.io.Serializable;
import java.util.Locale;

public class ReceiptLineItem implements Serializable {
    private String item;
    private double price;
    private boolean itemFound;
    private ReceiptEntity receiptOwner;

    private final int displayLength = 38;

    public ReceiptLineItem(String itemName, double price) {
        this.item = itemName;
        this.price = price;
        itemFound = false;
        receiptOwner = null;
    }

    public void setReceiptOwner(ReceiptEntity receiptOwner) {
        this.receiptOwner = receiptOwner;
    }

    public ReceiptEntity getReceiptOwner() {
        return receiptOwner;
    }

    public String getItem() {
        return item;
    }

    public String displayItem() {
        String displayName = item;

        if (item.length() > displayLength) {
            String[] split = item.split("");
            displayName = "";
            for (int i = 0; i < displayLength; i++) {
                displayName += split[i];
            }
            displayName += "...";
        }

        return displayName;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceString() {
        return Currency.getSymbol() + String.format(Locale.getDefault(),"%.2f", price);
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItemFound(boolean foundItem) {
        this.itemFound = foundItem;
    }

    public boolean isItemFound() {
        return itemFound;
    }
}
