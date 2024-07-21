package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import com.demo.app.Bank.UpdateAccountRequest;
import com.demo.app.Bank.DeleteAccountRequest;
import com.demo.app.Bank.DeleteAccountResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase{
    private final AtomicInteger accountNumGenerator=new AtomicInteger((1000));
    private final Map<Integer, AccountDetails> accounts=new HashMap<>();
    //Get AccountDetails Service
    @Override
    public void getAccountDetails(AccountRequest request, StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountNumber();

        AccountDetails accountDetails=accounts.getOrDefault(accountNumber,AccountDetails.newBuilder()
                .setAccountNumber(accountNumber)
                .setName("Unknown")
                .setBalance(0.0f)
                .build());

        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //CreateAccount Service
    @Override
    public void createAccount(CreateAccountRequest request,StreamObserver<AccountDetails> responseObserver){
        int accountNumber=accountNumGenerator.incrementAndGet();

        AccountDetails accountDetails=AccountDetails.newBuilder()
                .setAccountNumber(accountNumber)
                .setName(request.getName())
                .setBalance(request.getInitialBalance())
                .build();
        accounts.put(accountNumber,accountDetails);
        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //UpdateAccount Service
    @Override
    public void updateAccount(UpdateAccountRequest request,StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails accountDetails;
        if(accounts.containsKey(accountNumber)) {
            accountDetails = AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(request.getName())
                    .setBalance(request.getBalance())
                    .build();
            accounts.put(accountNumber, accountDetails);
        }else{
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName("Account not found")
                    .setBalance(0.0f)
                    .build();
        }
        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //DeleteAccount Service
    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        boolean success= accounts.remove(accountNumber)!=null;

        DeleteAccountResponse response=DeleteAccountResponse.newBuilder()
                .setSuccess(success)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    public static void main(String[] args)throws IOException, InterruptedException{
        Server server=ServerBuilder.forPort(50051)
                .addService(new AccountServiceImpl())
                .build();

        server.start();
        System.out.println("Server started and listening on port "+ server.getPort());

        server.awaitTermination();
    }
}
