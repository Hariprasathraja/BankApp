package com.demo.app;

import com.demo.app.Bank.AccountDetails;
import com.demo.app.Bank.AccountRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    //CreateAccount Test
    public void createAccountTest() {
        client.createAccount("Hari", 2000.0f);
        AccountRequest request = AccountRequest.newBuilder()
                .setAccountNumber(1007)
                .build();
        AccountDetails response = client.getBlockingStub().getAccountDetails(request);
        assertNotNull(response);
        System.out.println(response.getBalance()+" "+response.getAccountNumber());
        assertEquals("Hari", response.getName());
        assertEquals(2000.0f, response.getBalance());
    }

    @Test
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
}
