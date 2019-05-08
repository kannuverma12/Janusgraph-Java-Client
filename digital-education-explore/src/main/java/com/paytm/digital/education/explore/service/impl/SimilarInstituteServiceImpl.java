package com.paytm.digital.education.explore.service.impl;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.paytm.digital.education.enums.Number.ONE;
import static com.paytm.digital.education.explore.constants.CompareConstants.CAREERS360;
import static com.paytm.digital.education.explore.constants.CompareConstants.NIRF;
import static com.paytm.digital.education.explore.constants.CompareConstants.UNIVERSITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EMPTY_SQUARE_BRACKETS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EQ_OPERATOR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTION_CITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTION_STATE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IN_OPERATOR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAX_STREAMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NO_OF_HIGHER_RANK_COLLEGE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SIMILAR_COLLEGES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SIMILAR_COLLEGE_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TOTAL_SIMILAR_COLLEGE;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Ranking;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.database.repository.InstituteRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.InstituteStream;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SimilarInstituteServiceImpl {

    private CommonMongoRepository commonMongoRepository;
    private InstituteRepository   instituteRepository;

    private static List<String> projectionFields = Arrays.asList(INSTITUTE_ID, OFFICIAL_NAME, GALLERY_LOGO);

    @Cacheable(value = "similar_institutes", key = "'similar_'+#institute.instituteId")
    public Widget getSimilarInstitutes(Institute institute) {
        if (Objects.nonNull(institute)) {
            List<String> courseFields = Arrays.asList(STREAMS);
            List<Course> courses = commonMongoRepository
                    .getEntityFieldsByValuesIn(INSTITUTE_ID, Arrays.asList(institute.getInstituteId()), Course.class,
                            courseFields);
            Set<String> courseStreams = courses.stream().filter(c -> Objects.nonNull(c.getStreams()))
                    .flatMap(course -> course.getStreams().stream()).collect(Collectors.toSet());

            Map<String, Map<String, Ranking>> rankingDataMap = getRankingMap(institute.getRankings());
            if (!CollectionUtils.isEmpty(rankingDataMap)) {
                if (rankingDataMap.containsKey(NIRF)) {
                    return getSimilarCollegesByNirfRanking(institute, courseStreams, rankingDataMap.get(NIRF));
                }
            }
            return getSimilarCollegesByLocation(institute, courseStreams);
        }
        return null;
    }


    private Widget getSimilarCollegesByNirfRanking(Institute institute, Set<String> streams,
            Map<String, Ranking> nirfRanking) {
        //If Only one stream is present, show similar colleges based on the streams present
        //db.course.distinct("institute_id",{"streams":{$exists:true,$ne:[], $eq:["ENGINEERING_AND_ARCHITECTURE"]}})
        if (Objects.nonNull(streams) && streams.size() == ONE.getValue()) {
            return getSimilarCollegesByStream(EQ_OPERATOR, streams);
        }
        //otherwise show similar colleges based on the OVERALL rankings
        //db.institute.find({"rankings":{"$exists":true,"$ne":[],
        // "$elemMatch":{"source":"NIRF","ranking_type":"OVERALL"}}})
        if (nirfRanking.containsKey(OVERALL_RANKING) || nirfRanking.containsKey(UNIVERSITIES)) {
            List<Institute> resultList = new ArrayList<>();
            List<Institute> instituteList = getByOverAllRankings();
            int mainRank = nirfRanking.containsKey(OVERALL_RANKING) ? nirfRanking.get(OVERALL_RANKING).getRank()
                    : nirfRanking.get(UNIVERSITIES).getRank();
            int nextGreaterRankIndex = getNextGreaterRankIndex(instituteList, mainRank);
            if (nextGreaterRankIndex != -1 && nextGreaterRankIndex < instituteList.size()) {
                for (int i = nextGreaterRankIndex;
                     resultList.size() < NO_OF_HIGHER_RANK_COLLEGE && i < instituteList.size(); i++) {
                    if (instituteList.get(i).getInstituteId() != institute.getInstituteId()) {
                        resultList.add(instituteList.get(i));
                    }
                }
                for (int i = nextGreaterRankIndex - 1; i > 0 && resultList.size() < TOTAL_SIMILAR_COLLEGE; i--) {
                    if (instituteList.get(i).getInstituteId() != institute.getInstituteId()) {
                        resultList.add(instituteList.get(i));
                    }
                }
                //If lower rank institutes not found fill all higher rank institutes
                if (resultList.size() < TOTAL_SIMILAR_COLLEGE) {
                    resultList.clear();
                    for (int i = nextGreaterRankIndex;
                         resultList.size() < TOTAL_SIMILAR_COLLEGE && i < instituteList.size(); i++) {
                        if (instituteList.get(i).getInstituteId() != institute.getInstituteId()) {
                            resultList.add(instituteList.get(i));
                        }
                    }
                }
            } else {
                //if higher rank colleges not found, get same or lower rank colleges
                for (int i = instituteList.size() - 1; i > 0 && resultList.size() < TOTAL_SIMILAR_COLLEGE; i--) {
                    if (instituteList.get(i).getInstituteId() != institute.getInstituteId()) {
                        resultList.add(instituteList.get(i));
                    }
                }
            }
            return builWidgetResponse(resultList);
        }

        //If overall rank is not present, show institutes of maximum 2 streams in the priority order of the streams
        List<String> instituteStreams = new ArrayList<>(streams);
        Collections.sort(instituteStreams, (st1, st2) -> {
            InstituteStream stream1 = InstituteStream.valueOf(st1);
            InstituteStream stream2 = InstituteStream.valueOf(st2);
            return stream1.getValue() - stream2.getValue();
        });
        instituteStreams = instituteStreams.stream().limit(MAX_STREAMS).collect(Collectors.toList());
        return getSimilarCollegesByStream(IN_OPERATOR, instituteStreams);
    }

    private Widget builWidgetResponse(List<Institute> instituteList) {
        if (!CollectionUtils.isEmpty(instituteList)) {
            Widget widget = new Widget();
            List<WidgetData> widgetDataList = new ArrayList<>();
            for (Institute institute : instituteList) {
                WidgetData widgetData = new WidgetData();
                widgetData.setEntityId(institute.getInstituteId());
                widgetData.setOfficialName(institute.getOfficialName());
                if (Objects.nonNull(institute.getGallery())) {
                    widgetData.setLogoUrl(CommonUtil.getLogoLink(institute.getGallery().getLogo()));
                }
                widgetDataList.add(widgetData);
            }
            widget.setData(widgetDataList);
            widget.setEntity(EducationEntity.INSTITUTE.name());
            widget.setLabel(SIMILAR_COLLEGES);
            return widget;
        }
        return null;
    }

    private int getNextGreaterRankIndex(List<Institute> instituteList, int mainInstituteRank) {
        int nextGreaterIndex = -1;
        if (CollectionUtils.isEmpty(instituteList)) {
            return nextGreaterIndex;
        }
        int low = 0;
        int high = instituteList.size();
        while (low <= high) {
            int mid = (low + high) / 2;
            Ranking rankingObj = getOverallRankingBySource(instituteList.get(mid).getRankings(), NIRF);
            int instRank = rankingObj.getRank();
            if (instRank == mainInstituteRank) {
                nextGreaterIndex = mid + 1;
                break;
            } else if (instRank < mainInstituteRank) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return nextGreaterIndex;
    }

    @Cacheable(value = SIMILAR_COLLEGE_NAMESPACE, key = "nirf_overall_ranking")
    public List<Institute> getByOverAllRankings() {
        List<Institute> instituteList = instituteRepository.findAllByNIRFOverallRanking();
        Collections.sort(instituteList, (institute1, institute2) -> {
            if (CollectionUtils.isEmpty(institute1.getRankings())) {
                return 1;
            }
            if (CollectionUtils.isEmpty(institute2.getRankings())) {
                return -1;
            }
            Ranking ranking1 = getOverallRankingBySource(institute1.getRankings(), NIRF);
            Ranking ranking2 = getOverallRankingBySource(institute2.getRankings(), NIRF);

            if (Objects.nonNull(ranking1) && Objects.nonNull(ranking2)) {
                return ranking1.getRank() - ranking2.getRank();
            } else if (Objects.isNull(ranking1)) {
                return 1;
            }
            return 0;
        });

        return instituteList;
    }

    private Widget getSimilarCollegesByLocation(Institute institute, Set<String> streams) {
        Map<String, Object> instituteQueryMap = getInstituteQueryMapForStateAndStreams(institute.getInstitutionState(), streams);
        List<Institute> instituteList =
                commonMongoRepository.findAll(instituteQueryMap, Institute.class, projectionFields, AND);
        if (instituteList.size() > TOTAL_SIMILAR_COLLEGE) {
            Collections.sort(instituteList, (inst1, inst2) -> {
                if (CollectionUtils.isEmpty(inst1.getRankings())) {
                    return 1;
                }
                if (CollectionUtils.isEmpty(inst2.getRankings())) {
                    return -1;
                }
                Ranking ranking1 = getC360Ranking(inst1.getRankings());
                Ranking ranking2 = getC360Ranking(inst2.getRankings());
                if (Objects.nonNull(ranking1) && Objects.nonNull(ranking2)) {
                    return ranking1.getRank() - ranking2.getRank();
                } else if (Objects.isNull(ranking1)) {
                    return 1;
                }
                return -1;
            });
            instituteList = instituteList.stream()
                    .filter(institute1 -> institute1.getInstituteId() != institute.getInstituteId()
                            && Objects.nonNull(institute.getRankings())).limit(TOTAL_SIMILAR_COLLEGE)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(instituteList) || instituteList.size() < TOTAL_SIMILAR_COLLEGE) {
                //instituteQueryMap.remove(INSTITUTION_STATE);
                instituteQueryMap.put(INSTITUTION_CITY, institute.getInstitutionCity());
                instituteList =
                        commonMongoRepository.findAll(instituteQueryMap, Institute.class, projectionFields, AND);
                instituteList = instituteList.stream()
                        .filter(institute1 -> institute1.getInstituteId() != institute.getInstituteId())
                        .limit(TOTAL_SIMILAR_COLLEGE)
                        .collect(Collectors.toList());
            }
        }
        return builWidgetResponse(instituteList);
    }

    private Map<String, Object> getInstituteQueryMapForStateAndStreams(String instituteState, Set<String> streams) {
        List<Long> instituteIds = getInstituteIdsByStreams(IN_OPERATOR, streams);
        Map<String, Object> instituteIdMap = new HashMap<>();
        instituteIdMap.put(IN_OPERATOR, instituteIds);
        Map<String, Object> instituteQueryMap = new HashMap<>();
        instituteQueryMap.put(INSTITUTION_STATE, instituteState);
        instituteQueryMap.put(INSTITUTE_ID, instituteIdMap);
        return
    }

    private Ranking getC360Ranking(List<Ranking> rankings) {
        if (!CollectionUtils.isEmpty(rankings)) {
            Optional<Ranking> rankingOptional = rankings.stream()
                    .filter(ranking -> CAREERS360.equalsIgnoreCase(ranking.getSource())
                            && StringUtils.isNotBlank(ranking.getRankingType())
                            && (ranking.getRankingType().equalsIgnoreCase(OVERALL_RANKING)
                            || ranking.getRankingType().equalsIgnoreCase(UNIVERSITIES))
                            && Objects.nonNull(ranking.getRank()) && ranking.getRank() > 100).findFirst();
            if (Objects.nonNull(rankingOptional) && !rankingOptional.isPresent()) {
                return rankingOptional.get();
            }
        }
        return null;
    }

    private Ranking getOverallRankingBySource(List<Ranking> rankings, String source) {
        if (!CollectionUtils.isEmpty(rankings)) {
            Optional<Ranking> rankingOptional = rankings.stream()
                    .filter(ranking -> source.equalsIgnoreCase(ranking.getSource())
                            && StringUtils.isNotBlank(ranking.getRankingType())
                            && (ranking.getRankingType().equalsIgnoreCase(OVERALL_RANKING)
                            || ranking.getRankingType().equalsIgnoreCase(UNIVERSITIES))
                            && Objects.nonNull(ranking.getRank())).findFirst();
            if (rankingOptional.isPresent()) {
                return rankingOptional.get();
            }
        }
        return null;
    }

    private Map<String, Map<String, Ranking>> getRankingMap(List<Ranking> rankingList) {
        Map<String, Map<String, Ranking>> rankingDataMap = new HashMap<>();
        rankingDataMap.put(NIRF, new HashMap<>());
        rankingDataMap.put(CAREERS360, new HashMap<>());
        if (!CollectionUtils.isEmpty(rankingList)) {
            for (Ranking ranking : rankingList) {
                if (StringUtils.isNotBlank(ranking.getSource()) && Objects.nonNull(ranking.getRank())) {
                    if (StringUtils.isNotBlank(ranking.getRankingType()) && OVERALL_RANKING
                            .equalsIgnoreCase(ranking.getRankingType())) {
                        rankingDataMap.get(ranking.getSource()).put(OVERALL_RANKING, ranking);
                    } else if (StringUtils.isNotBlank(ranking.getRankingType()) && UNIVERSITIES
                            .equalsIgnoreCase(ranking.getRankingType())) {
                        rankingDataMap.get(ranking.getSource()).put(UNIVERSITIES, ranking);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(rankingDataMap.get(NIRF))) {
            rankingDataMap.remove(NIRF);
        }
        if (CollectionUtils.isEmpty(rankingDataMap.get(CAREERS360))) {
            rankingDataMap.remove(CAREERS360);
        }
        return rankingDataMap;
    }

    private List<Long> getInstituteIdsByStreams(String streamOperator, Collection<String> streams) {
        Map<String, Object> streamQueryMap = new HashMap<>();
        streamQueryMap.put(EXISTS, true);
        streamQueryMap.put(NE, EMPTY_SQUARE_BRACKETS);
        streamQueryMap.put(streamOperator, streams);
        Map<String, Object> courseQueryFields = new HashMap<>();
        courseQueryFields.put(STREAMS, streamQueryMap);
        List<Long> instituteIds = commonMongoRepository
                .findAllDistinctValues(courseQueryFields, Course.class, INSTITUTE_ID, Long.class);
        return instituteIds;
    }

    private Widget getSimilarCollegesByStream(String streamOperator, Collection<String> streams) {
        List<Long> instituteIds = getInstituteIdsByStreams(streamOperator, streams);
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, OFFICIAL_NAME, GALLERY_LOGO);
        List<Institute> instituteList = commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, instituteIds, Institute.class, instituteFields);
        instituteList = instituteList.stream().limit(4).collect(Collectors.toList());
        return builWidgetResponse(instituteList);
    }

}
