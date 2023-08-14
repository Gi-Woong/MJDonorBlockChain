// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./DonationToken.sol";

/**
 * @title DonationContract
 * @dev A contract to receive donations in the form of ERC-20 tokens
 * and send the donated tokens to a specified address when the donation period ends.
 */
contract DonationContract {
    address public owner;
    DonationToken public tokenContract;
    uint256 public targetPoint;
    uint256 public donationEndTime;
    // uint256 public totalDonation;
    bool public donationClosed;

    mapping(address => uint256) public donatedAmount;

    /**
     * @dev Modifier to check if the caller is the contract owner
     */
    modifier onlyOwner() {
        require(msg.sender == owner, "Only the contract owner can call this function");
        _;
    }

    /**
     * @dev Modifier to check if the donation period has not ended and donation is not closed
     */
    modifier beforeDonationEnd() {
        require(!donationClosed, "Donation has been closed");
        require(block.timestamp < donationEndTime, "Donation period has ended");
        _;
    }

    /**
     * @dev Constructor function to initialize the DonationContract
     * @param _targetPoint Target donation amount in points
     * @param _donationPeriodInDays Duration of the donation period in days
     */
    constructor(
        address _tokenContract,
        uint256 _targetPoint,
        uint256 _donationPeriodInDays
    ) {
        owner = msg.sender;
        targetPoint = _targetPoint;
        donationEndTime = block.timestamp + (_donationPeriodInDays * 1 days);
        tokenContract = DonationToken(_tokenContract);
    }

    /**
     * @dev Donate ERC-20 tokens to the contract
     * @dev donate 하기 전, DonationToken.sol의 approve 함수를 실행해야 함.
     * @param amount The amount of ERC-20 tokens to be donated
     */
    function donate(uint256 amount) public beforeDonationEnd {
        require(amount > 0, "Donation amount should be greater than 0");
        // require(tokenContract.approve(address(this), amount), "Failed to approve tokens to contract");
        require(tokenContract.transferFrom(msg.sender, address(this), amount), "Failed to transfer tokens to contract");
        // totalDonation += amount;
        donatedAmount[msg.sender] += amount;
    }

    /**
     * @dev Close the donation period and send donated tokens to the specified address if targetPoint is reached,
     * otherwise refund tokens to donors.
     * This function can only be called by the contract owner.
     */
    function closeDonation() public onlyOwner {
        require(!donationClosed, "Donation has already been closed");
        uint256 totalDonation = tokenContract.balanceOf(address(this));
        if (totalDonation >= targetPoint) {
            // Send donated tokens to the specified address
            require(tokenContract.transfer(address(tokenContract), totalDonation), "Failed to send donated tokens");
        }
        donationClosed = true;
    }

    /**
    * @dev Refund function to refund donated tokens to the donor after the donation is closed.
    * @notice This function can be called by any donor to request a refund of their donated tokens.
    * @notice The donation must be closed for refunds to be possible.
    * @notice The donated tokens will be transferred from the contract's address to the donor's address.
    * @notice The amount of tokens refunded will be the same as the amount donated by the caller.
    * @notice If the refund fails (e.g., due to insufficient balance), an error message will be reverted.
    */
    function refund() public {
        require(donationClosed, "Donation is not closed"); // 기부가 종료되지 않았을 경우 에러 발생
        uint256 refundAmount = donatedAmount[msg.sender];
        donatedAmount[msg.sender] = 0;
        require(tokenContract.transferFrom(address(this), msg.sender, refundAmount), "Failed to transfer tokens to donor"); // 기부자에게 기부한 토큰을 환불
    }

    receive() external payable {
        // Fallback function to accept direct Ether donations without calling donate function
        revert("This contract only accepts ERC-20 token donations.");
    }
}
