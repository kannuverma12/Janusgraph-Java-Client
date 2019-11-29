package com.paytm.digital.education.coaching.consumer.service.details;

import com.fasterxml.jackson.core.type.TypeReference;
import com.paytm.digital.education.coaching.consumer.model.dto.LandingPageStreamDto;
import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCoursesTopHitsData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamsTopHitsData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.service.search.CoachingCourseSearchService;
import com.paytm.digital.education.coaching.consumer.service.search.ExamSearchService;
import com.paytm.digital.education.coaching.consumer.service.search.helper.SearchDataHelper;
import com.paytm.digital.education.coaching.consumer.transformer.LandingPageStreamTransformer;
import com.paytm.digital.education.database.dao.CoachingStreamDAO;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.LANDING_PAGE_TOP_COURSES_LIMIT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LANDING_PAGE_TOP_COURSES_PER_STREAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LANDING_PAGE_TOP_EXAMS_LIMIT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LANDING_PAGE_TOP_EXAMS_PER_STREAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.FULL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.URL_DISPLAY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_ENTITY_POSITION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TOP_INSTITUTES_COUNT_LANDING_PAGE;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_STREAMS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_COURSES;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES;
import static com.paytm.digital.education.enums.EducationEntity.COACHING_COURSE;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;

@Service
@AllArgsConstructor
@Slf4j
public class LandingPageService {

    private SearchDataHelper             searchDataHelper;
    private ExamSearchService            examSearchService;
    private CoachingCourseSearchService  courseSearchService;
    private LandingPageStreamTransformer landingPageStreamTransformer;
    private CoachingStreamDAO            coachingStreamDAO;

    public void addDynamicData(List<Section> sections) {
        Map<Sort.Direction, String> sortMap = new HashMap<>();
        sortMap.put(Sort.Direction.ASC, "priority");
        List<StreamEntity> streamEntities = coachingStreamDAO.findAllAndSortBy(sortMap);
        List<String> streamIds =
                streamEntities.stream().map(e -> String.valueOf(e.getStreamId()))
                        .collect(Collectors.toList());
        for (Section section : sections) {
            switch (section.getType()) {
                case COACHING_STREAMS:
                    addStreams(section, streamEntities);
                    break;
                case TOP_COACHING_INSTITUTES:
                    addTopCoachingInstitutes(section);
                    break;
                case COACHING_TOP_EXAMS:
                    addTopExams(section, streamIds);
                    break;
                case COACHING_TOP_COURSES:
                    addTopCourses(section, streamIds);
                    break;
                default:
            }
        }
    }

    private void addStreams(Section section,
            List<StreamEntity> streamEntities) {
        List<Map<String, Object>> itemList = new ArrayList<>();
        List<LandingPageStreamDto> landingPageStreamDtoList = landingPageStreamTransformer
                .getLandingPageStreamDtoFromStreamEntity(streamEntities);
        if (!CollectionUtils.isEmpty(landingPageStreamDtoList)) {
            for (LandingPageStreamDto landingPageStreamDto : landingPageStreamDtoList) {
                itemList.add(getItemFromStreamDto(landingPageStreamDto));
            }
        }
        section.setItems(itemList);
    }

