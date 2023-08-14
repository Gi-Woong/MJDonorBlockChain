package org.web3j;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.generated.contracts.DonationContract;
import org.web3j.generated.contracts.DonationToken;
import org.web3j.generated.contracts.HelloWorld;
import org.web3j.protocol.Web3j;
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
        String privateKey = "0xd13bce0eff25e852c0239d83e12ad25bd9a2b30c92e113d555633e0fa5506f69"; //ganache private key
        Credentials credentials = Credentials.create(privateKey);
        Web3j web3j = Web3j.build(new HttpService(nodeUrl));
//        System.out.println("Deploying HelloWorld contract ...");
        DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
//       StaticGasProvider staticGasProvider = new StaticGasProvider(BigInteger.valueOf(4_100_000_000_000_000L), BigInteger.valueOf(100_000_000_000_000_000L));
        System.out.println("defaultGasProvider.getGasLimit() = " + defaultGasProvider.getGasLimit());
        HelloWorld helloWorld = HelloWorld.deploy(web3j, credentials, defaultGasProvider, "Hello Blockchain World!").send();
        System.out.println("Contract address: " + helloWorld.getContractAddress());
//       System.out.println("Greeting method result: " + helloWorld.greeting().send());


        //토큰 컨트랙트 배포
        DonationToken donationToken = DonationToken.deploy(web3j, credentials, new DefaultGasProvider(), "MJDonor", "MJD", BigInteger.ZERO).send();
        System.out.println("Contract address: " + donationToken.getContractAddress());

        // 기부하기
        donationToken.approve("", BigInteger.valueOf(1000));
        // 기부 컨트랙트 배포
        DonationContract donationContract = DonationContract.deploy(web3j, credentials, new DefaultGasProvider(),donationToken.getContractAddress(), BigInteger.valueOf(10_000L), BigInteger.valueOf(100L)).send();
        System.out.println("Contract address: " + donationContract.getContractAddress());
    }
}

