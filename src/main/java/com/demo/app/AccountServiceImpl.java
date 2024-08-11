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

import com.demo.utils.DataBaseUtil;

import io.grpc.Server;
import io.grpc.Status;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase{


    //CreateAccount Service
    @Override
    public void createAccount(CreateAccountRequest request,StreamObserver<CreateAccountResponse> responseObserver){
        boolean success=false;
        String userName=request.getName();

        if(userName.length()>3 && !userName.matches(".*[\\d@#$%^&+!=].*")) {
            try(Connection connection=DataBaseUtil.getConnection()){
                String query="Insert Into accounts (account_name,balance,status) Values (?,?,'active')";
                PreparedStatement statement= connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1,userName);
                statement.setFloat(2,request.getInitialBalance());

                int affectedRows=statement.executeUpdate();
                if(affectedRows>0){
                    success=true;
                    ResultSet generatedKeys=statement.getGeneratedKeys();
                    if(generatedKeys.next()){
                        int accountNumber =generatedKeys.getInt(1);
                        System.out.println("Created accounts with account number: "+accountNumber);
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
                responseObserver.onError(Status.INTERNAL.withDescription("Database error"+e.getMessage()).withCause(e).asRuntimeException());
                return;
            }
        }

        CreateAccountResponse response=CreateAccountResponse.newBuilder()
                .setSuccess(success)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    //GetAccountDetails Service
    @Override
    public void getAccountDetails(AccountRequest request, StreamObserver<AccountDetails> responseObserver){
        int accountNumber=request.getAccountNumber();
        AccountDetails.Builder accountDetails=AccountDetails.newBuilder()
                .setAccountNumber(accountNumber);

        try(Connection connection=DataBaseUtil.getConnection()){
            String query="Select * From accounts Where account_id= ?";
            PreparedStatement statement=connection.prepareStatement(query);
            statement.setInt(1,accountNumber);

            ResultSet resultSet=statement.executeQuery();

            if(resultSet.next()){
                String status=resultSet.getString("status");
                if("inactive".equals(status)){
                    accountDetails.setName("Account  inactive");
                    accountDetails.setBalance(0.0f);
                }else {
                    accountDetails.setName(resultSet.getString("account_name"))
                            .setBalance(resultSet.getFloat("balance"));
                }
            }else{
                accountDetails.setName("Account not found.")
                        .setBalance(0.0f);
            }
        }catch (SQLException e){
            e.printStackTrace();
            responseObserver.onError(Status.UNKNOWN.withDescription("Database error: "+e.getMessage()).withCause(e).asRuntimeException());
            return;
        }

        responseObserver.onNext(accountDetails.build());
        responseObserver.onCompleted();
    }


    //UpdateAccount Service
    @Override
    public void updateAccount(UpdateAccountRequest request,StreamObserver<UpdateAccountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        String prevName="";
        boolean success=true;
        try(Connection connection=DataBaseUtil.getConnection()) {
            String query = "Select * from accounts Where account_id= ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, accountNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        prevName = resultSet.getString("account_name");
                    } else {
                        success = false;
                    }
                }
            }
            if (success) {
                query = "Update accounts Set account_name= ? Where account_id= ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, request.getName());
                    statement.setInt(2, accountNumber);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected == 0) {
                        success = false;
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            responseObserver.onError(e);
            return;
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
        boolean success= false;
        String message;

        try(Connection connection=DataBaseUtil.getConnection()) {
            String query = "Update accounts Set status='inactive' Where account_id= ?";
            try(PreparedStatement deleteStatement = connection.prepareStatement(query)){
                deleteStatement.setInt(1, accountNumber);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    message="Account deactivated successfully.";
                }else{
                    message="Account not found.";
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            responseObserver.onError(Status.UNKNOWN.withDescription("Database error: " + e.getMessage()).withCause(e).asRuntimeException());
            responseObserver.onError(e);
            return;
        }
        DeleteAccountResponse response=DeleteAccountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    //DepositAmount Service
    @Override
    public void depositAmount(DepositAmountRequest request,StreamObserver<DepositAmountResponse> responseObserver) {
        int accountNumber = request.getAccountNumber();
        boolean success = false;
        float newBalance=0;
        String message;

        try (Connection connection = DataBaseUtil.getConnection()) {
            connection.setAutoCommit(false);
            String query = "Select * From accounts Where account_id= ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    if (request.getDepositAmount() > 0) {
                        newBalance = resultSet.getFloat("balance") + request.getDepositAmount();
                        query = "Update accounts Set balance= ? Where account_id= ?";

                        try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                            updateStatement.setFloat(1, newBalance);
                            updateStatement.setInt(2, accountNumber);
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                query = "Insert Into transactions (account_id, type, amount) Values (?,?,?)";
                                try (PreparedStatement transactionStatement = connection.prepareStatement(query)) {
                                    transactionStatement.setInt(1, accountNumber);
                                    transactionStatement.setString(2, "Deposit");
                                    transactionStatement.setFloat(3, request.getDepositAmount());
                                    transactionStatement.executeUpdate();

                                    connection.commit();
                                    success=true;
                                    message = "Deposit Successful";
                                }
                            } else {
                                message = "Failed to Deposit";
                                connection.rollback();
                            }
                        }
                    } else {
                        message = "Invalid Deposit amount";
                    }
                }else {
                    message="Invalid account number.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseObserver.onError(e);
            return;
        }

        DepositAmountResponse response = DepositAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setBalance(newBalance)
                .build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    //WithDrawAmount Service
    @Override
    public void withDrawAmount(WithDrawAmountRequest request,StreamObserver<WithDrawAmountResponse> responseObserver){
        int accountNumber=request.getAccountNumber();
        boolean success=false;
        float balance=0;
        String message;

        try(Connection connection=DataBaseUtil.getConnection()){
            connection.setAutoCommit(false);
            String query="Select * From accounts where account_id= ?";

            try(PreparedStatement statement= connection.prepareStatement(query)){
                statement.setInt(1,accountNumber);
                ResultSet resultSet=statement.executeQuery();

                if(resultSet.next()) {
                    float newBalance = resultSet.getFloat("balance") - request.getWithDrawAmount();

                    if (request.getWithDrawAmount() > 0 && newBalance >= 0.0f) {
                        query = "Update accounts Set balance= ? Where account_id= ?";
                        balance=newBalance;
                        try (PreparedStatement withDrawStatement = connection.prepareStatement(query)) {
                            withDrawStatement.setFloat(1, newBalance);
                            withDrawStatement.setInt(2, accountNumber);
                            int rowAffected = withDrawStatement.executeUpdate();

                            if (rowAffected > 0) {
                                query = "Insert Into transactions (account_id,type,amount) Values (?,?,?)";
                                try (PreparedStatement transactionStatement = connection.prepareStatement(query)) {
                                    transactionStatement.setInt(1, accountNumber);
                                    transactionStatement.setString(2, "WithDraw");
                                    transactionStatement.setFloat(3, -request.getWithDrawAmount());
                                    transactionStatement.executeUpdate();

                                    connection.commit();
                                    success=true;
                                    message = "Withdrawal successful";
                                }
                            } else {
                                message = "Failed to withDraw.";
                                connection.rollback();
                            }
                        }
                    } else {
                        if (request.getWithDrawAmount() < 1) message = "Invalid Amount.";
                        else message = "Insufficient Balance.";
                    }
                }else {
                    message="Account not found.";
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            responseObserver.onError(Status.UNKNOWN.withDescription("Database error: " + e.getMessage()).withCause(e).asRuntimeException());
            responseObserver.onError(e);
            return;
        }

        WithDrawAmountResponse response=WithDrawAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setBalance(balance)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    //TransferAmount Service
    @Override
    public void transferAmount(TransferAmountRequest request,StreamObserver<TransferAmountResponse> responseObserver){
        int fromAccountNumber=request.getFromAccount();
        int toAccountNumber=request.getToAccount();
        float transferAmount =request.getTransferAmount(), balance=0;
        boolean success = false;
        String message;

        if(transferAmount>0) {
            try (Connection connection = DataBaseUtil.getConnection()) {
                connection.setAutoCommit(false);
                String query ="Select * From accounts Where account_id= ?";

                try (PreparedStatement fromStatement= connection.prepareStatement(query)){
                    fromStatement.setInt(1,fromAccountNumber);
                    try(ResultSet fromResultSet=fromStatement.executeQuery()) {
                        if (fromResultSet.next()) {
                            float fromBalance = fromResultSet.getFloat("balance") - transferAmount;
                            if (fromBalance >= 0.0f) {

                                try (PreparedStatement toStatement = connection.prepareStatement(query)) {
                                    toStatement.setInt(1,toAccountNumber);
                                    try(ResultSet toResultSet=toStatement.executeQuery()) {
                                        if (toResultSet.next()) {
                                            query = "Update accounts Set balance= ? Where account_id= ?";

                                            try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                                                updateStatement.setFloat(1, fromBalance);
                                                updateStatement.setInt(2,fromAccountNumber);
                                                updateStatement.executeUpdate();

                                                float toBalance=toResultSet.getFloat("balance")+transferAmount;
                                                updateStatement.setFloat(1,toBalance);
                                                updateStatement.setInt(2,toAccountNumber);
                                                updateStatement.executeUpdate();

                                                query="Insert Into transactions (account_id, type, amount) Values (?,?,?)";
                                                try(PreparedStatement transactionStatement=connection.prepareStatement(query)){
                                                    transactionStatement.setInt(1,fromAccountNumber);
                                                    transactionStatement.setString(2,"Transfer out");
                                                    transactionStatement.setFloat(3,-transferAmount);
                                                    transactionStatement.executeUpdate();

                                                    transactionStatement.setInt(1,toAccountNumber);
                                                    transactionStatement.setString(2,"Transfer in");
                                                    transactionStatement.setFloat(3,transferAmount);
                                                    transactionStatement.executeUpdate();

                                                    balance=fromBalance;
                                                    connection.commit();
                                                    success=true;
                                                    message="Transfer successful.";
                                                }
                                            }
                                        } else {
                                            message = "Invalid Recipient account number.";
                                        }
                                    }
                                }
                            }else {
                                message="Insufficient Balance.";
                            }
                        }else {
                            message="Invalid sender account number.";
                        }
                    }
                }catch (SQLException e){
                    connection.rollback();
                    e.printStackTrace();
                    responseObserver.onError(Status.UNKNOWN.withDescription("Database error: " + e.getMessage()).withCause(e).asRuntimeException());
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                responseObserver.onError(Status.UNKNOWN.withDescription("Database error: " + e.getMessage()).withCause(e).asRuntimeException());
                return;
            }
        }else{
            message="Invalid amount.";
        }

        TransferAmountResponse response=TransferAmountResponse.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .setBalance(balance)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    //TransactionHistory Service
    @Override
    public void getTransactionHistory(TransactionHistoryRequest request, StreamObserver<TransactionHistoryResponse> responseObserver) {
        int accountNumber=request.getAccountNumber();
        TransactionHistoryResponse.Builder response=TransactionHistoryResponse.newBuilder();

        try(Connection connection=DataBaseUtil.getConnection()){

            String transactionHistoryQuery="Select * From transactions Where account_id= ? Order By timestamp Desc";

            try (PreparedStatement transactionStatement= connection.prepareStatement(transactionHistoryQuery)){
                transactionStatement.setInt(1,accountNumber);

                try (ResultSet resultSet=transactionStatement.executeQuery()){
                    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    while (resultSet.next()){
                        Timestamp timestamp=resultSet.getTimestamp("timestamp");
                        String formattedTimestamp=dateFormat.format(timestamp);

                        TransactionDetails transactionDetails=TransactionDetails.newBuilder()
                                .setTransactionId(resultSet.getInt("transaction_id"))
                                .setAccountNumber(resultSet.getInt("account_id"))
                                .setType(resultSet.getString("type"))
                                .setAmount(resultSet.getFloat("amount"))
                                .setTimeStamp(formattedTimestamp)
                                .build();

                        response.addTransactions(transactionDetails);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            responseObserver.onError(Status.UNKNOWN.withDescription("Database error: "+e.getMessage()).withCause(e).asRuntimeException());
            return;
        }
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
