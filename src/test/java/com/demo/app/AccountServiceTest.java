package com.demo.app;

import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import com.demo.app.Bank.TransactionHistoryRequest;
import com.demo.app.Bank.TransactionHistoryResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceTest {

    private static ManagedChannel channel;
    private static AccountClient client;

    @BeforeAll
    public static void setUp() {
        channel = ManagedChannelBuilder.forAddress("172.23.0.3", 50051)
                .usePlaintext()
                .build();
        client = new AccountClient(channel);
    }

    @AfterAll
    public static void tearDown() {
        channel.shutdown();
    }

    @Test
    @Order(1)
    //CreateAccount Test
    public void createAccountTest() {
        client.createAccount("Hari", 2000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        System.out.println(response.getBalance()+" "+response.getAccountNumber());
        assertEquals("Hari", response.getName());
        assertEquals(2000.0f, response.getBalance());
    }

    @Test
    @Order(2)
    //GetAccount Test
    public void getAccountTest() {
        client.createAccount("Raja", 10000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(2)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals("Prasath", response.getName());
        assertEquals(10000.0f, response.getBalance());
    }

    @Test
    @Order(3)
    //UpdateAccount Test
    public void updateAccountTest() {
        client.updateAccount(1, "Raja");
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals("Raja", response.getName());
    }

    @Test
    @Order(4)
    //DeleteAccount Test
    public void deleteAccountTest() {
        client.deleteAccount(2);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(2)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals("Unknown", response.getName());
    }

    @Test
    @Order(5)
    //DepositAmount Test
    public void depositAmountTest() {
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        float balance = response.getBalance();
        client.depositAmount(1, 5000.0f);
        request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(5000.0f + balance, response.getBalance());
    }

    @Test
    @Order(6)
    //WithDrawAmount Test
    public void withDrawAmountTest() {
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        float balance = response.getBalance();
        client.withDrawAmount(1, 300.0f);
        request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(balance - 300.0f, response.getBalance());
    }

    @Disabled
    @Test
    //TransferAmount Test
    public void transferAmountTest() {
        client.createAccount("Hari", 6000.0f);
        client.createAccount("Raja", 500.0f);
        client.transferAmount(1, 2, 2000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertEquals(6000.0f - 2000.0f, response.getBalance());

        request = AccountRequest.newBuilder()
                .setAccountNumber(2)
                .build();
        response = client.getBlockingStub().getAccountDetails(request);
        assertEquals(500.0f + 2000.0f, response.getBalance());
    }

    @Test
    @Order(7)
    //NonExistent Account Test
    public void nonExistingAccountTest(){
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(9999)
                .build();

        AccountDetails response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals("Unknown",response.getName());
        assertEquals(0.0f,response.getBalance());
    }

    @Test
    @Order(8)
    //Insufficient Amount WithDraw Test
    public void inSufficientWithDrawTest(){
        client.createAccount("InsufficientAccountTest",100.0f);
        client.withDrawAmount(3,150.0f);
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(1003)
                .build();

        AccountDetails response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(100.0f,response.getBalance());
    }

    @Test
    @Order(9)
    //Insufficient Amount WithDraw Test
    public void inSufficientTransferTest(){
        client.createAccount("fromAccountTest",100.0f);
        client.createAccount("toAccountTest",50.0f);
        client.transferAmount(4, 5,200.0f);
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(4)
                .build();

        AccountDetails response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(100.0f,response.getBalance());

        request=AccountRequest.newBuilder()
                .setAccountNumber(5)
                .build();

        response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(50.0f,response.getBalance());
    }

    @Test
    @Order(10)
    //Negative Deposit Test
    public void negativeDepositTest(){
        client.createAccount("negativeDepositTest",100.0f);
        client.depositAmount(6,-500.0f);
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(6)
                .build();

        AccountDetails response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(100.0f,response.getBalance());
    }

    @Test
    @Order(11)
    //TransactionHistory test
    public void transactionHistoryTest(){
        client.createAccount("TransactionHistoryTest",500.0f);
        client.depositAmount(7,500.0f);
        client.withDrawAmount(7,800.0f);

        TransactionHistoryRequest request= TransactionHistoryRequest.newBuilder()
                .setAccountNumber(7)
                .build();

        TransactionHistoryResponse response=client.getBlockingStub().getTransactionHistory(request);
        assertNotNull(response);
        assertEquals(2,response.getTransactionsCount());
    }
}
