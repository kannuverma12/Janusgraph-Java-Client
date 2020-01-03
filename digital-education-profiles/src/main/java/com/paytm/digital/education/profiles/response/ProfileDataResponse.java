package com.paytm.digital.education.profiles.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProfileDataResponse {

    private List<Profile> profileData = new ArrayList<>();


    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Profile {

        private Long customerId;

        private Long profileId;

        private String name;

        private LocalDate dateOfBirth;

        private Boolean isEnabled;

        private Map<String, Object> data = new HashMap();
    }
}
