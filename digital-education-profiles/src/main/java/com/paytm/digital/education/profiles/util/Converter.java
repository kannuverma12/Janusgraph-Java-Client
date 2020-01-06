package com.paytm.digital.education.profiles.util;

import com.paytm.digital.education.profiles.db.model.ProfileDataEntity;
import com.paytm.digital.education.profiles.db.model.ProfileIdentifierEntity;
import com.paytm.digital.education.profiles.response.ProfileDataResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Converter {


    public static String getBase64EncodedString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static ProfileDataResponse getProfileDataResponse(
            List<ProfileDataEntity> profileDatumEntities, List<ProfileIdentifierEntity> profileIdentifierEntities) {
        ProfileDataResponse profileDataResponse = new ProfileDataResponse();

        Map<Long, List<ProfileDataEntity>> profileGroupById =
                profileDatumEntities.stream()
                        .collect(Collectors.groupingBy(pd -> pd.getProfileId()));

        Map<Long, ProfileIdentifierEntity> profileIdentifierGroupById =
                profileIdentifierEntities.stream()
                        .collect(Collectors.toMap(ProfileIdentifierEntity::getProfileId, Function.identity()));

        for (Long profileId : profileIdentifierGroupById.keySet()) {
            ProfileDataResponse.Profile profile = new ProfileDataResponse.Profile();
            profile.setProfileId(profileIdentifierGroupById.get(profileId).getProfileId());
            profile.setCustomerId(profileIdentifierGroupById.get(profileId).getCustomerId());
            profile.setName(profileIdentifierGroupById.get(profileId).getName());
            profile.setDateOfBirth(profileIdentifierGroupById.get(profileId).getDateOfBirth());
            profile.setIsEnabled(profileIdentifierGroupById.get(profileId).getIsEnabled());
            profileGroupById.getOrDefault(profileId, Arrays.asList()).forEach(data -> {
                profile.getData().put(data.getKey(), data.getValue());
            });
            profileDataResponse.getProfileData().add(profile);
        }

        return profileDataResponse;
    }
}
