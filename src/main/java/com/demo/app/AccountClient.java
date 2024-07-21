package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import com.demo.app.Bank.UpdateAccountRequest;
import com.demo.app.Bank.DeleteAccountRequest;
import com.demo.app.Bank.DeleteAccountResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub blockingStub;

    public AccountClient(ManagedChannel channel){
        blockingStub=AccountServiceGrpc.newBlockingStub(channel);
    }

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

        AccountDetails response=blockingStub.createAccount(request);
        System.out.println("Created Account Number: "+response.getAccountNumber());
        System.out.println("Account name: "+response.getName());
        System.out.println("Account Balance: "+response.getBalance());
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
    public static void main(String[] args){
        ManagedChannel channel=ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        AccountClient client=new AccountClient(channel);

        //Create new Account
        client.createAccount("Hari",200060.0f);
        client.createAccount("Raja",4000.0f);

        client.updateAccount(1002,"Raja",200050.0f);

        client.deleteAccount(1001);
        client.getAccountDetails(1001);
        channel.shutdown();
    }
}
