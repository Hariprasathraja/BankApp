package com.demo.app;

import com.demo.app.Bank;
import com.demo.app.Bank.AccountDetails;

public class Main {
    public static void main(String[] args) {
        System.out.print("Hello and welcome!");
        AccountDetails accountDetails= AccountDetails.newBuilder().setName("Hari").build();
        System.out.println(accountDetails.getName());
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}