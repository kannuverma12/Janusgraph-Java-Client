package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateDetails {

    private String username;

    @NotEmpty
    private String email;

    @NotEmpty
    private String mobileNumber;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dob;

    private String password;

    private String salutation;

    private String fullName;

    private String firstName;

    private String middleName;

    private String lastName;

    private Integer age;

    private String gender;

    private String motherName;

    private String fatherName;

    private String landLineNumber;

    private String nationality;

    private String stateDomicile;

    private String motherTongue;

    private String marathiFluency;          // Can you read/write/speak Marathi – Check box (Read/Write/Speak)

    private String englishFluency;          // Can you read/write/speak English– Check box (Read/Write/Speak)

    private String employmentExchangeStatus;   // Whether registered in Employment exchange (YES/NO)

    private String employmentExchangeName;

    private String employmentExchangeNumber;

    private String disability;              // Person with Disability (YES/NO)

    private String disabilityType;

    private String scribeRequired;

    private String scribeArrangedBySelf;

    private String religion;

    private String category;

    private String subCaste;

    private String nonCreamyLayer;          // Do you belong to Non-Creamy layer (YES/NO)

    private String casteValidity;           // Check if caste benefits can be applied for the post

    private List<CandidateAddress> address;

    private Boolean samePermanentAddress;   // IS PERMANENT ADDRESS SAME AS CORRESPONDENCE ADDRESS.

    private String maritalStatus;

    private Float amount;

    private CandidateDocument photo;

    private CandidateDocument signature;

    private List<CandidateQualification> qualification;

    private String nameAfterMarriage;

    private String exServiceManStatus;

    private String exServiceManYear;

    private String exServiceManMonth;

    private String exServiceManDays;

    private String isStateGovtEmployee;

    private String isBMCEmployee;

    private String isMeritSportsman;

    private String isFreedomFighterNominatedChild;

    private String isCensusEmployeeOf1991;

    private String isElectionEmployeeAfter1994;

    private String isProjectAffected;

    private String isEarthquakeAffected;

    private String isPartTimeEmployee;

    private String isHomeGaurd;

    private String isZilaParishadEmployee;

    private String isQualifiedForStateCivilServices;

    private String isDependentOfExServiceMan;

    private String isFromNaxaliteArea;

    private String isChildOfAffectedPolice;

    private String extraActivity;

    private String hobbies;

    private String height;

    private String chestDeflated;

    private String chestInflated;

    private String pendingProsecution;

    private String initiatedActionByCouncil;

    private String initiatedActionByGovtOrg;

    private String courtCase;

    private String debarred;

    private String interestedInLowerQualificationPosts;

    private List<CandidateEmployment> employment;
}
