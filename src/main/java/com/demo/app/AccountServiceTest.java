package com.demo.app;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;

public class AccountServiceTest {

    private final AccountClient client;
    public AccountServiceTest(ManagedChannel channel){
        client=new AccountClient(channel);
    }
    //CreateAccount Test
    public void createAccountTest(String name, float intialbalance){
        client.createAccount(name, intialbalance);
    }
    //GetAccountDetails Test
    public void getAccountTest(int accountNumber){
        client.getAccountDetails(accountNumber);
    }
    //UpdateAccount Test
    public void updateAccountTest(int accountNumber, String name, float balance){
        client.updateAccount(accountNumber ,name, balance);
    }
    //DeleteAccount Test
    public void deleteAccountTest(int accountNumber){
        client.deleteAccount(accountNumber);
    }
    //DepositAmount Test
    public void depositAmountTest(int accountNumber, float amount){
        client.depositAmount(accountNumber, amount);
    }
    //WithDrawAmount Test
    public void withDrawAmountTest(int accountNumber, float amount){
        client.withDrawAmount(accountNumber, amount);
    }
    //TransferAmount Test
    public void transferAmountTest(int fromAccount, int toAccount, float amount){
        client.transferAmount(fromAccount, toAccount, amount);
    }

    public static void main(String[] args) {
        ManagedChannel channel=ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        AccountServiceTest test=new AccountServiceTest(channel);

        test.createAccountTest("Hari",20000.0f);
        test.createAccountTest("Raja",3000.0f);

        test.getAccountTest(1001);
        test.getAccountTest(1002);

        test.updateAccountTest(1002,"Prasath",3000.0f);

        test.depositAmountTest(1001,5000.0f);

        test.withDrawAmountTest(1001,2000.0f);

        test.transferAmountTest(1001,1002,8000.0f);

        test.deleteAccountTest(1002);

        test.getAccountTest(1002);
    }
}