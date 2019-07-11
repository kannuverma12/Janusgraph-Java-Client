package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.CourseDto;
import com.paytm.digital.education.explore.dto.CutoffDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSES;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.COURSE_IDS;

@Service
@AllArgsConstructor
@Slf4j
public class TransformAndSaveCourseService {
    private IncrementalDataHelper incrementalDataHelper;
    private CommonMongoRepository commonMongoRepository;

    public void transformAndSave(List<CourseDto> courseDtos) {
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
    }

    private Map<String, Object> transformData(List<CourseDto> courseDtos) {
        Map<String, Object> response = new HashMap<>();
        List<Long> courseIds = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
        for (CourseDto courseDto : courseDtos) {
            List<CutoffDto> cutoffs = courseDto.getCutoffs();
            if (Objects.nonNull(cutoffs)) {
                List<CutoffDto> cutoffList = new ArrayList<>();
                for (CutoffDto cutoff : cutoffs) {
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
            courseDto.setCutoffs(cutoffs);
            Course course = new Course();
            BeanUtils.copyProperties(courseDto, course);
            courses.add(course);
            courseIds.add(course.getCourseId());
        }
        response.put("course_ids", courseIds);
        response.put("courses", courses);
        return response;
    }
}
