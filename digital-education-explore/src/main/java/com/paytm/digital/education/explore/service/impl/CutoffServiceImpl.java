package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Cutoff;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.request.dto.search.CutoffSearchRequest;
import com.paytm.digital.education.explore.response.dto.common.CutOff;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;
import com.paytm.digital.education.explore.service.CutoffService;
import com.paytm.digital.education.explore.utility.FieldsRetrievalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_CLASS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_CASTE_GROUP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CUTOFF_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_LIST_EXISTS;

@Slf4j
@AllArgsConstructor
@Service
public class CutoffServiceImpl implements CutoffService {

    private CommonMongoRepository commonMongoRepository;

    @Cacheable(value = "cutoff_search")
    public List<CutoffSearchResponse> searchCutOffs(CutoffSearchRequest cutoffSearchRequest) {
        List<String> projectionFields = commonMongoRepository
                .getFieldsByGroup(Course.class, cutoffSearchRequest.getFieldGroup());
        if (projectionFields != null) {
            Map<String, ArrayList<String>> allFields =
                    FieldsRetrievalUtil.getFormattedFields(projectionFields, COURSE_CLASS);
            ArrayList<String> courseProjectionFields = allFields.get(COURSE.name().toLowerCase());
            List<Course> courseAndCutoffs = commonMongoRepository
                    .findAll(buildQueryObject(cutoffSearchRequest), Course.class,
                            courseProjectionFields);
            return buildResponse(courseAndCutoffs, cutoffSearchRequest);
        } else {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }
    }

    private Map<String, Object> buildQueryObject(CutoffSearchRequest cutoffSearchRequest) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, cutoffSearchRequest.getInstituteId());
        queryObject.put(CUTOFF_EXAM_ID, cutoffSearchRequest.getExamId());
        queryObject.put(CUTOFF_CASTE_GROUP, cutoffSearchRequest.getCasteGroup());
        queryObject.put(CUTOFF_GENDER, cutoffSearchRequest.getGender());
        return queryObject;
    }

    private List<CutoffSearchResponse> buildResponse(List<Course> courses,
            CutoffSearchRequest cutoffSearchRequest) {
        List<CutoffSearchResponse> response = new ArrayList<>();
        for (Course course : courses) {
            CutoffSearchResponse individualResponse = new CutoffSearchResponse();
            individualResponse.setCourseDuration(course.getCourseDuration());
            individualResponse.setCourseId(course.getCourseId());
            individualResponse.setCourseNameOfficial(course.getCourseNameOfficial());
            individualResponse.setMasterBranch(course.getMasterBranch());
            individualResponse.setCourseLevel(course.getCourseLevel());
            individualResponse.setCutOffs(getCutOffs(course.getCutoffs(), cutoffSearchRequest));
            response.add(individualResponse);
        }
        return response;
    }

    private List<CutOff> getCutOffs(List<Cutoff> cutoffs, CutoffSearchRequest cutoffSearchRequest) {
        List<CutOff> responseCutoffs = new ArrayList<>();
        for (Cutoff cutoff : cutoffs) {
            if (cutoff.getCasteGroup().equals(cutoffSearchRequest.getCasteGroup()) && cutoff
                    .getGender().equals(cutoffSearchRequest.getGender())
                    && cutoff.getExamId() == cutoffSearchRequest.getExamId()) {
                CutOff individualCutOff = new CutOff();
                individualCutOff.setCasteGroup(cutoff.getCasteGroup());
                individualCutOff.setCutoffType(cutoff.getCutoffType());
                individualCutOff.setGender(cutoff.getGender());
                individualCutOff.setExamId(cutoff.getExamId());
                individualCutOff.setValue(cutoff.getFinalValue());
                individualCutOff.setMeritListType(cutoff.getMeritListType());
                individualCutOff.setYear(cutoff.getYear());
                responseCutoffs.add(individualCutOff);
            }
        }
        return responseCutoffs;
    }

    @Cacheable(value = "cutoff_get_list")
    public ExamAndCutOff getSearchList(long instituteId, long examId) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(INSTITUTE_ID, instituteId);
        queryParams.put(CUTOFF_EXAM_ID, examId);
        ExamAndCutOff genderCasteList = getGenderCasteGroupList(queryParams);
        if (genderCasteList.getCasteGroups() == null) {
            throw new NotFoundException(NO_LIST_EXISTS,
                    NO_LIST_EXISTS.getExternalMessage());
        }
        return genderCasteList;
    }

    private ExamAndCutOff getGenderCasteGroupList(Map<String, Object> queryParams) {
        ExamAndCutOff examAndCutOff = new ExamAndCutOff();
        List<String> projectionFields = Arrays.asList(CUTOFF);
        List<Course> courses = commonMongoRepository
                .findAll(queryParams, Course.class,
                        projectionFields);
        Set<Gender> genders = new HashSet<>();
        Set<String> casteGroups = new HashSet<>();
        for (Course course : courses) {
            for (Cutoff cutoff : course.getCutoffs()) {
                genders.add(cutoff.getGender());
                casteGroups.add(cutoff.getCasteGroup());
            }
        }
        if (!casteGroups.isEmpty()) {
            examAndCutOff.setCasteGroups(new ArrayList<>(casteGroups));
            examAndCutOff.setGenders(new ArrayList<>(genders));
        }
        return examAndCutOff;
    }

}
