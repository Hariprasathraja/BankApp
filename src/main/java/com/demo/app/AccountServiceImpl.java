package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase{
    private final AtomicInteger accountNumGenerator=new AtomicInteger((1000));

    //Get AccountDetails Service
    @Override
    public void getAccountDetails(AccountRequest request, StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountnumber();

        AccountDetails accountDetails=AccountDetails.newBuilder()
                .setAccountnumber(accountNumber)
                .setName("Hari")
                .setBalance(15000.0f)
                .build();

        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //CreateAccount Service
    @Override
    public void createAccount(CreateAccountRequest request,StreamObserver<AccountDetails> responseObserver){
        int accountNumber=accountNumGenerator.incrementAndGet();
        String name=request.getName();
        float initialBalance=request.getInitialBalance();

        AccountDetails accountDetails=AccountDetails.newBuilder()
                .setAccountnumber(accountNumber)
                .setName(name)
                .setBalance(initialBalance)
                .build();

        responseObserver.onNext(accountDetails);
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
