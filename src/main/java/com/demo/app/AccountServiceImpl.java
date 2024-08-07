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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase{
    private final AtomicInteger accountNumGenerator=new AtomicInteger(1000);
    private final AtomicInteger transactionIdCounter= new AtomicInteger(0);
    private final Map<Integer, AccountDetails> accounts=new HashMap<>();
    private final Map<Integer, List<TransactionDetails>> transactionHistory=new HashMap<>();
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
    public void createAccount(CreateAccountRequest request,StreamObserver<CreateAccountResponse> responseObserver){
        int accountNumber=accountNumGenerator.incrementAndGet();
        boolean success=false;
        String userName=request.getName();

        if(userName.length()>3 && !userName.matches(".*[\\d@#$%^&+!=].*")) {
            AccountDetails accountDetails = AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(request.getName())
                    .setBalance(request.getInitialBalance())
                    .build();
            accounts.put(accountNumber, accountDetails);
            success=true;
        }

        CreateAccountResponse response=CreateAccountResponse.newBuilder()
                .setSuccess(success)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //UpdateAccount Service
    @Override
    public void updateAccount(UpdateAccountRequest request,StreamObserver<UpdateAccountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails accountDetails=accounts.get(accountNumber);
        boolean success=true;
        String prevName=accountDetails.getName();

        if(accounts.containsKey(accountNumber)) {
            accountDetails = AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(request.getName())
                    .setBalance(accountDetails.getBalance())
                    .build();
            accounts.put(accountNumber, accountDetails);
        }else{
            success=false;
        }

        UpdateAccountResponse response=UpdateAccountResponse.newBuilder()
                .setSuccess(success)
                .setPreviousName(prevName)
                .build();

        responseObserver.onNext(response);
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
    public void depositAmount(DepositAmountRequest request,StreamObserver<DepositAmountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        boolean success=true;
        String message;
        AccountDetails accountDetails= accounts.get(accountNumber);

        if(accountDetails!=null && request.getDepositAmount()>0){
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(accountDetails.getName())
                    .setBalance(accountDetails.getBalance()+request.getDepositAmount())
                    .build();
            accounts.put(accountNumber,accountDetails);
            message="Deposit Successful!";
            recordTransaction(accountNumber,"Deposit",request.getDepositAmount());
        }else{
            success=false;
            if(accountDetails==null) message="Invalid Account Number.";
            else message="Invalid Deposit Amount.";
        }

        DepositAmountResponse.Builder response=DepositAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message);

        accountDetails=accounts.get(accountNumber);
        if(accountDetails!=null) response.setBalance(accountDetails.getBalance());
        else response.setBalance(0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    //WithDrawAmount Service
    @Override
    public void withDrawAmount(WithDrawAmountRequest request,StreamObserver<WithDrawAmountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails accountDetails=accounts.get(accountNumber);
        boolean success=true;
        String message;

        if(accountDetails!=null && request.getWithDrawAmount()>0 && accountDetails.getBalance()-request.getWithDrawAmount()>=0.0f){
            accountDetails=AccountDetails.newBuilder()
                    .setAccountNumber(accountNumber)
                    .setName(accountDetails.getName())
                    .setBalance(accountDetails.getBalance()-request.getWithDrawAmount())
                    .build();
            accounts.put(accountNumber,accountDetails);
            message="WithDrawn Successful!";
            recordTransaction(accountNumber,"WithDraw",request.getWithDrawAmount());
        }else{
            success=false;
            if(accountDetails==null) message="Account not found.";
            else if(request.getWithDrawAmount()<=0) message="Invalid amount.";
            else message="Insufficient Balance.";
        }

        WithDrawAmountResponse.Builder response=WithDrawAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message);

        accountDetails=accounts.get(accountNumber);
        if(accountDetails!=null) response.setBalance(accountDetails.getBalance());
        else response.setBalance(0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    //TransferAmount Service
    @Override
    public void transferAmount(TransferAmountRequest request,StreamObserver<TransferAmountResponse> responseObserver){
        int fromAccountNumber=request.getFromAccount();
        int toAccountNumber=request.getToAccount();
        float transferAmount =request.getTransferAmount();
        boolean success = false;
        String message;

        if(transferAmount>0) {
            AccountDetails fromAccountDetails = accounts.get(fromAccountNumber);
            AccountDetails toAccountDetails = accounts.get(toAccountNumber);
            if (fromAccountDetails != null && toAccountDetails != null && fromAccountDetails.getBalance() - transferAmount >= 0.0f) {
                fromAccountDetails = AccountDetails.newBuilder()
                        .setAccountNumber(fromAccountNumber)
                        .setName(fromAccountDetails.getName())
                        .setBalance(fromAccountDetails.getBalance() - transferAmount)
                        .build();

                toAccountDetails = AccountDetails.newBuilder()
                        .setAccountNumber(toAccountNumber)
                        .setName(toAccountDetails.getName())
                        .setBalance(toAccountDetails.getBalance() + transferAmount)
                        .build();

                accounts.put(fromAccountNumber, fromAccountDetails);
                accounts.put(toAccountNumber, toAccountDetails);

                success=true;
                message="Amount Transferred Successfully!";
                recordTransaction(fromAccountNumber, "Transfer", -transferAmount);
                recordTransaction(toAccountNumber, "Transfer", transferAmount);
            }else{
                if (fromAccountDetails==null) message="Invalid Account Number.";
                else if (toAccountDetails==null) message="Invalid Recipient Account Number.";
                else message="Insufficient Balance!";
            }
        }else{
            message="Invalid Amount.";
        }

        TransferAmountResponse.Builder response=TransferAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message);
        AccountDetails fromAccountDetails =accounts.get(fromAccountNumber);
        if(fromAccountDetails !=null) response.setBalance(fromAccountDetails.getBalance());
        else response.setBalance(0);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    //Records TransactionDetails
    public void recordTransaction(int accountNumber, String type, float amount){
        int transactionID=transactionIdCounter.incrementAndGet();
        TransactionDetails transactionDetails=TransactionDetails.newBuilder()
                .setTransactionId(transactionID)
                .setType(type)
                .setAmount(amount)
                .setTimeStamp(Instant.now().toString())
                .build();

        transactionHistory.computeIfAbsent(accountNumber,k->new ArrayList<>()).add(transactionDetails);
    }
    //TransactionHistory Service
    @Override
    public void getTransactionHistory(TransactionHistoryRequest request, StreamObserver<TransactionHistoryResponse> responseObserver) {
        int accountNumber=request.getAccountNumber();
        List<TransactionDetails> transactions= transactionHistory.getOrDefault(accountNumber,Collections.emptyList());
        TransactionHistoryResponse.Builder response=TransactionHistoryResponse.newBuilder();
        response.addAllTransactions(transactions);
        responseObserver.onNext(response.build());
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
