package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.entity.StudentSelected;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.googlesheet.model.TopRankedAchievedForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HTTPS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.S3_UPLOAD_FAILED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STUDENT_SELECTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STUDENT_SELECTED_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STUDENT_SELECTED_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STUDENT_SELECTED_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STUDENT_SELECTED_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;

@Service
@AllArgsConstructor
public class ImportStudentSelectedService {
    private IngestDataHelper            ingestDataHelper;
    private CoachingInstituteRepository coachingInstituteRepository;
    private FailedDataRepository        failedDataRepository;
    private UploadUtil                  uploadUtil;

    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(STUDENT_SELECTED_SHEET_ID);
        String headerRange = (String) propertyMap.get(STUDENT_SELECTED_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(STUDENT_SELECTED_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(STUDENT_SELECTED_SHEET_RANGE_TEMPLATE);
        List<Object> studentSelectedSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(),
                GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> instituteIds = new ArrayList<>();
        List<TopRankedAchievedForm> studentSelectedFormSheetData = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<StudentSelected> previousStudentSelectedData = getAllFailedData(instituteIds);
        if (Objects.nonNull(studentSelectedSheetData)) {
            studentSelectedFormSheetData = studentSelectedSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, TopRankedAchievedForm.class))
                    .peek(studentSelectedForm -> instituteIds
                            .add(studentSelectedForm.getInstituteId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingInstitute> instituteMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            List<CoachingInstitute> existingInstitutes =
                    coachingInstituteRepository.findAllCoachingInstitutes(instituteIds);
            instituteMap = existingInstitutes.stream()
                    .collect(Collectors.toMap(c -> c.getInstituteId(), c -> c));
        }
        if (!previousStudentSelectedData.isEmpty()) {
            reimportFailedStudentSelectedData(previousStudentSelectedData, instituteMap,
                    failedDataList);
            ingestDataHelper.updateReimportStatus(STUDENT_SELECTED, COACHING);
        }
        if (!studentSelectedFormSheetData.isEmpty()) {
            buildInstituteStudentSelectedMap(studentSelectedFormSheetData, instituteMap,
                    failedDataList);
        }
        if (!instituteMap.isEmpty()) {
            ingestDataHelper.saveCoachingInstitutes(instituteMap);
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper
                .updatePropertyMap(STUDENT_SELECTED_SHEET_START_ROW, studentSelectedSheetData,
                        startRow);

        return true;
    }

    private void buildInstituteStudentSelectedMap(
            List<TopRankedAchievedForm> sheetDataList, Map<Long, CoachingInstitute> instituteMap,
            List<Object> failedDataList) {
        for (TopRankedAchievedForm sheetData : sheetDataList) {
            StudentSelected studentSelected = new StudentSelected();
            BeanUtils.copyProperties(sheetData, studentSelected);
            if (Objects.nonNull(sheetData.getExamId())) {
                studentSelected.setQualifyingExamId(sheetData.getExamId());
            }
            Long instituteId = studentSelected.getInstituteId();
            if (Objects.nonNull(instituteId) && Objects.nonNull(instituteMap.get(instituteId))) {
                if (!uploadStudentPhoto(studentSelected, instituteId, failedDataList)) {
                    continue;
                }
                List<StudentSelected> studentSelectedList =
                        instituteMap.get(instituteId).getStudentsSelected();
                if (Objects.isNull(studentSelectedList)) {
                    studentSelectedList = new ArrayList<>();
                }
                studentSelectedList.add(studentSelected);
                instituteMap.get(instituteId).setStudentsSelected(studentSelectedList);
            } else {
                // Failure Cases Handling (Invalid InstituteId)
                ingestDataHelper.addToFailedList(studentSelected, "InstituteId is empty or "
                                + "invalid", false,
                        failedDataList, COACHING, STUDENT_SELECTED);
            }
        }
    }

    private List<StudentSelected> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, STUDENT_SELECTED);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedDataList = failedDataRepository.findAll(queryObject);
        List<StudentSelected> studentSelectedList =
                failedDataList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        StudentSelected.class)).peek(studentSelected -> instituteIds
                        .add(studentSelected.getInstituteId())).collect(Collectors.toList());
        return studentSelectedList;
    }

    private boolean reimportFailedStudentSelectedData(List<StudentSelected> studentSelectedList,
            Map<Long, CoachingInstitute> instituteMap, List<Object> failedDataList) {
        for (StudentSelected studentSelected : studentSelectedList) {
            Long instituteId = studentSelected.getInstituteId();
            if (!uploadStudentPhoto(studentSelected, instituteId, failedDataList)) {
                continue;
            }
            List<StudentSelected> existingStudentSelectedList =
                    instituteMap.get(instituteId).getStudentsSelected();
            if (Objects.isNull(existingStudentSelectedList)) {
                existingStudentSelectedList = new ArrayList<>();
            }
            existingStudentSelectedList.add(studentSelected);
            instituteMap.get(instituteId).setStudentsSelected(existingStudentSelectedList);
        }
        return true;
    }

    private boolean uploadStudentPhoto(StudentSelected studentSelected, Long instituteId,
            List<Object> failedDataList) {
        String studentPhoto = studentSelected.getStudentPhoto();
        if (StringUtils.isNotBlank(studentPhoto) && studentPhoto.startsWith(HTTPS)) {
            String relativeUrl = uploadUtil.uploadFile(studentPhoto, null,
                    instituteId, CoachingConstants.S3RelativePath.STUDENT_SELECTED,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                studentSelected.setStudentPhoto(relativeUrl);
            } else {
                ingestDataHelper.addToFailedList(studentSelected, S3_UPLOAD_FAILED, false,
                        failedDataList, COACHING, STUDENT_SELECTED);
                return false;
            }
        }
        return true;
    }
}
