package com.example.ethan_perera_2331419.services;

public class store_user_details {
    private static store_user_details instance;
    private String globalDetail = "Null";

    public store_user_details() {}

    public static store_user_details getInstance() {
        if (instance == null) {
            instance = new store_user_details();
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
