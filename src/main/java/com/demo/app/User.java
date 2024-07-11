package com.demo.app;

public class User {
    private final bank bank;

    public User(bank bank) {
        this.bank = bank;
    }

    public void createAccount(int accountNumber, String name) {
        bank.createAccount(accountNumber, name);
    }

    public void viewAccount(int accountNumber) {
        bank.viewAccount(accountNumber);
    }

    public void updateAccount(int accountNumber, String newName) {
        bank.updateAccount(accountNumber, newName);
    }

    public void deleteAccount(int accountNumber) {
        bank.deleteAccount(accountNumber);
    }

    public void deposit(int accountNumber, float amount) {
        bank.deposit(accountNumber, amount);
    }

    public void withdraw(int accountNumber, float amount) {
        bank.withdraw(accountNumber, amount);
    }

    public void transfer(int fromAccountNumber, int toAccountNumber, float amount) {
        bank.transfer(fromAccountNumber, toAccountNumber, amount);
    }

    public void displayAllAccounts() {
        bank.displayAllAccounts();
    }
}
