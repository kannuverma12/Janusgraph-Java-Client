package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.googlesheet.model.CoachingFacilityForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FACILITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FACILITY_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FACILITY_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FACILITY_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FACILITY_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;

@AllArgsConstructor
@Service
public class ImportFacilitiesService {
    private IngestDataHelper            ingestDataHelper;
    private CoachingInstituteRepository coachingInstituteRepository;
    private FailedDataRepository        failedDataRepository;

    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(FACILITY_SHEET_ID);
        String headerRange = (String) propertyMap.get(FACILITY_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(FACILITY_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(FACILITY_SHEET_RANGE_TEMPLATE);
        List<Object> faciltySheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName());
        List<Long> instituteIds = new ArrayList<>();
        List<CoachingFacilityForm> facilityFormSheetData = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<CoachingFacilityForm> previousFailedFacilityData = getAllFailedData(instituteIds);
        if (Objects.nonNull(faciltySheetData)) {
            facilityFormSheetData = faciltySheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, CoachingFacilityForm.class))
                    .peek(facilityForm -> instituteIds
                            .add(facilityForm.getInstituteId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingInstitute> instituteMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            // Optimization required
            List<CoachingInstitute> existingInstitutes =
                    coachingInstituteRepository.findAllCoachingInstitutes(instituteIds);
            instituteMap = existingInstitutes.stream()
                    .collect(Collectors.toMap(c -> c.getInstituteId(), c -> c));
        }
        if (!previousFailedFacilityData.isEmpty()) {
            buildInstituteFacilityMap(previousFailedFacilityData, instituteMap, failedDataList);
            ingestDataHelper.updateReimportStatus(FACILITY, COACHING);
        }
        if (!facilityFormSheetData.isEmpty()) {
            buildInstituteFacilityMap(facilityFormSheetData, instituteMap,
                    failedDataList);
            ingestDataHelper.saveCoachingInstitutes(instituteMap);
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper.updatePropertyMap(FACILITY_SHEET_START_ROW, faciltySheetData, startRow);
        return true;
    }

    private void buildInstituteFacilityMap(
            List<CoachingFacilityForm> sheetDataList, Map<Long, CoachingInstitute> instituteMap,
            List<Object> failedDataList) {
        for (CoachingFacilityForm sheetData : sheetDataList) {
            Long instituteId = sheetData.getInstituteId();
            CoachingInstitute coachingInstitute = instituteMap.get(instituteId);
            if (Objects.nonNull(instituteId) && Objects.nonNull(coachingInstitute)) {
                Map<String, String> facilities = coachingInstitute.getFacilities();
                if (Objects.isNull(facilities)) {
                    facilities = new HashMap<>();
                }
                facilities.put(sheetData.getFacilityType(), sheetData.getFacilityDescription());
                coachingInstitute.setFacilities(facilities);
            } else {
                // Failure Cases Handling (Invalid InstituteId)
                ingestDataHelper.addToFailedList(sheetData, "InstituteId is empty or invalid",
                        false,
                        failedDataList, COACHING, FACILITY);
            }

        }
    }

    private List<CoachingFacilityForm> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, FACILITY);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedDataList = failedDataRepository.findAll(queryObject);
        List<CoachingFacilityForm> failedFacilityList =
                failedDataList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        CoachingFacilityForm.class)).peek(facility -> instituteIds
                        .add(facility.getInstituteId())).collect(Collectors.toList());
        return failedFacilityList;
    }
}
