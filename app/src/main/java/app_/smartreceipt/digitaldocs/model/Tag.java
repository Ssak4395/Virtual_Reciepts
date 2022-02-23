package app_.smartreceipt.digitaldocs.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private String name;
    private int numInArray;
    private boolean selected;

    public Tag (String name, int numInArray) {
        this.name = name;
        this.numInArray = numInArray;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public int getNumInArray() {
        return numInArray;
    }

    public void setNumInArray(int numInArray) {
        this.numInArray = numInArray;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
