package com.example.ethan_perera_2331419;

import com.example.ethan_perera_2331419.services.fundamental_tools;
import org.junit.Test;

import java.util.Objects;

public class password_validation_test {

    // The password is validated based on the following criteria:
    // - length must be greater than 0 or less than (or equal to) 12
    // - Password must contain a minimum of 3 numeric chars
    // - Password must contain at least one special char

    private fundamental_tools tools = new fundamental_tools();
    @Test
    public void missing_special_char(){
        String[] result = tools.validatePassword("123");
        if (Objects.equals(result[0], "false")){
            System.out.println(result[1]);
        }
    }

    @Test
    public void password_length_validation(){
        String[] result = tools.validatePassword("");
        if (Objects.equals(result[0], "false")){
            System.out.println(result[1]);
        }
    }

    @Test
    public void missing_numbers(){
        String[] result = tools.validatePassword("abc");
        if (Objects.equals(result[0], "false")){
            System.out.println(result[1]);
        }
    }
}