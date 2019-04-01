package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.PARENT_INSTITUTE_ID_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_SIZE_FOR_INSTITUTE_DETAIL;
import com.paytm.digital.education.explore.enums.CollegeEntityType;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.impl.CourseSearchService;
import com.paytm.digital.education.explore.response.dto.detail.Course;
import com.paytm.digital.education.explore.response.dto.search.CourseSearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class CourseDetailHelper {

    private CourseSearchService courseSearchService;

    /**
     * Filtering by parent institute id for universities to get courses of all child colleges
     */
    public List<Course> addCourseData(List<Object> instituteIds, CollegeEntityType type)
            throws IOException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.COURSE);
        searchRequest.setLimit(COURSE_SIZE_FOR_INSTITUTE_DETAIL);
        searchRequest.setFetchFilter(false);
        Map<String, List<Object>> filters = new HashMap<>();
        if (type == CollegeEntityType.UNIVERSITY) {
            filters.put(PARENT_INSTITUTE_ID_COURSE, instituteIds);
        } else {
            filters.put(INSTITUTE_ID_COURSE, instituteIds);
        }
        searchRequest.setFilter(filters);
        SearchResponse response = courseSearchService.search(searchRequest);
        if (!CollectionUtils.isEmpty(response.getResults().getValues())) {
            List<Course> courses = new ArrayList<>();
            List<SearchBaseData> searchDataList = response.getResults().getValues();
            for (SearchBaseData searchData : searchDataList) {
                ((CourseSearchResponse) searchData).getCourses().forEach(courseData -> {
                    Course course = new Course();
                    course.setCourseId(courseData.getCourseId());
                    course.setDurationInMonth(courseData.getDurationInMonths());
                    course.setName(courseData.getOfficialName());
                    course.setSeats(courseData.getSeatsAvailable());
                    courses.add(course);
                });
            }
            return courses;
        }
        return null;
    }
}