    private void addTopCoachingInstitutes(Section section) {
        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_ENTITY_POSITION, ASC);
        List<CoachingInstituteData> institutes =
                (List<CoachingInstituteData>) (List<?>) searchDataHelper
                        .getTopSearchData(null, EducationEntity.COACHING_INSTITUTE, sortOrder,
                                TOP_INSTITUTES_COUNT_LANDING_PAGE);
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (CoachingInstituteData coachingInstitute : institutes) {
            itemList.add(
                    getItem(coachingInstitute.getBrandName(), coachingInstitute.getUrlDisplayKey(),
                            coachingInstitute.getCoachingInstituteId(), coachingInstitute.getLogo(),
                            null));
        }
        section.setItems(itemList);
    }

    private void addTopExams(Section section, List<String> streamsInOrder) {
        List<ExamData> exams = getTopExamsPerStream(streamsInOrder);
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(exams)) {
            for (ExamData exam : exams) {
                itemList.add(
                        getItem(exam.getExamShortName(), exam.getUrlDisplayKey(), exam.getExamId(),
                                exam.getLogoUrl(), exam.getOfficialName()));
            }

            if (itemList.size() > LANDING_PAGE_TOP_EXAMS_LIMIT) {
                itemList = itemList.subList(0, LANDING_PAGE_TOP_EXAMS_LIMIT);
            }
            section.setItems(itemList);
        }
    }

    private List<ExamData> getTopExamsPerStream(List<String> streamsInOrder) {
        LinkedHashMap<String, DataSortOrder> sortOrderMap = new LinkedHashMap<>();
        sortOrderMap.put(IGNORE_ENTITY_POSITION, ASC);
        SearchRequest searchRequest = SearchRequest.builder()
                .fetchSearchResults(false)
                .entity(EXAM)
                .sortOrder(sortOrderMap)
                .dataPerFilter(STREAM_IDS)
                .fetchFilter(true)
                .fetchSearchResultsPerFilter(true)
                .limit(LANDING_PAGE_TOP_EXAMS_PER_STREAM)
                .build();
        try {
            SearchResponse searchResponse = examSearchService.search(searchRequest);
            return fetchTopExamsPerStreamFromSearchResponse(searchResponse, streamsInOrder);
        } catch (IOException e) {
            log.error("IO Exception for fetching top exams per stream "
                    + "", e);
        } catch (TimeoutException e) {
            log.error("Timeout exception for fetching top top exams per stream ",
                    e);
        }
        return null;
    }

    private void addTopCourses(Section section, List<String> streamsInOrder) {
        List<CoachingCourseData> courses = getTopCoursesPerStream(streamsInOrder);
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(courses)) {
            for (CoachingCourseData course : courses) {
                final Map<String, Object> courseDataMap = JsonUtils.convertValue(course,
                        new TypeReference<Map<String, Object>>() {
                        });

                itemList.add(courseDataMap);
            }

            if (itemList.size() > LANDING_PAGE_TOP_COURSES_LIMIT) {
                itemList = itemList.subList(0, LANDING_PAGE_TOP_COURSES_LIMIT);
            }
            section.setItems(itemList);
        }
    }

    private List<CoachingCourseData> getTopCoursesPerStream(List<String> streamsInOrder) {
        LinkedHashMap<String, DataSortOrder> sortOrderMap = new LinkedHashMap<>();
        sortOrderMap.put(GLOBAL_PRIORITY, ASC);
        SearchRequest searchRequest = SearchRequest.builder()
                .fetchSearchResults(false)
                .entity(COACHING_COURSE)
                .sortOrder(sortOrderMap)
                .dataPerFilter(STREAM_IDS)
                .fetchFilter(true)
                .fetchSearchResultsPerFilter(true)
                .limit(LANDING_PAGE_TOP_COURSES_PER_STREAM)
                .build();
        try {
            SearchResponse searchResponse = courseSearchService.search(searchRequest);
            return fetchTopCoursesPerStreamFromSearchResponse(searchResponse, streamsInOrder);
        } catch (Exception e) {
            log.error("Exception for fetching top exams per stream ", e);
        }

        return null;
    }

    private List<ExamData> fetchTopExamsPerStreamFromSearchResponse(SearchResponse searchResponse,
            List<String> streamsInOrder) {
        Map<String, List<ExamData>> examsPerStream =
                ((ExamsTopHitsData) searchResponse.getResults().getValues().get(0))
                        .getExamsPerStream();
        List<ExamData> exams = new ArrayList<>();
        Set<Integer> examIds = new HashSet<>();
        for (String stream : streamsInOrder) {
            List<ExamData> examData = examsPerStream.get(stream);
            if (!CollectionUtils.isEmpty(examData)) {
                for (ExamData exam : examData) {
                    if (examIds.add(exam.getExamId())) {
                        exams.add(exam);
                        break;
                    }
                }
            }
        }
        return exams;
    }

    private List<CoachingCourseData> fetchTopCoursesPerStreamFromSearchResponse(
            SearchResponse searchResponse,
            List<String> streamsInOrder) {
        Map<String, List<CoachingCourseData>> coursesPerStream =
                ((CoachingCoursesTopHitsData) searchResponse.getResults().getValues().get(0))
                        .getCoursesPerStream();
        List<CoachingCourseData> courses = new ArrayList<>();
        Set<Long> courseIds = new HashSet<>();
        for (String stream : streamsInOrder) {
            List<CoachingCourseData> courseData = coursesPerStream.get(stream);
            if (!CollectionUtils.isEmpty(courseData)) {
                for (CoachingCourseData course : courseData) {
                    if (courseIds.add(course.getCourseId())) {
                        courses.add(course);
                        break;
                    }
                }
            }
        }

        return courses;
    }

    private Map<String, Object> getItem(String name, String urlDisplayKey, Object id, String logo,
            String fullName) {
        Map<String, Object> item = new HashMap<>();
        item.put(NAME, name);
        item.put(ID, id);
        item.put(URL_DISPLAY_KEY, urlDisplayKey);
        item.put(LOGO, logo);
        if (StringUtils.isNotBlank(fullName)) {
            item.put(FULL_NAME, fullName);
        }
        return item;
    }

    private Map<String, Object> getItemFromStreamDto(LandingPageStreamDto landingPageStreamDto) {
        Map<String, Object> item = new HashMap<>();
        item.put(NAME, landingPageStreamDto.getDisplayName());
        item.put(ID, landingPageStreamDto.getEntityId());
        item.put(URL_DISPLAY_KEY, landingPageStreamDto.getUrlDisplayKey());
        item.put(LOGO, landingPageStreamDto.getLogo());
        if (StringUtils.isNotBlank(landingPageStreamDto.getFullName())) {
            item.put(FULL_NAME, landingPageStreamDto.getFullName());
        }
        return item;
    }
}
