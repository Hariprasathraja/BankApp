package com.demo.app;
import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.CreateAccountRequest;
import com.demo.app.Bank.UpdateAccountRequest;
import com.demo.app.Bank.DeleteAccountRequest;
import com.demo.app.Bank.DeleteAccountResponse;
import com.demo.app.Bank.DepositAmountRequest;
import com.demo.app.Bank.WithDrawAmountRequest;
import com.demo.app.Bank.TransferAmountRequest;
import com.demo.app.Bank.TransferAmountResponse;
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

    //DepositAmount Service
    @Override
    public void depositAmount(DepositAmountRequest request,StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails accountDetails= accounts.get(accountNumber);
        if(accountDetails!=null){
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(accountDetails.getName())
                    .setBalance(accountDetails.getBalance()+request.getDepositAmount())
                    .build();
            accounts.put(accountNumber,accountDetails);
        }else{
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName("Unknown")
                    .setBalance(0.0f)
                    .build();
        }
        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //WithDrawAmount Service
    @Override
    public void withDrawAmount(WithDrawAmountRequest request,StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails accountDetails=accounts.get(accountNumber);

        if(accountDetails!=null && accountDetails.getBalance()-request.getWithDrawAmount()>0.0f){
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(accountDetails.getName())
                    .setBalance(accountDetails.getBalance()-request.getWithDrawAmount())
                    .build();
            accounts.put(accountNumber,accountDetails);
        }else{
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName("Unknown")
                    .setBalance(0.0f)
                    .build();
        }
        responseObserver.onNext(accountDetails);
        responseObserver.onCompleted();
    }

    //TransferAmount Service
    @Override
    public void transferAmount(TransferAmountRequest request,StreamObserver<TransferAmountResponse> responseObserver){
        int fromAccountNumber=request.getFromAccount();
        int toAccountNumber=request.getToAccount();
        float transferAmount =request.getTransferAmount();

        AccountDetails fromAccountDetails=accounts.get(fromAccountNumber);
        AccountDetails toAccountDetails=accounts.get(toAccountNumber);
        boolean success=false;
        if(fromAccountDetails!=null && fromAccountDetails.getBalance()- transferAmount >0.0f){
            fromAccountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(fromAccountNumber)
                    .setName(fromAccountDetails.getName())
                    .setBalance(fromAccountDetails.getBalance()- transferAmount)
                    .build();

            toAccountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(toAccountNumber)
                    .setName(toAccountDetails.getName())
                    .setBalance(toAccountDetails.getBalance()+transferAmount)
                    .build();

            accounts.put(fromAccountNumber,fromAccountDetails);
            accounts.put(toAccountNumber,toAccountDetails);

            success=true;
        }
        TransferAmountResponse response=TransferAmountResponse.newBuilder()
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
