package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import com.demo.app.Bank.CreateAccountResponse;
import com.demo.app.Bank.UpdateAccountRequest;
import com.demo.app.Bank.DeleteAccountRequest;
import com.demo.app.Bank.DeleteAccountResponse;
import com.demo.app.Bank.DepositAmountRequest;
import com.demo.app.Bank.WithDrawAmountRequest;
import com.demo.app.Bank.TransferAmountRequest;
import com.demo.app.Bank.TransferAmountResponse;
import com.demo.app.Bank.TransactionDetails;
import com.demo.app.Bank.TransactionHistoryRequest;
import com.demo.app.Bank.TransactionHistoryResponse;

import io.grpc.ManagedChannel;

public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub blockingStub;

    public AccountClient(ManagedChannel channel){
        blockingStub=AccountServiceGrpc.newBlockingStub(channel);
    }

    public AccountServiceGrpc.AccountServiceBlockingStub getBlockingStub(){
        return blockingStub;
    }
    //GetAccountDetails request
    public void getAccountDetails(int accountNumber){
        AccountRequest request=AccountRequest.newBuilder().setAccountNumber(accountNumber).build();
        AccountDetails response=blockingStub.getAccountDetails(request);
        System.out.println("Account name: "+response.getName());
        System.out.println("Account Balance: "+response.getBalance());
        System.out.println();
    }

    //CreateAccount request
    public void createAccount(String name, float initialBalance){
        CreateAccountRequest request=CreateAccountRequest.newBuilder()
                .setName(name)
                .setInitialBalance(initialBalance)
                .build();

        CreateAccountResponse response=blockingStub.createAccount(request);
        if(response.getSuccess()){
            System.out.println("Account created successfully!");
        }else{
            System.out.println("Invalid username!");
            System.out.println("Username should not contains digits or special characters and the minimum length is 4.");
        }
        System.out.println();
    }

    //UpdateAccount request
    public void updateAccount(int accountNumber, String name, float balance){
        UpdateAccountRequest request= UpdateAccountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .setName(name)
                .setBalance(balance)
                .build();

        AccountDetails response=blockingStub.updateAccount(request);
        System.out.println("Account Number: "+response.getAccountNumber());
        System.out.println("Updated name: "+response.getName());
        System.out.println("Account Balance: "+response.getBalance());
        System.out.println();
    }

    //DeleteAccount request
    public void deleteAccount(int accountNumber) {
        DeleteAccountRequest request = DeleteAccountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .build();

        DeleteAccountResponse response = blockingStub.deleteAccount(request);
        if (response.getSuccess()) {
            System.out.println("Account "+accountNumber+" deleted successfully");
        }else{
            System.out.println("Account not found/ Failed to delete account");
        }
        System.out.println();
    }

    //DepositAmount request
    public void depositAmount(int accountNumber, float amount){
        DepositAmountRequest request=DepositAmountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .setDepositAmount(amount)
                .build();

        AccountDetails response=blockingStub.depositAmount(request);
        if(response.getBalance()==0.0f){
            System.out.println("Account "+accountNumber+" not found.");
        }else{
            System.out.println("Account number: "+response.getAccountNumber());
            System.out.println("Amount: "+amount+" successfully deposited");
            System.out.println("Total Balance: "+response.getBalance());
        }
        System.out.println();
    }

    //WithDrawAmount request
    public void withDrawAmount(int accountNumber, float amount){
        WithDrawAmountRequest request=WithDrawAmountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .setWithDrawAmount(amount)
                .build();

        AccountDetails response=blockingStub.withDrawAmount(request);
        if(response.getBalance()==0.0f){
            System.out.println("Account "+accountNumber+" not found or Insufficient Balance!!!");
        }else{
            System.out.println("Account number: "+response.getAccountNumber());
            System.out.println("Amount: "+amount+" successfully withDrawn");
            System.out.println("Total Balance: "+response.getBalance());
        }
        System.out.println();
    }

    //TransferAmount request
    public void transferAmount(int fromAccount, int toAccount, float transferAmount){
        TransferAmountRequest request=TransferAmountRequest.newBuilder()
                .setFromAccount(fromAccount)
                .setToAccount(toAccount)
                .setTransferAmount(transferAmount)
                .build();

        TransferAmountResponse response=blockingStub.transferAmount(request);
        if(response.getSuccess()){
            System.out.println("Transfer successful.");
        }else{
            System.out.println("Transfer failed.");
        }
        System.out.println();
    }

    //Transaction History
    public void transactionHistory(int accountNumber){
        TransactionHistoryRequest request=TransactionHistoryRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .build();

        TransactionHistoryResponse response=blockingStub.getTransactionHistory(request);
        System.out.println("Transaction history for account number: "+accountNumber);
        for(TransactionDetails transaction: response.getTransactionsList()){
            System.out.println("Transaction ID: "+transaction.getTransactionId());
            System.out.println("Type: "+transaction.getType());
            System.out.println("Amount: "+transaction.getAmount());
            System.out.println("TimeStamp: "+transaction.getTimeStamp());
            System.out.println();
        }
    }
}
