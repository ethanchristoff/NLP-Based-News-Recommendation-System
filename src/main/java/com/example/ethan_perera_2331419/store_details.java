package com.example.ethan_perera_2331419;

public class store_details {
    private static store_details instance;
    private String globalDetail = "Null";

    store_details() {}

    public static store_details getInstance() {
        if (instance == null) {
            instance = new store_details();
        }
        return instance;
    }

    public void setGlobalDetails(String globalDetail) {
        this.globalDetail = globalDetail;
    }

    public String getGlobalDetails() {
        return globalDetail;
    }
}
