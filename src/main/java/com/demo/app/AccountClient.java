package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub blockingStub;

    public AccountClient(ManagedChannel channel){
        blockingStub=AccountServiceGrpc.newBlockingStub(channel);
    }

    public void getAccountDetails(int accountNumber){
        AccountRequest request=AccountRequest.newBuilder().setAccountnumber(accountNumber).build();
        AccountDetails response=blockingStub.getAccountDetails(request);
        System.out.println("Account name: "+response.getName());
        System.out.println("Account Balance: "+response.getBalance());
    }

    //CreateAccount request
    public void createAccount(String name, float initialBalance){
        CreateAccountRequest request=CreateAccountRequest.newBuilder()
                .setName(name)
                .setInitialBalance(initialBalance)
                .build();

        AccountDetails response=blockingStub.createAccount(request);
        System.out.println("Created Account Number: "+response.getAccountnumber());
        System.out.println("Account name: "+response.getName());
        System.out.println("Account Balance: "+response.getBalance());
    }

    public static void main(String[] args){
        ManagedChannel channel=ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        AccountClient client=new AccountClient(channel);
        client.getAccountDetails(12345);

        //Create new Account
        client.createAccount("Prasath",200060.0f);
        channel.shutdown();
    }
}
