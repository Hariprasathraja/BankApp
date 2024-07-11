package com.demo.app;

import com.demo.grpc.AccountDetails;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class bank {
    private static final String ACCOUNT_FILE_NAME = "/home/hpr/Protoexample/com/accountdetails.bin";
    private final Map<Integer, Account> accounts = new HashMap<>();
    private final Object lock = new Object();

    public bank() {
        loadAccounts();
    }

    public void createAccount(int accountNumber, String name) {
        synchronized (lock) {
            if (!accounts.containsKey(accountNumber)) {
                Account account = new Account(accountNumber, name, 0);
                accounts.put(accountNumber, account);
                saveAccounts();
                System.out.println("Account created successfully.\n");
            } else {
                System.out.println("Account number already exists.\n");
            }
        }
    }

    public void viewAccount(int accountNumber) {
        synchronized (lock) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                System.out.println("Account Number: " + accountNumber);
                System.out.println("Name: " + account.getName());
                System.out.println("Balance: " + account.getBalance() + "\n");
            } else {
                System.out.println("Account not found.\n");
            }
        }
    }

    public void updateAccount(int accountNumber, String newName) {
        synchronized (lock) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                account.setName(newName);
                saveAccounts();
                System.out.println("Account name updated successfully.\n");
            } else {
                System.out.println("Account not found.\n");
            }
        }
    }

    public void deleteAccount(int accountNumber) {
        synchronized (lock) {
            if (accounts.remove(accountNumber) != null) {
                saveAccounts();
                System.out.println("Account deleted successfully.\n");
            } else {
                System.out.println("Account not found.\n");
            }
        }
    }

    public void deposit(int accountNumber, float amount) {
        synchronized (lock) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                if (amount > 0) {
                    account.setBalance(account.getBalance() + amount);
                    saveAccounts();
                    System.out.println("Amount deposited successfully.\n");
                } else {
                    System.out.println("Invalid amount.\n");
                }
            } else {
                System.out.println("Account not found.\n");
            }
        }
    }

    public void withdraw(int accountNumber, float amount) {
        synchronized (lock) {
            Account account = accounts.get(accountNumber);
            if (account != null) {
                if (amount > 0 && amount <= account.getBalance()) {
                    account.setBalance(account.getBalance() - amount);
                    saveAccounts();
                    System.out.println("Amount withdrawn successfully.\n");
                } else {
                    System.out.println("Invalid amount.\n");
                }
            } else {
                System.out.println("Account not found.\n");
            }
        }
    }

    public void transfer(int fromAccountNumber, int toAccountNumber, float amount) {
        synchronized (lock) {
            Account fromAccount = accounts.get(fromAccountNumber);
            Account toAccount = accounts.get(toAccountNumber);
            if (fromAccount != null && toAccount != null) {
                if (amount > 0 && amount <= fromAccount.getBalance()) {
                    fromAccount.setBalance(fromAccount.getBalance() - amount);
                    toAccount.setBalance(toAccount.getBalance() + amount);
                    saveAccounts();
                    System.out.println("Amount transferred successfully.\n");
                } else {
                    System.out.println("Invalid amount or insufficient funds.\n");
                }
            } else {
                System.out.println("One or both accounts not found.\n");
            }
        }
    }

    public void displayAllAccounts() {
        synchronized (lock) {
            for (Account account : accounts.values()) {
                System.out.println("Account Number: " + account.getAccountNumber());
                System.out.println("Name: " + account.getName());
                System.out.println("Balance: " + account.getBalance());
                System.out.println();
            }
        }
    }

    private void saveAccounts() {
        synchronized (lock) {
            try (FileOutputStream fos = new FileOutputStream(ACCOUNT_FILE_NAME)) {
                for (Account account : accounts.values()) {
                    account.getAccountDetails().writeDelimitedTo(fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAccounts() {
        synchronized (lock) {
            try (FileInputStream fis = new FileInputStream(ACCOUNT_FILE_NAME)) {
                while (fis.available() > 0) {
                    AccountDetails accountDetails = AccountDetails.parseDelimitedFrom(fis);
                    accounts.put(accountDetails.getAccountnumber(), new Account(accountDetails));
                }
            } catch (FileNotFoundException e) {
                System.out.println("No existing data file found.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

