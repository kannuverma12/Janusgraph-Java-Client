package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.entity.CoachingCourse;
import com.paytm.digital.education.coaching.database.repository.CoachingCourseRepository;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.coaching.googlesheet.model.CoachingCourseForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.DateUtil;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HTTPS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.S3_UPLOAD_FAILED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;

@Service
@AllArgsConstructor
public class ImportCoachingCourseService {
    private IngestDataHelper         ingestDataHelper;
    private CoachingCourseRepository coachingCourseRepository;
    private FailedDataRepository     failedDataRepository;
    private UploadUtil               uploadUtil;

    /*
     ** Import the new data from spreadsheet
     */
    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(COURSE_SHEET_ID);
        String headerRange = (String) propertyMap.get(COURSE_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(COURSE_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(COURSE_SHEET_RANGE_TEMPLATE);
        List<Object> courseSheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(),
                GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> courseIds = new ArrayList<>();
        List<CoachingCourseForm> coachingCourseFormSheetData = new ArrayList<>();
        Map<Long, List<FailedData>> failedDataMap = new HashMap<>();
        List<CoachingCourse> previousFailedCourseList = getAllFailedData(courseIds);
        if (Objects.nonNull(courseSheetData)) {
            coachingCourseFormSheetData = courseSheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, CoachingCourseForm.class))
                    .peek(coachingCourseForm -> courseIds
                            .add(coachingCourseForm.getCourseId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingCourse> courseMap = new HashMap<>();
        if (!courseIds.isEmpty()) {
            List<CoachingCourse> existingCourses =
                    coachingCourseRepository.findAllCoachingCourses(courseIds);
            courseMap = existingCourses.stream()
                    .collect(Collectors.toMap(c -> c.getCourseId(), c -> c));
        }
        Set<Long> failedCourseIds = new HashSet<>();
        if (!previousFailedCourseList.isEmpty()) {
            reimportFailedCourseData(previousFailedCourseList, courseMap, failedDataMap,
                    failedCourseIds);
            ingestDataHelper.updateReimportStatus(COURSE, COACHING);
        }
        if (!coachingCourseFormSheetData.isEmpty()) {
            addCoachingCourseData(coachingCourseFormSheetData, courseMap, failedDataMap,
                    failedCourseIds);
        }
        if (!failedDataMap.isEmpty()) {
            List<Object> failedDataList = failedDataMap.values().stream()
                    .collect(ArrayList::new, List::addAll, List::addAll);
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper.updatePropertyMap(COURSE_SHEET_START_ROW, courseSheetData, startRow);

        return true;
    }

    private List<CoachingCourse> getAllFailedData(List<Long> courseIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, COURSE);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedCoursesList = failedDataRepository.findAll(queryObject);
        List<CoachingCourse> coachingCoursesList =
                failedCoursesList.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        CoachingCourse.class)).peek(coachingCourse -> courseIds
                        .add(coachingCourse.getCourseId())).collect(Collectors.toList());
        return coachingCoursesList;
    }

