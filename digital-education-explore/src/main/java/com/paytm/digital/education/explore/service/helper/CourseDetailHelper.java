package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.CollegeEntityType;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.detail.Course;
import com.paytm.digital.education.explore.response.dto.search.CourseSearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.impl.CourseSearchService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.constant.ExploreConstants.COURSE_SIZE_FOR_INSTITUTE_DETAIL;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID_COURSE;
import static com.paytm.digital.education.constant.ExploreConstants.PARENT_INSTITUTE_ID_COURSE;

@Service
@AllArgsConstructor
public class CourseDetailHelper {

    private CourseSearchService courseSearchService;

    /**
     * Filtering by parent institute id for universities to get courses of all child colleges
     */
    public Pair<Long, List<Course>> getCourseDataList(List<Object> instituteIds,
            CollegeEntityType type)
            throws IOException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.COURSE);
        searchRequest.setLimit(COURSE_SIZE_FOR_INSTITUTE_DETAIL);
        searchRequest.setFetchFilter(true);
        Map<String, List<Object>> filters = new HashMap<>();
        if (type == CollegeEntityType.UNIVERSITY) {
            filters.put(PARENT_INSTITUTE_ID_COURSE, instituteIds);
        } else {
            filters.put(INSTITUTE_ID_COURSE, instituteIds);
        }
        searchRequest.setFilter(filters);
        SearchResponse response = courseSearchService.search(searchRequest);
        List<Course> courses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getResults().getValues())) {
            List<SearchBaseData> searchDataList = response.getResults().getValues();
            for (SearchBaseData searchData : searchDataList) {
                ((CourseSearchResponse) searchData).getCourses().forEach(courseData -> {
                    Course course = new Course();
                    course.setCourseId(courseData.getCourseId());
                    course.setDurationInMonth(courseData.getDurationInMonths());
                    course.setName(courseData.getOfficialName());
                    course.setUrlDisplayKey(courseData.getUrlDisplayKey());
                    course.setSeats(courseData.getSeatsAvailable());
                    course.setFee(courseData.getFee());
                    course.setAcceptingApplication(courseData.isAcceptingApplication());
                    courses.add(course);
                });
            }
            long totalCourses = response.getTotal();
            return new MutablePair<>(totalCourses, courses);
        }
        return new MutablePair<>(0L, courses);
    }

    public Pair<Long, Map<String, List<Course>>> getCourseDataPerLevel(List<Object> instituteIds,
            CollegeEntityType type, Client client) throws IOException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.COURSE);
        searchRequest.setLimit(COURSE_SIZE_FOR_INSTITUTE_DETAIL);
        searchRequest.setFetchFilter(true);
        searchRequest.setClient(client);
        Map<String, List<Object>> filters = new HashMap<>();
        if (type == CollegeEntityType.UNIVERSITY) {
            filters.put(PARENT_INSTITUTE_ID_COURSE, instituteIds);
        } else {
            filters.put(INSTITUTE_ID_COURSE, instituteIds);
        }
        searchRequest.setFilter(filters);
        SearchResponse response = courseSearchService.search(searchRequest);
        Map<String, List<Course>> coursesPerLevel = new HashMap<>();
        if (!CollectionUtils.isEmpty(response.getResults().getValues())) {
            List<SearchBaseData> searchDataList = response.getResults().getValues();
            for (SearchBaseData searchData : searchDataList) {
                ((CourseSearchResponse) searchData).getCoursesPerLevel()
                        .forEach((level, courseDataList) -> {
                            List<Course> courses = new ArrayList<>();
                            courseDataList.forEach(courseData -> {
                                Course course = new Course();
                                course.setCourseId(courseData.getCourseId());
                                course.setDurationInMonth(courseData.getDurationInMonths());
                                course.setName(courseData.getOfficialName());
                                course.setUrlDisplayKey(courseData.getUrlDisplayKey());
                                course.setSeats(courseData.getSeatsAvailable());
                                course.setFee(courseData.getFee());
                                course.setAcceptingApplication(courseData.isAcceptingApplication());
                                courses.add(course);
                            });
                            coursesPerLevel
                                    .put(CourseLevel.valueOf(level.toUpperCase()).getDisplayName(),
                                            courses);
                        });
            }
            long totalCourses = response.getTotal();
            return new MutablePair<>(totalCourses, coursesPerLevel);
        }
        return new MutablePair<>(0L, coursesPerLevel);
    }
}
