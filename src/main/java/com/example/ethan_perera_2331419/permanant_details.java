package com.example.ethan_perera_2331419;

public class permanant_details {
    private static permanant_details instance;
    private String globalDetail = "Null";

    permanant_details() {}

    public static permanant_details getInstance() {
        if (instance == null) {
            instance = new permanant_details();
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