    private void addCoachingCourseData(
            List<CoachingCourseForm> courseSheetDataList,
            Map<Long, CoachingCourse> courseMap,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedCourseIds) {
        boolean isFailed;
        boolean isImportable;
        String message = null;
        for (CoachingCourseForm sheetData : courseSheetDataList) {
            CoachingCourse coachingCourse = new CoachingCourse();
            Long courseId = sheetData.getCourseId();
            isFailed = false;
            isImportable = true;
            if (Objects.nonNull(courseId)) {
                CoachingCourse existingDetails = courseMap.get(courseId);
                if (Objects.nonNull(existingDetails) && !failedCourseIds.contains(courseId)) {
                    BeanUtils.copyProperties(existingDetails, coachingCourse);
                } else {
                    coachingCourse.setCourseId(courseId);
                    List<FailedData> previousFailedData = failedDataMap.get(courseId);
                    if (Objects.nonNull(previousFailedData)) {
                        FailedData failedData = previousFailedData.get(0);
                        isFailed = false;
                        message = failedData.getMessage();
                        isImportable = failedData.getIsImportable();
                        coachingCourse =
                                JsonUtils.convertValue(failedData.getData(),
                                        CoachingCourse.class);
                    } else {
                        isFailed = true;
                        message = "Invalid course Id";
                        isImportable = false;
                    }
                }
            }
            String courseName = sheetData.getCourseName();
            if (StringUtils.isNotBlank(courseName)) {
                coachingCourse.setCourseName(courseName);
            }
            Long instituteId = sheetData.getInstituteId();
            if (Objects.nonNull(instituteId)) {
                coachingCourse.setInstituteId(instituteId);
            }
            String courseCategory = sheetData.getCourseCategory();
            if (StringUtils.isNotBlank(courseCategory)) {
                coachingCourse.setCourseType(CourseType.fromString(courseCategory));
            }
            String examPreparedIds = sheetData.getExamsPreparedFor();
            if (StringUtils.isNotBlank(examPreparedIds)) {
                List<String> examIdsInString = Arrays.asList(examPreparedIds.split(", "));
                List<Long> examIds = examIdsInString.stream().map(e2 -> Long.parseLong(e2)).collect(
                        Collectors.toList());
                coachingCourse.setExams(examIds);
            }
            String streamsPrepared = sheetData.getStreamPreparedFor();
            if (StringUtils.isNotBlank(streamsPrepared)) {
                List<String> streams = Arrays.asList(streamsPrepared.split(", "));
                coachingCourse.setStreamPreparedFor(streams);
            }
            Integer duration = sheetData.getCourseDurationInMonths();
            if (Objects.nonNull(duration)) {
                coachingCourse.setCourseDurationInMonths(duration);
            }
            String eligibilityCriteria = sheetData.getEligibilityCriteria();
            if (StringUtils.isNotBlank(eligibilityCriteria)) {
                coachingCourse.setEligibilityCriteria(eligibilityCriteria);
            }
            String courseDetails = sheetData.getCourseDetails();
            if (StringUtils.isNotBlank(courseDetails)) {
                coachingCourse.setCourseDetails(courseDetails);
            }
            String courseDesc = sheetData.getCourseDescription();
            if (StringUtils.isNotBlank(courseDesc)) {
                coachingCourse.setCourseDescription(courseDesc);
            }
            String teachingMethod = sheetData.getTeachingMethodology();
            if (StringUtils.isNotBlank(teachingMethod)) {
                coachingCourse.setTeachingMethodology(teachingMethod);
            }
            String classSchedule = sheetData.getClassSchedule();
            if (StringUtils.isNotBlank(classSchedule)) {
                coachingCourse.setClassSchedule(classSchedule);
            }
            String studyMaterialDescription = sheetData.getStudyMaterialDescription();
            if (StringUtils.isNotBlank(studyMaterialDescription)) {
                coachingCourse.setStudyMaterialDescription(studyMaterialDescription);
            }
            String facilities = sheetData.getFacilities();
            if (StringUtils.isNotBlank(facilities)) {
                List<String> facilitiesList = Arrays.asList(facilities.split(", "));
                coachingCourse.setFacilities(facilitiesList);
            }
            /*String status = sheetData.getStatus().toLowerCase();
            if (StringUtils.isNotBlank(status)) {
                if (status.equals(ACTIVE)) {
                    coachingCourse.setActive(true);
                } else {
                    coachingCourse.setActive(false);
                }
            }*/
            if (Objects.nonNull(courseId) && !isFailed) {
                isFailed = uploadFiles(coachingCourse);
                if (isFailed) {
                    message = S3_UPLOAD_FAILED;
                }
            }
            if (!isFailed && isImportable) {
                coachingCourseRepository.upsertCoaching(coachingCourse);
                if (Objects.nonNull(courseId)) {
                    courseMap.put(courseId, coachingCourse);
                    failedDataMap.remove(courseId);
                }
            } else {
                addToFailedList(coachingCourse, message, failedDataMap, failedCourseIds,
                        isImportable);
            }
        }
    }

    private void addToFailedList(CoachingCourse coachingCourse, String message,
            Map<Long, List<FailedData>> failedDataMap, Set<Long> failedCourseIds,
            boolean isImportable) {
        FailedData failedData = new FailedData();
        failedData.setComponent(COACHING);
        failedData.setHasImported(false);
        failedData.setType(COURSE);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(coachingCourse);
        Long courseId = coachingCourse.getCourseId();
        if (Objects.nonNull(courseId)) {
            failedCourseIds.add(courseId);
            failedDataMap.put(courseId, Arrays.asList(failedData));
        } else {
            List<FailedData> failedNewData = failedDataMap.get((long) 0);
            if (Objects.isNull(failedNewData)) {
                failedNewData = new ArrayList<>();
            }
            failedNewData.add(failedData);
            failedDataMap.put((long) 0, failedNewData);
        }
    }

    private boolean reimportFailedCourseData(List<CoachingCourse> coachingCourseList,
            Map<Long, CoachingCourse> courseMap, Map<Long, List<FailedData>> failedDataMap,
            Set<Long> failedCourseIds) {
        boolean isFailed;
        String message = null;
        for (CoachingCourse coachingCourse : coachingCourseList) {
            isFailed = uploadFiles(coachingCourse);
            if (!isFailed) {
                coachingCourseRepository.upsertCoaching(coachingCourse);
                if (Objects.nonNull(coachingCourse.getCourseId())) {
                    courseMap.put(coachingCourse.getCourseId(), coachingCourse);
                    failedDataMap.remove(coachingCourse.getCourseId());
                }
            } else {
                addToFailedList(coachingCourse, message, failedDataMap, failedCourseIds,
                        true);
            }
        }
        return true;
    }

    private boolean uploadFiles(CoachingCourse coachingCourse) {
        Long courseId = coachingCourse.getCourseId();
        boolean isFailed = false;
        String classSchedule = coachingCourse.getClassSchedule();
        if (StringUtils.isNotBlank(classSchedule) && classSchedule.startsWith(HTTPS)) {
            String relativeUrl = uploadUtil.uploadFile(classSchedule, null,
                    courseId, CoachingConstants.S3RelativePath.CLASS_SCHEDULE,
                    AwsConfig.getS3CoachingBucketName(),
                    GoogleConfig.getCoachingCredentialFileName(),
                    GoogleConfig.getCoachingCredentialFolderPath()).getKey();
            if (StringUtils.isNotBlank(relativeUrl)) {
                coachingCourse.setClassSchedule(relativeUrl);
            } else {
                isFailed = true;
            }
        }
        return isFailed;
    }


}
