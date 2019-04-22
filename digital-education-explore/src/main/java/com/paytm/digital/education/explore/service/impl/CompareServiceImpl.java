package com.paytm.digital.education.explore.service.impl;


import com.paytm.digital.education.explore.database.entity.*;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import com.paytm.digital.education.explore.response.dto.detail.CompareInstDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.service.CompareService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.*;

@Slf4j
@Service
public class CompareServiceImpl implements CompareService {

    @Autowired
    private InstituteDetailServiceImpl instituteDetailService;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    @Autowired
    private StreamDataHelper streamDataHelper;

    @Override
    public CompareDetail compareInstitutes(List<Long> instList, String fieldGroup,
            List<String> fields) throws IOException, TimeoutException {

        CompareDetail compareDetail = new CompareDetail();
        List<CompareInstDetail> instituteDetailsList = new ArrayList<>();


        for (Long inst : instList) {
            if(Objects.nonNull(inst)) {

                Institute institute = instituteDetailService.getinstitute(inst, fieldGroup);

                CompareInstDetail instDetail = getCompareInstDetail(institute);

                instituteDetailsList.add(instDetail);
            }
        }
        if (!CollectionUtils.isEmpty(instituteDetailsList)){
            compareDetail.setInstitutes(instituteDetailsList);
        }


        return compareDetail;
    }

    private CompareInstDetail getCompareInstDetail(Institute inst) {
        CompareInstDetail cDetail = new CompareInstDetail();

        cDetail.setInstitute_id(inst.getInstituteId());
        if (!Objects.nonNull(inst.getCampusSize())) {
            cDetail.setCampusArea(inst.getCampusSize() + " Acres");
        }
        cDetail.setTotalIntake(inst.getTotalIntake());                              //no data available year wise
        cDetail.setApprovals(getApprovalDetail(inst.getApprovals()));               //need to check logic for multiple approvals
        cDetail.setFacilities(getFacilitiesDetail(inst.getFacilities()));           //filter from master list
        cDetail.setRankings(getResponseRankingList(inst.getRankings()));            //check logic for multiple rankings?
        cDetail.setPlacements(getPlacementDetail(inst.getSalariesPlacement()));     // generate string for placements

        List<Course> courses = getCourses(inst.getInstituteId());
        if(!CollectionUtils.isEmpty(courses)){
            cDetail.setCourseLevel(getCourseLevel(courses));

            cDetail.setExamAccepted(getExamAccepted(courses));

            cDetail.setMinimumCourseFee(getMinCourseFee(courses));

            cDetail.setStreamsPreparedFor(getStreams(courses));
        }

        return cDetail;


    }

    private Set<String> getStreams(List<Course> courses) {
        Map<String, String> streamMap = streamDataHelper.getStreamMap();

        Set<String> streamList = courses.stream().filter(c -> Objects.nonNull(c.getStreams()))
                .flatMap(c -> c.getStreams().stream())
                .filter(st -> Objects.nonNull(streamMap.get(st.toLowerCase())))
                .map(st ->  streamMap.get(st.toLowerCase()))
                .collect(Collectors.toSet());

        return streamList;

    }

    private Long getMinCourseFee(List<Course> courses) {
        Long minFee = Long.MAX_VALUE;
        for (Course c : courses) {
            List<CourseFee> feesList = c.getCourseFees();

            if (!CollectionUtils.isEmpty(feesList)) {
                if(feesList.size() > 1){
                    for (CourseFee fee : feesList) {
                        if (Objects.nonNull(fee.getCasteGroup()) && fee.getCasteGroup()
                                .equalsIgnoreCase("general")) {
                            //some fee object do not have caste group
                            if(Objects.nonNull(fee.getFee())){
                                minFee = Math.min(minFee, fee.getFee());
                            }
                        }
                    }
                }else{
                    CourseFee fee = feesList.get(0);
                    if(Objects.nonNull(fee) && Objects.nonNull(fee.getFee())){
                        minFee = Math.min(minFee, fee.getFee());
                    }
                }

            }
        }
        if (minFee != Long.MAX_VALUE)
            return minFee;
        return null;
    }

    private List<String> getExamAccepted(List<Course> courses) {
        Set<Long> examAccepted = courses.stream().filter(c -> Objects.nonNull(c.getExamsAccepted()))
                .flatMap(c -> c.getExamsAccepted().stream())
                .collect(Collectors.toSet());

        List<Exam> exams = getExamList(examAccepted);
        List<String> retList =
                exams.stream().filter(e -> Objects.nonNull(e.getExamShortName()))
                        .map(e -> e.getExamShortName()).collect(Collectors.toList());

        return retList;
    }

    private List<Exam> getExamList(Set<Long> examIds) {
        List<String> examFields = new ArrayList<>();
        examFields.add("exam_short_name");
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(EXAM_ID, new ArrayList<>(examIds));
        List<Exam> examList = commonMongoRepository
                .findAll(queryObject, Exam.class, examFields, OR);
        return examList;
    }

    private Set<String> getCourseLevel(List<Course> courses) {

        Set<String> courseLevel = courses.stream().filter(c -> Objects.nonNull(c.getCourseLevel()))
                .map(c -> c.getCourseLevel().name())
                .collect(Collectors.toSet());
        return courseLevel;
    }

    private List<Course> getCourses(Long instId){
        List<Course> courses = new ArrayList<>();

        List<String> courseFields = commonMongoRepository.getFieldsByGroup(Course.class, "details");
        // add streams in the field group for course details
        if (!CollectionUtils.isEmpty(courseFields)) {
            courses = commonMongoRepository
                    .getEntitiesByIdAndFields(INSTITUTE_ID, instId, Course.class,
                            courseFields);
        }
        return courses;
    }

    private List<String> getApprovalDetail(List<String> approvals) {
        return null;
    }

    private List<String> getFacilitiesDetail(List<String> facilities) {
        List<String> facList = new ArrayList<>();
        //todo filter from master facility list

        return facilities;
    }

    private String getPlacementDetail(List<Placement> salariesPlacement) {
        String pData = null;
        for(Placement p : salariesPlacement){

        }
        return pData;
    }

    private List<Ranking> getResponseRankingList(
            List<com.paytm.digital.education.explore.database.entity.Ranking> dbRankingList) {
        List<Ranking> rList = new ArrayList<>();
        for(com.paytm.digital.education.explore.database.entity.Ranking dbRanking : dbRankingList){
            Ranking r = new Ranking();
            r.setRank(dbRanking.getRank());
            r.setSource(dbRanking.getSource());
            r.setYear(dbRanking.getYear());
            r.setRating(dbRanking.getRating());
            rList.add(r);
        }
        if(!CollectionUtils.isEmpty(rList))
            return rList;

        return null;
    }
}
