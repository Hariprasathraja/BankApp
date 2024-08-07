package com.demo.app;

import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import com.demo.app.Bank.CreateAccountResponse;
import com.demo.app.Bank.UpdateAccountRequest;
import com.demo.app.Bank.UpdateAccountResponse;
import com.demo.app.Bank.DeleteAccountRequest;
import com.demo.app.Bank.DeleteAccountResponse;
import com.demo.app.Bank.DepositAmountRequest;
import com.demo.app.Bank.DepositAmountResponse;
import com.demo.app.Bank.WithDrawAmountRequest;
import com.demo.app.Bank.WithDrawAmountResponse;
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
    public void updateAccount(int accountNumber, String name){
        UpdateAccountRequest request= UpdateAccountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .setName(name)
                .setBalance(0.0f)
                .build();

        UpdateAccountResponse response=blockingStub.updateAccount(request);
        if(response.getSuccess()){
            System.out.println("Account name changed successfully from "+response.getPreviousName()+" to "+name+".");
        }else{
            System.out.println("Invalid Account Number.");
        }
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

        DepositAmountResponse response=blockingStub.depositAmount(request);
        if(response.getSuccess()){
            System.out.println(response.getMessage());
            System.out.println("Balance: "+response.getBalance());
        }else{
            System.out.println(response.getMessage());
        }
        System.out.println();
    }

    //WithDrawAmount request
    public void withDrawAmount(int accountNumber, float amount){
        WithDrawAmountRequest request=WithDrawAmountRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .setWithDrawAmount(amount)
                .build();

        WithDrawAmountResponse response=blockingStub.withDrawAmount(request);
        if(response.getSuccess()){
            System.out.println(response.getMessage());
            System.out.println("Balance: "+response.getBalance());
        }else{
            System.out.println(response.getMessage());
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
            System.out.println(response.getMessage());
            System.out.println("Balance: "+response.getBalance());
        }else{
            System.out.println(response.getMessage());
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
