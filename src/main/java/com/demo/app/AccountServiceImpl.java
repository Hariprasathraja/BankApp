package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase{
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
    public static void main(String[] args)throws IOException, InterruptedException{
        Server server=ServerBuilder.forPort(50051)
                .addService(new AccountServiceImpl())
                .build();

        server.start();
        System.out.println("Server started and listening on port "+ server.getPort());

        server.awaitTermination();
    }
}
