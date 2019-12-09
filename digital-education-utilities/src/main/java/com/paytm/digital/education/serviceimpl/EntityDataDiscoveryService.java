package com.paytm.digital.education.serviceimpl;

import static com.paytm.digital.education.constant.ExploreConstants.ABOUT_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.APP_DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.DESCRIPTION;
import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.DUMMY_EXAM_ICON;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.constant.ExploreConstants.ID;
import static com.paytm.digital.education.constant.ExploreConstants.IMAGE_URL;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.NAME;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_ITEMS;
import static com.paytm.digital.education.constant.ExploreConstants.URL_DISPLAY_KEY;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_LOGO;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_OFFICIAL_NAME;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.mapping.ErrorEnum.PAYTM_STREAM_DISABLED;

import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntityDataDiscoveryService {

    private static final List<String> INSTITUTE_PROJECTION_FIELDS =
            Arrays.asList(INSTITUTE_ID, OFFICIAL_NAME, GALLERY_LOGO);
    private static final List<String> EXAM_PROJECTION_FIELDS      =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO);
    private static final List<String> EXAM_FIELDS_WITH_ABOUT     =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO, ABOUT_EXAM);
    private static final List<String> SCHOOL_PROJECTION_FIELDS    =
            Arrays.asList(SCHOOL_ID, SCHOOL_OFFICIAL_NAME, SCHOOL_LOGO);
    private static final Logger       log                         =
            LoggerFactory.getLogger(EntityDataDiscoveryService.class);
    private final CommonMongoRepository commonMongoRepository;
    private final StreamDAO             streamDAO;
    private final SchoolConfig          schoolConfig;

    public void updateInstituteData(Section section) {
        List<Map<String, Object>> sectionItems = section.getItems();
        if (!CollectionUtils.isEmpty(sectionItems)) {
            List<Long> instituteIds = sectionItems.stream()
                    .map(item -> ((Integer) item.get(INSTITUTE_ID)).longValue())
                    .collect(Collectors.toList());
            Map<Long, Institute> instituteMap = getInstituteMap(instituteIds);
            for (Map<String, Object> item : sectionItems) {
                Long instituteId = ((Integer)item.get(INSTITUTE_ID)).longValue();
                if (instituteMap.containsKey(instituteId)) {
                    Institute institute = instituteMap.get(instituteId);
                    item.put(NAME, institute.getOfficialName());
                    item.put(URL_DISPLAY_KEY,
                            CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
                    item.put(LOGO,
                            CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                    item.put(ICON,
                            CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                } else {
                    log.error("Institute Id : {} of College in focus not found in the databse.",
                            item.get(INSTITUTE_ID));
                }
            }
        }
    }

    public void updatePaytmStreamData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            Map<Long, StreamEntity> streamEntityMap = streamDAO.getStreamEntityMapById();
            for (Map<String, Object> item : section.getItems()) {
                Long streamId = ((Integer) item.get(STREAM_IDS)).longValue();
                StreamEntity streamEntity = streamEntityMap.get(streamId);
                if (Objects.nonNull(streamEntity) && streamEntity.getIsEnabled()) {
                    item.put(STREAM_IDS, streamId);
                    item.put(NAME, streamEntity.getName());
                    item.put(APP_DISPLAY_NAME, streamEntity.getShortName());
                    item.put(ICON,
                            CommonUtil.getAbsoluteUrl(streamEntity.getLogo(), section.getType()));
                } else {
                    throw new EducationException(PAYTM_STREAM_DISABLED,
                            PAYTM_STREAM_DISABLED.getExternalMessage(), new Object[] {streamId});
                }
            }
        }
    }

    public void updateTopExamsAppData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            List<Long> examIds = new ArrayList<>();
            for (Map<String, Object> item : section.getItems()) {
                for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {
                    Map<String, Object> subitems =
                            (Map<String, Object>) topExamsPerLevel.getValue();
                    List<Map<String, Object>> topExams =
                            (List<Map<String, Object>>) Optional.ofNullable(subitems.get(SUB_ITEMS))
                                    .orElse(new ArrayList<>());
                    List<Long> itemsExamIds =
                            topExams.stream().map(exam -> ((Integer) exam.get(EXAM_ID)).longValue())
                                    .collect(Collectors.toList());
                    examIds.addAll(itemsExamIds);
                }
            }

            Map<Long, Exam> examEntityMap = getExamDataMap(examIds, EXAM_PROJECTION_FIELDS);
            for (Map<String, Object> item : section.getItems()) {
                for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {
                    Map<String, Object> subitems =
                            (Map<String, Object>) topExamsPerLevel.getValue();
                    List<Map<String, Object>> topExams =
                            (List<Map<String, Object>>) Optional.ofNullable(subitems.get(SUB_ITEMS))
                                    .orElse(new ArrayList<>());
                    updateExamData(topExams, examEntityMap);
                }
            }
        }
    }

    public void updateTopExamsData(Section section) {
        List<Long> examIds = new ArrayList<>();
        for (Map<String, Object> item : section.getItems()) {
            for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {
                List<Map<String, Object>> subitems =
                        (List<Map<String, Object>>) topExamsPerLevel.getValue();
                List<Long> itemsExamIds =
                        subitems.stream().map(exam -> ((Integer) exam.get(EXAM_ID)).longValue())
                                .collect(Collectors.toList());
                examIds.addAll(itemsExamIds);
            }
        }
        Map<Long, Exam> examEntityMap = getExamDataMap(examIds, EXAM_PROJECTION_FIELDS);
        for (Map<String, Object> item : section.getItems()) {
            for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {
                List<Map<String, Object>> subitems =
                        (List<Map<String, Object>>) topExamsPerLevel.getValue();
                updateExamData(subitems, examEntityMap);
            }
        }
    }

    public void updatePopularExamsData(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            List<Long> examIds = section.getItems().stream()
                    .map(item -> ((Integer)item.get(EXAM_ID)).longValue())
                    .collect(Collectors.toList());
            Map<Long, Exam> examDataMap = getExamDataMap(examIds, EXAM_PROJECTION_FIELDS);
            updateExamData(section.getItems(), examDataMap);
        }
    }

    public void updateExamFocusData(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            List<Long> examIds = section.getItems().stream()
                    .map(item -> ((Integer)item.get(EXAM_ID)).longValue())
                    .collect(Collectors.toList());
            Map<Long, Exam> examDataMap = getExamDataMap(examIds, EXAM_FIELDS_WITH_ABOUT);
            updateExamData(section.getItems(), examDataMap);
        }
    }

    private void updateExamData(List<Map<String, Object>> examDataList, Map<Long, Exam> examMap) {
        for (Map<String, Object> examData : examDataList) {
            Long examId = ((Integer) examData.get(EXAM_ID)).longValue();
            if (examMap.containsKey(examId)) {
                Exam exam = examMap.get(examId);
                examData.put(FULL_NAME, exam.getExamFullName());
                examData.put(NAME, exam.getExamShortName());
                String logoUrl = StringUtils.isNotBlank(exam.getLogo())
                        ? exam.getLogo() : DUMMY_EXAM_ICON;
                examData.put(LOGO, CommonUtil.getLogoLink(logoUrl, EXAM));
                examData.put(ICON, CommonUtil.getLogoLink(logoUrl, EXAM));
                examData.put(URL_DISPLAY_KEY,
                        CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
                if (StringUtils.isNotBlank(exam.getAboutExam())) {
                    examData.put(DESCRIPTION, exam.getAboutExam());
                }
            } else {
                log.error(
                        "Exam id : {} doesn't exists for exam entity in our database.",
                        examData.get(EXAM_ID));
            }
        }
    }

    public void updateSchoolData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            List<Long> schoolIds = section.getItems().stream()
                            .map(item -> ((Integer)item.get(SCHOOL_ID)).longValue())
                            .collect(Collectors.toList());
            Map<Long, School> schoolEntityMap = getSchoolEntityMap(schoolIds);
            for (Map<String, Object> item : section.getItems()) {
                Long schoolId = ((Integer) item.get(SCHOOL_ID)).longValue();
                if (schoolEntityMap.containsKey(schoolId)) {
                    School school = schoolEntityMap.get(schoolId);
                    item.put(SCHOOL_OFFICIAL_NAME, school.getOfficialName());
                    String logoUrl = (Objects.nonNull(school.getGallery()) && StringUtils
                            .isNotBlank(school.getGallery().getLogo()))
                            ? school.getGallery().getLogo() : schoolConfig.getSchoolPlaceholderLogoURL();
                    item.put(ICON, CommonUtil.getLogoLink(logoUrl, SCHOOL));
                    item.put(LOGO, item.get(ICON));
                    item.put(URL_DISPLAY_KEY,
                            CommonUtil.convertNameToUrlDisplayName(school.getOfficialName()));
                } else {
                    log.error("School Id : {} not found in our database of school entity.",
                            item.get(SCHOOL_ID));
                }
            }
        }
    }

    public void updateLocationData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            for (Map<String, Object> item : section.getItems()) {
                item.put(ICON, CommonUtil.getAbsoluteUrl(item.get(ICON).toString(),
                        section.getType()));
            }
        }
    }

    public void updateAppFooterData(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            for (Map<String, Object> item : section.getItems()) {
                item.put(ICON, CommonUtil.getAbsoluteUrl(item.get(ICON).toString(),
                        section.getType()));
            }
        }
    }

    public void updateExamsFooterData(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            List<Long> examIds = section.getItems().stream()
                    .map(item -> ((Integer) item.get(ID)).longValue())
                    .collect(Collectors.toList());
            Map<Long, Exam> examEntityMap = getExamDataMap(examIds, EXAM_PROJECTION_FIELDS);
            for (Map<String, Object> item : section.getItems()) {
                Long examId = ((Integer) item.get(ID)).longValue();
                if (examEntityMap.containsKey(examId)) {
                    Exam exam = examEntityMap.get(examId);
                    item.put(DISPLAY_NAME, exam.getExamShortName());
                    item.put(URL_DISPLAY_KEY,
                            CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
                } else {
                    log.error("Exam Id : {} not present in our database .", examId);
                }
            }
        }
    }

    public void updateBannerLinks(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            for (Map<String, Object> item : section.getItems()) {
                item.put(IMAGE_URL, CommonUtil
                        .getAbsoluteUrl(item.get(IMAGE_URL).toString(), section.getType()));
            }
        }
    }

    private Map<Long, Exam> getExamDataMap(List<Long> examIds, List<String> fields) {
        List<Exam> examList = commonMongoRepository
                .getEntityFieldsByValuesIn(EXAM_ID, examIds, Exam.class, fields);
        return Optional.ofNullable(examList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Exam::getExamId, Function.identity()));
    }

    private Map<Long, Institute> getInstituteMap(List<Long> instituteIds) {
        List<Institute> instituteList = commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, instituteIds, Institute.class,
                        INSTITUTE_PROJECTION_FIELDS);
        return Optional.ofNullable(instituteList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Institute::getInstituteId, Function
                        .identity()));
    }

    private Map<Long, School> getSchoolEntityMap(List<Long> schoolIds) {
        List<School> instituteList = commonMongoRepository
                .getEntityFieldsByValuesIn(SCHOOL_ID, schoolIds, School.class,
                        SCHOOL_PROJECTION_FIELDS);
        return Optional.ofNullable(instituteList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(School::getSchoolId, Function
                        .identity()));
    }
}
