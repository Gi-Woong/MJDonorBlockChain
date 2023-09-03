package org.web3j.generated.contracts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJDonorBlockChainTest {
    private static Web3j web3j;
    private static Credentials credentials;
    private static DefaultGasProvider defaultGasProvider;
    private static String donorPrivateKey;
    private static String recipientPrivateKey;
    private static DonationToken donationToken;

    @BeforeAll
    static void setup() throws Exception {
        donorPrivateKey = "0x14c9281398cb546d20e8ad4006d1de4f9b1336509e30ff7abd19dbd2e884c59e";
        recipientPrivateKey = "0xd13bce0eff25e852c0239d83e12ad25bd9a2b30c92e113d555633e0fa5506f69";

        web3j = Web3j.build(new HttpService("http://127.0.0.1:8545"));
        credentials = Credentials.create(donorPrivateKey);
        defaultGasProvider = new DefaultGasProvider();

        donationToken = DonationToken.deploy(web3j, credentials, defaultGasProvider, "MJDonor", "MJD", BigInteger.ZERO).send();
    }


    @Test
    public void testTokenTransfer() throws Exception {
        DonationToken recipientToken = DonationToken.load(donationToken.getContractAddress(), web3j, Credentials.create(recipientPrivateKey), defaultGasProvider);

        BigInteger initialBalance = donationToken.balanceOf(credentials.getAddress()).send();
        BigInteger transferAmount = BigInteger.valueOf(100);

        // Transfer tokens from donor to recipient
        TransactionReceipt transferReceipt = donationToken.transfer(recipientToken.getContractAddress(), transferAmount).send();

        BigInteger donorFinalBalance = donationToken.balanceOf(credentials.getAddress()).send();
        BigInteger recipientFinalBalance = donationToken.balanceOf(recipientToken.getContractAddress()).send();

        //assert
        assertTrue(transferReceipt.isStatusOK());
        assertEquals(initialBalance.subtract(transferAmount), donorFinalBalance);
        assertEquals(transferAmount, recipientFinalBalance);
    }
}
