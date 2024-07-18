package com.demo.app;

import com.demo.app.Bank;
import com.demo.app.Bank.AccountDetails;

public class Main {
    public static void main(String[] args) {
        System.out.print("Hello and welcome!");
        AccountDetails accountDetails= AccountDetails.newBuilder().setName("Raja").build();
        System.out.println(accountDetails.getName());
    }
}