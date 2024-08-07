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
public class JUnitAccountServiceTests {

    private static ManagedChannel channel;
    private static AccountClient client;

    @BeforeAll
    public static void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
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
                .setAccountNumber(1001)
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
        client.createAccount("Prasath", 10000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1002)
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
        client.updateAccount(1001, "Raja");
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1001)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals("Raja", response.getName());
    }

    @Test
    @Order(4)
    //DeleteAccount Test
    public void deleteAccountTest() {
        client.deleteAccount(1002);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1002)
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
                .setAccountNumber(1001)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        float balance = response.getBalance();
        client.depositAmount(1001, 5000.0f);
        request = AccountRequest.newBuilder()
                .setAccountNumber(1001)
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
                .setAccountNumber(1001)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        float balance = response.getBalance();
        client.withDrawAmount(1001, 300.0f);
        request = AccountRequest.newBuilder()
                .setAccountNumber(1001)
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
        client.transferAmount(1001, 1002, 2000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1001)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertEquals(6000.0f - 2000.0f, response.getBalance());

        request = AccountRequest.newBuilder()
                .setAccountNumber(1002)
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
        client.withDrawAmount(1003,150.0f);
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
        client.transferAmount(1004, 1005,200.0f);
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(1004)
                .build();

        AccountDetails response=client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        assertEquals(100.0f,response.getBalance());

        request=AccountRequest.newBuilder()
                .setAccountNumber(1005)
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
        client.depositAmount(1006,-500.0f);
        AccountRequest request=AccountRequest.newBuilder()
                .setAccountNumber(1006)
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
        client.depositAmount(1007,500.0f);
        client.withDrawAmount(1007,800.0f);

        TransactionHistoryRequest request= TransactionHistoryRequest.newBuilder()
                .setAccountNumber(1007)
                .build();

        TransactionHistoryResponse response=client.getBlockingStub().getTransactionHistory(request);
        assertNotNull(response);
        assertEquals(2,response.getTransactionsCount());
    }
}
