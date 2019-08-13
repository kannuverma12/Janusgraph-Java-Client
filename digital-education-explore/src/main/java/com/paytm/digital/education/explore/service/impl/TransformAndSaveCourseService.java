package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.ingestion.Course;
import com.paytm.digital.education.explore.database.ingestion.Cutoff;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_IDS;

@Service
@AllArgsConstructor
@Slf4j
public class TransformAndSaveCourseService {
    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

    public void transformAndSave(List<Course> courseDtos, Boolean versionUpdate) {
        try {
            Map<String, Object> courseData = transformData(courseDtos);
            List<Long> courseIds = (List<Long>) courseData.get(COURSE_IDS);
            List<Course> courses = (List<Course>) courseData.get(COURSES);
            Map<Long, String> map = new HashMap<>();
            if (!courseIds.isEmpty()) {
                List<Course> existingCourse =
                        incrementalDataHelper.getExistingData(Course.class, COURSE_ID,
                                courseIds);
                map = existingCourse.stream()
                        .collect(Collectors.toMap(c -> c.getCourseId(), c -> c.getId()));
            }
            for (Course course : courses) {
                String id = map.get(course.getCourseId());
                if (StringUtils.isNotBlank(id)) {
                    course.setId(id);
                }
                commonMongoRepository.saveOrUpdate(course);
            }
            if (Objects.isNull(versionUpdate) || versionUpdate == true) {
                incrementalDataHelper.incrementFileVersion(COURSE_FILE_VERSION);
            }
        } catch (Exception e) {
            log.info("Course ingestion exception : " + e.getMessage());
        }
    }

    private Map<String, Object> transformData(List<Course> courses) {
        Map<String, Object> response = new HashMap<>();
        Set<Long> courseIds = new HashSet<>();
        Set<Course> courseSet = new HashSet<>();

        for (Course course : courses) {
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
        log.info("courseIds : " + courseIds.size() + ", courseSet : " + courseSet.size());
        response.put(COURSE_IDS, new ArrayList<>(courseIds));
        response.put(COURSES, new ArrayList<>(courseSet));
        return response;
    }
}
