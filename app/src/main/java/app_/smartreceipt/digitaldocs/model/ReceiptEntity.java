package app_.smartreceipt.digitaldocs.model;


import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app_.smartreceipt.digitaldocs.R;

public class ReceiptEntity implements Serializable {

    private String documentID;
    private String storeName;
    private int imagePath;
    private String date;
    private List<Tag> tags = new ArrayList<>();
    private List<ReceiptLineItem> lineItems = new ArrayList<>();
    private double totalPrice;

    /*
    A receipt has a store name, date of purchase, total price and line item names and prices
    We have to separate the line items into two arrays as firebase doesn't support nested arrays or maps
    or anything like that
     */
    public ReceiptEntity(String storeName, String date, double totalPrice) {
        this.storeName = storeName;
        lookUpCompany();
        this.date = date;
        this.totalPrice = totalPrice;
    }

    public ReceiptEntity() { // to add info from custom classes to firebase we need an empty constructor
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDate() {
        return date;
    }

    public String getFormattedDate() {
        String[] splitDate = date.split("/");
        int repYear = Integer.parseInt(splitDate[0]);
        int repMonth = Integer.parseInt(splitDate[1]);
        int repDay = Integer.parseInt(splitDate[2]);
        Calendar cal = Calendar.getInstance();
        cal.set(repYear,repMonth-1,repDay);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return df.format(cal.getTime());
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLineItems(List<ReceiptLineItem> lineItems) {
        for (ReceiptLineItem lineItem : lineItems) {
            lineItem.setReceiptOwner(this);
        }
        this.lineItems = lineItems;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<ReceiptLineItem> getLineItems() {
        return lineItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getTotalPriceStirng() {
        return Currency.getSymbol() + String.format("%.2f", totalPrice);
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String toStirng() {
        String s = storeName + "," + date + "," + totalPrice + "," + imagePath;

        for (ReceiptLineItem lineItem : lineItems) {
            s += "," + lineItem.getItem() + "," + (lineItem.getPrice() + "");
        }

        return s + "\n";
    }

    public void setImagePath(int ip) {
        imagePath = ip;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setID(String id) {
        documentID = id;
    }

    public String getID() {
        return documentID;
    }

    public void lookUpCompany() {
        if (storeName.toLowerCase().contains("coles")) {
            storeName = "Coles Supermarket";
            imagePath = R.drawable.coles;
        } else if (storeName.toLowerCase().contains("woolworths")) {
            storeName = "Woolworths Group";
            imagePath = R.drawable.woolworth3;
        } else if (storeName.toLowerCase().contains("jb hi") || storeName.toLowerCase().contains("jbhi") || storeName.toLowerCase().contains("jb-hi")) {
            storeName = "JB Hi-Fi";
            imagePath = R.drawable.jbhifi;
        }
    }
}
