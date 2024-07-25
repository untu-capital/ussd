package com.example.ussd1.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String idNumber;
    private String branchName;
    private String maritalStatus;
    private String gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String placeOfBusiness;
    private String industryCode;
    private String loanAmount;
    private String streetNo;
    private String businessName;
    private String businessStartDate;
    private String streetName;
    private String suburb;
    private String city;
    private String tenure;
    private String userId;
    private String username;
    private String loanStatus;
    private String loanStatusAssigner;
    private Integer fcbScore;
    private String fcbStatus;
    private String comment;
    private String assignTo;
    private String assignedBy;
    private String additionalRemarks;
    private String loanFileId;
    private String processLoanStatus;
    private String processedBy;
    private String meetingLoanAmount;
    private String meetingTenure;
    private String meetingInterestRate;
    private String meetingOnWhichBasis;
    private String meetingCashHandlingFee;
    private String meetingRepaymentAmount;
    private String meetingProduct;
    private String meetingRN;
    private String meetingUpfrontFee;
    private String meetingFinalizedBy;
    private String bocoSignature;
    private String bocoSignatureImage;
    private String bocoName;
    private String bmSignature;
    private String bmName;
    private String caSignature;
    private String caName;
    private String cmSignature;
    private String cmName;
    private String finSignature;
    private String finName;
    private String boardSignature;
    private String boardName;
    private String assignedStatus;
    private String lessFees;
    private String applicationFee;
    private String bocoDate;
    private String bmDateAssignLo;
    private String loDate;
    private String bmDateMeeting;
    private String ccDate;
    private String predisDate;
    private String pipelineStatus;
    private String bmSetMeeting;
    private String creditCommit;
    private String completelyDone;
    private String nextOfKinName;
    private String nextOfKinPhone;
    private String nextOfKinRelationship;
    private String nextOfKinAddress;
    private String nextOfKinName2;
    private String nextOfKinPhone2;
    private String nextOfKinRelationship2;
    private String nextOfKinAddress2;
    private String clientLoanId;
    private String loanCount;
    private String platformUsed;
}
