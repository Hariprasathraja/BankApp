package com.demo.app;

import java.util.Scanner;

public class BankApp {
    public static void main(String[] args) {
        bank bank = new bank();
        User user = new User(bank);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("****Bank Application Menu****\n");
            System.out.println("1. Create new Account\n2. View Account\n3. Update Account\n4. Delete Account\n5. Deposit Amount\n6. Withdraw Amount\n7. Transfer Account\n8. Display All Account Details\n9. Exit");
            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter account number: ");
                    int accountNumber = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    user.createAccount(accountNumber, name);
                    break;
                case 2:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextInt();
                    user.viewAccount(accountNumber);
                    break;
                case 3:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    user.updateAccount(accountNumber, newName);
                    break;
                case 4:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextInt();
                    user.deleteAccount(accountNumber);
                    break;
                case 5:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextInt();
                    System.out.print("Enter amount to deposit: ");
                    float depositAmount = scanner.nextFloat();
                    user.deposit(accountNumber, depositAmount);
                    break;
                case 6:
                    System.out.print("Enter account number: ");
                    accountNumber = scanner.nextInt();
                    System.out.print("Enter amount to withdraw: ");
                    float withdrawAmount = scanner.nextFloat();
                    user.withdraw(accountNumber, withdrawAmount);
                    break;
                case 7:
                    System.out.print("Enter your account number: ");
                    int fromAccountNumber = scanner.nextInt();
                    System.out.print("Enter recipient account number: ");
                    int toAccountNumber = scanner.nextInt();
                    System.out.print("Enter amount to transfer: ");
                    float transferAmount = scanner.nextFloat();
                    user.transfer(fromAccountNumber, toAccountNumber, transferAmount);
                    break;
                case 8:
                    user.displayAllAccounts();
                    break;
                case 9:
                    System.out.println("Thanks for using.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }

        scanner.close();
    }
}

