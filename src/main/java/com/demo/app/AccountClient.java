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
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AccountClient {
    private final AccountServiceGrpc.AccountServiceBlockingStub blockingStub;

    public AccountClient(ManagedChannel channel){
        blockingStub=AccountServiceGrpc.newBlockingStub(channel);
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
    public static void main(String[] args){
        ManagedChannel channel=ManagedChannelBuilder.forAddress("accountserver",50051)
                .usePlaintext()
                .build();

        AccountClient client=new AccountClient(channel);

        //Create new Account
        client.createAccount("Hari",20000.0f);
        client.createAccount("Raja",4000.0f);

        //client.updateAccount(1002,"Raja",200050.0f);

        //client.deleteAccount(1001);

        //client.getAccountDetails(1001);

        client.depositAmount(1002,6000.0f);

        client.withDrawAmount(1002,4000.0f);

        client.getAccountDetails(1002);

        client.transferAmount(1001,1002,10000.0f);

        client.getAccountDetails(1002);

        channel.shutdown();
    }
}
