package org.web3j;

import org.web3j.crypto.Credentials;
import org.web3j.generated.contracts.DonationContract;
import org.web3j.generated.contracts.DonationToken;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

/**
 * <p>This is the generated class for <code>web3j new helloworld</code></p>
 * <p>It deploys the Hello World contract in src/main/solidity/ and prints its address</p>
 * <p>For more information on how to run this project, please refer to our <a href="https://docs.web3j.io/latest/command_line_tools/#running-your-application">documentation</a></p>
 */
public class MJDonorBlockChain {
    private static final String nodeUrl = System.getenv().getOrDefault("WEB3J_NODE_URL", "http://127.0.0.1:8545");
//   private static final String walletPassword = System.getenv().getOrDefault("WEB3J_WALLET_PASSWORD", "<wallet_password>");
//   private static final String walletPath = System.getenv().getOrDefault("WEB3J_WALLET_PATH", "<wallet_path>");

    public static void main(String[] args) throws Exception {

//        Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletPath);
        String account0 = "0xf472a171dc35fD30a2462dD5CDCF5F39b26dc390";
        String account1 = "0xc3591719DE53ad772C9d18CD8c25e44541d3b26e";
        String privateKey0 = "0xd13bce0eff25e852c0239d83e12ad25bd9a2b30c92e113d555633e0fa5506f69"; //ganache private key
        String privateKey1 = "0x14c9281398cb546d20e8ad4006d1de4f9b1336509e30ff7abd19dbd2e884c59e"; //ganache private key
//        Credentials credentials = Credentials.create(privateKey0);
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
        DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
//       StaticGasProvider staticGasProvider = new StaticGasProvider(BigInteger.valueOf(4_100_000_000_000_000L), BigInteger.valueOf(100_000_000_000_000_000L));
        System.out.println("defaultGasProvider.getGasLimit() = " + defaultGasProvider.getGasLimit());
        // user0 deploy Hello
//        HelloWorld helloWorld = HelloWorld.deploy(web3j, Credentials.create(privateKey0), defaultGasProvider, "Hello Blockchain World!").send();
//        System.out.println("Contract address: " + helloWorld.getContractAddress());
//       System.out.println("Greeting method result: " + helloWorld.greeting().send());

        // user0 deploy contract / 토큰 컨트랙트 배포
        DonationToken donationToken0 = DonationToken.deploy(web3j, Credentials.create(privateKey0), new DefaultGasProvider(), "MJDonor", "MJD", BigInteger.ZERO).send();
        System.out.println("Contract address: " + donationToken0.getContractAddress());

        // user0 approve for sending 100MJD to user1 / 토큰 전송 허용
        donationToken0.approve(account0, BigInteger.valueOf(1000));
        // user0 sends 100MJD to user1 / 토큰 전송
        TransactionReceipt transferReceipt = donationToken0.transfer(account1, BigInteger.valueOf(100)).send();
        System.out.println("transferReceipt = " + transferReceipt);

        // Deploy donationContract / 기부 컨트랙트 배포
        DonationContract donationContract1 = DonationContract.deploy(web3j,
                Credentials.create(privateKey1),
                new DefaultGasProvider(),
                donationToken0.getContractAddress(),
                BigInteger.valueOf(10_000L),
                BigInteger.valueOf(100L)).send();
        System.out.println("Contract address: " + donationContract1.getContractAddress());

        // load donationToken1
        DonationToken donationToken1 = DonationToken.load(donationToken0.getContractAddress(), web3j, Credentials.create(privateKey1), defaultGasProvider);
        // user1 approve for sending 100MJD to DonationContract / 토큰 전송 허용
        donationToken1.approve(donationContract1.getContractAddress(), BigInteger.valueOf(100L)).send();
        // user1 donate 100MJD to DonationContract / 기부
        donationContract1.donate(BigInteger.valueOf(100L)).send();
        System.out.println("account1's DonatedAmount:" + donationContract1.donatedAmount(account1).send());
    }
}
