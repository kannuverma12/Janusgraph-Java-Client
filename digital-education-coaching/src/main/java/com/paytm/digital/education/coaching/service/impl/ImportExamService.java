package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.database.entity.CoachingExam;
import com.paytm.digital.education.coaching.database.repository.CoachingExamRepository;
import com.paytm.digital.education.enums.ExamType;
import com.paytm.digital.education.coaching.googlesheet.model.CoachingExamForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ACTIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DB_DATE_FORMAT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.XCEL_EXAM_DATE_FORMAT;

@AllArgsConstructor
@Service
public class ImportExamService {
    private IngestDataHelper       ingestDataHelper;
    private CoachingExamRepository coachingExamRepository;
    private FailedDataRepository   failedDataRepository;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData()
            throws IOException, GeneralSecurityException, ParseException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(EXAM_SHEET_ID);
        String headerRange = (String) propertyMap.get(EXAM_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(EXAM_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(EXAM_SHEET_RANGE_TEMPLATE);
        List<Object> examSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(),
                GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> examIds = new ArrayList<>();
        List<CoachingExamForm> coachingExamFormSheetData = new ArrayList<>();
        Map<Long, List<FailedData>> failedDataMap = new HashMap<>();
        List<CoachingExam> previousFailedExamList = getAllFailedData(examIds);
        if (Objects.nonNull(examSheetData)) {
            coachingExamFormSheetData = examSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, CoachingExamForm.class))
                    .peek(coachingExamForm -> examIds
                            .add(coachingExamForm.getExamId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingExam> examMap = new HashMap<>();
        if (!examIds.isEmpty()) {
            List<CoachingExam> existingInstitutes =
                    coachingExamRepository.findAllCoachingExam(examIds);
            examMap = existingInstitutes.stream()
                    .collect(Collectors.toMap(c -> c.getExamId(), c -> c));
        }
        Set<Long> failedExamIds = new HashSet<>();
        if (!previousFailedExamList.isEmpty()) {
            reimportFailedExamData(previousFailedExamList, examMap, failedDataMap,
                    failedExamIds);
            ingestDataHelper.updateReimportStatus(EXAM, COACHING);
        }
        if (!coachingExamFormSheetData.isEmpty()) {
            addCoachingExamData(coachingExamFormSheetData, examMap, failedDataMap,
                    failedExamIds);
        }
        if (!failedDataMap.isEmpty()) {
            List<Object> failedDataList = failedDataMap.values().stream()
                    .collect(ArrayList::new, List::addAll, List::addAll);
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper.updatePropertyMap(EXAM_SHEET_START_ROW, examSheetData, startRow);
        return true;
    }

    private List<CoachingExam> getAllFailedData(List<Long> examIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, EXAM);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedExamList = failedDataRepository.findAll(queryObject);
        List<CoachingExam> coachingExamList =
                failedExamList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        CoachingExam.class)).peek(coachingExam -> examIds
                        .add(coachingExam.getExamId())).collect(Collectors.toList());
        return coachingExamList;
    }

    private boolean reimportFailedExamData(List<CoachingExam> coachingExamList,
            Map<Long, CoachingExam> examMap, Map<Long, List<FailedData>> failedDataMap,
            Set<Long> failedExamIds) {
        for (CoachingExam coachingExam : coachingExamList) {
            coachingExamRepository.upsertCoachingExam(coachingExam);
            if (Objects.nonNull(coachingExam.getExamId())) {
                examMap.put(coachingExam.getExamId(), coachingExam);
                failedDataMap.remove(coachingExam.getExamId());
            }
        }
        return true;
    }

