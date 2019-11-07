package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_IDS;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.MERCHANT_CAREER_360;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.database.ingestion.Course;
import com.paytm.digital.education.explore.database.ingestion.Cutoff;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.explore.service.helper.StreamDataTranslator;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class TransformAndSaveCourseService {

    private static Logger log = LoggerFactory.getLogger(TransformAndSaveCourseService.class);

    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;
    private StreamDataTranslator streamDataTranslator;

    public Integer transformAndSave(List<Course> courseDtos, Boolean versionUpdate) throws
            EducationException {
        int courseUpdated = 0;
        try {
            Map<String, Object> courseData = transformData(courseDtos);
            List<Long> courseIds = (List<Long>) courseData.get(COURSE_IDS);
            List<Course> courses = (List<Course>) courseData.get(COURSES);
            Map<Long, String> map = new HashMap<>();
            if (!courseIds.isEmpty()) {
                List<Course> existingCourse =
                        incrementalDataHelper.getExistingData(Course.class, COURSE_ID,
                                courseIds);
                map = existingCourse.stream().collect(
                        Collectors.toMap(c -> c.getCourseId(), c -> c.getId(), (c1, c2) -> c2));
            }
            log.info("Saving courses to db.");

            for (Course course : courses) {
                String id = map.get(course.getCourseId());
                if (StringUtils.isNotBlank(id)) {
                    course.setId(id);
                }
                //set paytm stream ids
                if (!CollectionUtils.isEmpty(course.getStreams())) {
                    course.setStreamIds(streamDataTranslator
                            .getPaytmStreams(course.getStreams(), MERCHANT_CAREER_360,
                                    course.getCourseId(),
                                    com.paytm.digital.education.database.entity.Course.class));
                }
                commonMongoRepository.saveOrUpdate(course);
                courseUpdated++;
            }
            log.info("Saved courses to db.");
            if (Objects.isNull(versionUpdate) || versionUpdate == true) {
                log.info("Updating version number for course");
                incrementalDataHelper.incrementFileVersion(COURSE_FILE_VERSION);
            }
        } catch (Exception e) {
            log.error("Course ingestion exception : {}, {}", e, e.getMessage());
            throw new BadRequestException(ErrorEnum.CORRUPTED_FILE,
                    ErrorEnum.CORRUPTED_FILE.getExternalMessage());
        }
        return courseUpdated;
    }

    private Map<String, Object> transformData(List<Course> courses) {
        Map<String, Object> response = new HashMap<>();
        Set<Long> courseIds = new HashSet<>();
        Set<Course> courseSet = new HashSet<>();

        log.info("Transforming Courses.");
        for (Course course : courses) {
            log.info("Found Course document from dump for id : {}, {} ", course.getCourseId(),
                    JsonUtils.toJson(course));
            List<Cutoff> cutoffs = course.getCutoffs();
            if (Objects.nonNull(cutoffs)) {
                List<Cutoff> cutoffList = new ArrayList<>();
                for (Cutoff cutoff : cutoffs) {
                    if (Objects.nonNull(cutoff.getLocation())) {
                        String location = cutoff.getLocation().replace("_", " ");
                        cutoff.setLocation(location);
                    }
                    if (Objects.nonNull(cutoff.getMeritListType())) {
                        String meritList = cutoff.getMeritListType().replace("_", " ");
                        cutoff.setMeritListType(meritList);
                    }
                    if (Objects.nonNull(cutoff.getCasteGroup())) {
                        String casteGroup = cutoff.getCasteGroup().replace("_", " ");
                        cutoff.setCasteGroup(casteGroup);
                    }
                    cutoffList.add(cutoff);
                }
            }
            course.setCutoffs(cutoffs);
            if (!courseIds.contains(course.getCourseId())) {
                courseIds.add(course.getCourseId());
                courseSet.add(course);
            }
        }
        response.put(COURSE_IDS, new ArrayList<>(courseIds));
        response.put(COURSES, new ArrayList<>(courseSet));
        return response;
    }
}
