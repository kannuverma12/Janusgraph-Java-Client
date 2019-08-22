package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.database.entity.CoachingCenter;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.repository.CoachingCenterRespository;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.coaching.googlesheet.model.CoachingCentreForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTRE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTRE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTRE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTRE_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER;
import static com.paytm.digital.education.utility.DateUtil.getCurrentDate;

@Service
@AllArgsConstructor
public class ImportCoachingCenterService {
    private IngestDataHelper            ingestDataHelper;
    private CoachingInstituteRepository coachingInstituteRepository;
    private FailedDataRepository        failedDataRepository;
    private CoachingCenterRespository   coachingCenterRespository;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(CENTRE_SHEET_ID);
        String headerRange = (String) propertyMap.get(CENTRE_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(CENTRE_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(CENTRE_SHEET_RANGE_TEMPLATE);
        List<Object> coachingCentreSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(), GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> instituteIds = new ArrayList<>();
        List<Long> centerIds = new ArrayList<>();
        List<CoachingCentreForm> coachingCentreFormSheetData = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        if (Objects.nonNull(coachingCentreSheetData)) {
            coachingCentreFormSheetData = coachingCentreSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, CoachingCentreForm.class))
                    .peek(coachingCentreForm -> instituteIds
                            .add(coachingCentreForm.getInstituteId()))
                    .peek(coachingCentreForm -> centerIds
                            .add(coachingCentreForm.getCenterId()))
                    .collect(Collectors.toList());
        }
        List<Long> existingInstituteList = new ArrayList<>();
        if (!instituteIds.isEmpty()) {
            // Need to optimize
            List<CoachingInstitute> existingInstitutes =
                    coachingInstituteRepository.findAllCoachingInstitutes(instituteIds);
            existingInstituteList = existingInstitutes.stream().map(c -> c.getInstituteId())
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingCenter> coachingCenterMap = new HashMap<>();
        if (!centerIds.isEmpty()) {
            List<CoachingCenter> existingCoachingCenters =
                    coachingInstituteRepository.findAllCoachingCenter(centerIds);
            coachingCenterMap = existingCoachingCenters.stream()
                    .collect(Collectors.toMap(c -> c.getCenterId(), c -> c));
        }
        if (!coachingCentreFormSheetData.isEmpty()) {
            buildInstituteCoachingCentreMap(coachingCentreFormSheetData, existingInstituteList,
                    coachingCenterMap, failedDataList);
            saveCoachingCenters(coachingCenterMap);
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper
                .updatePropertyMap(CENTRE_SHEET_START_ROW, coachingCentreSheetData, startRow);

        return true;
    }

    private void buildInstituteCoachingCentreMap(
            List<CoachingCentreForm> sheetDataList,
            List<Long> existingInstituteList,
            Map<Long, CoachingCenter> coachingCenterMap, List<Object> failedDataList) {
        for (CoachingCentreForm sheetData : sheetDataList) {
            CoachingCenter coachingCenter = new CoachingCenter();
            BeanUtils.copyProperties(sheetData, coachingCenter);
            if (StringUtils.isNotBlank(sheetData.getCoursesAvailable())) {
                List<String> courseAvailableList =
                        Arrays.asList(sheetData.getCoursesAvailable().split(", "));
                List<CourseType> courseTypeList = new ArrayList<>();
                for (String course : courseAvailableList) {
                    CourseType courseType = CourseType.fromString(course);
                    if (Objects.nonNull(courseType)) {
                        courseTypeList.add(courseType);
                    }
                }
                coachingCenter.setCourseTypeAvailable(courseTypeList);
            }
            if (StringUtils.isNotBlank(sheetData.getStatus())) {
                if (sheetData.getStatus().toLowerCase().equals(ACTIVE)) {
                    coachingCenter.setActive(true);
                } else {
                    coachingCenter.setActive(false);
                }
            }
            Long instituteId = coachingCenter.getInstituteId();
            if (Objects.nonNull(instituteId) && existingInstituteList.contains(instituteId)) {
                Long centerId = coachingCenter.getCenterId();
                if (Objects.nonNull(centerId)) {
                    if (Objects.nonNull(coachingCenterMap.get(centerId))) {
                        CoachingCenter updateDatedCoachingCenter =
                                updateCoachingCenter(coachingCenter,
                                        coachingCenterMap.get(centerId));
                        if (StringUtils.isNotBlank(sheetData.getStatus())) {
                            if (sheetData.getStatus().equals(ACTIVE)) {
                                updateDatedCoachingCenter.setActive(true);
                            } else {
                                updateDatedCoachingCenter.setActive(false);
                            }
                        }
                        updateDatedCoachingCenter.setUpdatedAt(getCurrentDate());
                        coachingCenterMap.put(centerId, updateDatedCoachingCenter);
                    } else {
                        // Failure cases (Invalid center Id)
                        ingestDataHelper.addToFailedList(coachingCenter, "center id is invalid.",
                                false, failedDataList, COACHING, COACHING_CENTER);
                    }
                } else {
                    // Need to optimized
                    centerId = coachingCenterRespository.getNextSequenceId(COACHING_CENTER);
                    coachingCenter.setCenterId(centerId);
                    coachingCenter.setCreatedAt(getCurrentDate());
                    coachingCenter.setUpdatedAt(coachingCenter.getCreatedAt());
                    coachingCenterMap.put(centerId, coachingCenter);
                }
            } else {
                // Failure Cases Handling (Invalid InstituteId)
                ingestDataHelper.addToFailedList(coachingCenter, "InstituteId is empty or "
                        + "invalid", false, failedDataList, COACHING, COACHING_CENTER);
            }
        }
    }

    private CoachingCenter updateCoachingCenter(CoachingCenter newData, CoachingCenter oldData) {
        String officialName = newData.getOfficialName();
        if (StringUtils.isNotBlank(officialName)) {
            oldData.setOfficialName(officialName);
        }

        String streetAddress1 = newData.getStreetAddress1();
        if (StringUtils.isNotBlank(streetAddress1)) {
            oldData.setStreetAddress1(officialName);
        }

        String streetAddress2 = newData.getStreetAddress2();
        if (StringUtils.isNotBlank(streetAddress2)) {
            oldData.setStreetAddress2(streetAddress2);
        }

        String streetAddress3 = newData.getStreetAddress3();
        if (StringUtils.isNotBlank(streetAddress3)) {
            oldData.setStreetAddress3(streetAddress3);
        }

        String city = newData.getCity();
        if (StringUtils.isNotBlank(city)) {
            oldData.setCity(city);
        }

        String state = newData.getState();
        if (StringUtils.isNotBlank(state)) {
            oldData.setState(state);
        }

        Integer pincode = newData.getPincode();
        if (Objects.nonNull(pincode)) {
            oldData.setPincode(pincode);
        }

        String latitude = newData.getLatitude();
        if (StringUtils.isNotBlank(latitude)) {
            oldData.setLatitude(latitude);
        }

        String longitude = newData.getLongitude();
        if (StringUtils.isNotBlank(longitude)) {
            oldData.setLongitude(longitude);
        }

        if (Objects.nonNull(newData.getCourseTypeAvailable())) {
            oldData.setCourseTypeAvailable(newData.getCourseTypeAvailable());
        }
        return oldData;
    }

    private void saveCoachingCenters(Map<Long, CoachingCenter> coachingCenterMap) {
        for (Map.Entry<Long, CoachingCenter> entry : coachingCenterMap.entrySet()) {
            coachingCenterRespository.upsertCoachingCenter(entry.getValue());
        }
    }

}
