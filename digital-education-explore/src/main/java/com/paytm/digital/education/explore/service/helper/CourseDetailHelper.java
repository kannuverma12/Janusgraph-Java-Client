package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.entity.Course;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseDetailHelper {

    public List<com.paytm.digital.education.explore.response.dto.detail.Course> getCoursesListing(
            List<Course> courseList) {
        if (!CollectionUtils.isEmpty(courseList)) {
            List<com.paytm.digital.education.explore.response.dto.detail.Course> responseList =
                    new ArrayList<>();
            for (Course course : courseList) {
                com.paytm.digital.education.explore.response.dto.detail.Course courseData =
                        new com.paytm.digital.education.explore.response.dto.detail.Course();
                courseData.setCourseId(course.getCourseId());
                courseData.setName(course.getCourseNameOfficial());
                courseData.setDurationInMonth(course.getCourseDuration());
                courseData.setSeats(course.getSeatsAvailable());
                responseList.add(courseData);
            }
            return responseList;
        }
        return null;
    }
}