    private void addCoachingExamData(
            List<CoachingExamForm> examSheetDataList,
            Map<Long, CoachingExam> examMap,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedExamIds) throws
            ParseException {
        boolean isFailed;
        boolean isImportable;
        String message = null;
        for (CoachingExamForm sheetData : examSheetDataList) {
            CoachingExam coachingExam = new CoachingExam();
            Long examId = sheetData.getExamId();
            isFailed = false;
            isImportable = true;
            if (Objects.nonNull(examId)) {
                CoachingExam existingDetails = examMap.get(examId);
                if (Objects.nonNull(existingDetails) && !failedExamIds.contains(examId)) {
                    BeanUtils.copyProperties(existingDetails, coachingExam);
                } else {
                    coachingExam.setExamId(examId);
                    List<FailedData> previousFailedData = failedDataMap.get(examId);
                    if (Objects.nonNull(previousFailedData)) {
                        FailedData failedData = previousFailedData.get(0);
                        isFailed = false;
                        message = failedData.getMessage();
                        isImportable = failedData.getIsImportable();
                        coachingExam =
                                JsonUtils.convertValue(failedData.getData(),
                                        CoachingExam.class);
                    } else {
                        isFailed = true;
                        message = "Invalid Exam Id";
                        isImportable = false;
                    }
                }
            }
            String examName = sheetData.getExamName();
            if (StringUtils.isNotBlank(examName)) {
                coachingExam.setExamName(examName);
            }
            String description = sheetData.getExamDescription();
            if (StringUtils.isNotBlank(description)) {
                coachingExam.setExamDescription(description);
            }
            Long instituteId = sheetData.getInstituteId();
            if (Objects.nonNull(instituteId)) {
                coachingExam.setInstituteId(instituteId);
            }
            Long courseId = sheetData.getCourseId();
            if (Objects.nonNull(courseId)) {
                coachingExam.setCourseId(courseId);
            }
            String status = sheetData.getStatus();
            if (StringUtils.isNotBlank(status)) {
                status = status.toLowerCase();
                if (status.equals(ACTIVE)) {
                    coachingExam.setActive(true);
                } else {
                    coachingExam.setActive(false);
                }
            }
            String examTypeValue = sheetData.getExamType();
            if (StringUtils.isNotBlank(examTypeValue)) {
                ExamType examType = ExamType.fromString(examTypeValue);
                if (Objects.nonNull(examType)) {
                    coachingExam.setExamType(examType);
                }
            }
            Integer examDuration = sheetData.getExamDuration();
            if (Objects.nonNull(examDuration)) {
                coachingExam.setExamDuration(examDuration);
            }
            Double marks = sheetData.getMarks();
            if (Objects.nonNull(marks)) {
                coachingExam.setMarks(marks);
            }
            String examDateString = sheetData.getExamDate();
            if (StringUtils.isNotBlank(examDateString)) {
                List<String> examDateStringList = Arrays.asList(examDateString.split(", "));
                List<Date> examDateList = new ArrayList<>();
                for (String dateString : examDateStringList) {
                    Date date = DateUtil.convertDateFormat(XCEL_EXAM_DATE_FORMAT, DB_DATE_FORMAT,
                            dateString);
                    examDateList.add(date);
                }
                coachingExam.setExamDates(examDateList);
            }
            if (!isFailed && isImportable) {
                coachingExamRepository.upsertCoachingExam(coachingExam);
                if (Objects.nonNull(examId)) {
                    examMap.put(examId, coachingExam);
                    failedDataMap.remove(examId);
                }
            } else {
                addToFailedList(coachingExam, message, failedDataMap, failedExamIds,
                        isImportable);
            }
        }
    }

    private void addToFailedList(CoachingExam coachingExam, String message,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedExamIds,
            boolean isImportable) {
        FailedData failedData = new FailedData();
        failedData.setComponent(COACHING);
        failedData.setHasImported(false);
        failedData.setType(EXAM);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(coachingExam);
        Long examId = coachingExam.getExamId();
        if (Objects.nonNull(examId)) {
            failedExamIds.add(examId);
            failedDataMap.put(examId, Arrays.asList(failedData));
        } else {
            List<FailedData> failedNewData = failedDataMap.get((long) 0);
            if (Objects.isNull(failedNewData)) {
                failedNewData = new ArrayList<>();
            }
            failedNewData.add(failedData);
            failedDataMap.put((long) 0, failedNewData);
        }
    }
}
