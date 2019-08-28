package com.paytm.digital.education.explore.service.impl;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.NE;
import static com.paytm.digital.education.enums.Number.ONE;
import static com.paytm.digital.education.explore.constants.CompareConstants.CAREERS360;
import static com.paytm.digital.education.explore.constants.CompareConstants.NIRF;
import static com.paytm.digital.education.explore.constants.CompareConstants.UNIVERSITIES;
import static com.paytm.digital.education.constant.ExploreConstants.COLLEGES_PER_STREAM;
import static com.paytm.digital.education.constant.ExploreConstants.EMPTY_SQUARE_BRACKETS;
import static com.paytm.digital.education.constant.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_ADDRESS;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTION_STATE;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTION_CITY;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.IN_OPERATOR;
import static com.paytm.digital.education.constant.ExploreConstants.MAX_STREAMS;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.constant.ExploreConstants.SIMILAR_COLLEGES;
import static com.paytm.digital.education.constant.ExploreConstants.SIMILAR_COLLEGE_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.STREAMS;
import static com.paytm.digital.education.constant.ExploreConstants.TOTAL_SIMILAR_COLLEGE;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.database.entity.Course;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.Ranking;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.database.repository.InstituteRepository;
import com.paytm.digital.education.explore.enums.CourseStream;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@AllArgsConstructor
public class SimilarInstituteServiceImpl {

    private CommonMongoRepository commonMongoRepository;
    private InstituteRepository   instituteRepository;
    private WidgetsDataHelper     widgetsDataHelper;

    private static List<String> projectionFields =
            Arrays.asList(INSTITUTE_ID, OFFICIAL_NAME, GALLERY_LOGO, OFFICIAL_ADDRESS,
                    INSTITUTION_CITY, INSTITUTION_STATE);

    @Cacheable(value = "similar_institutes", key = "'similar_'+#institute.instituteId")
    public List<Widget> getSimilarInstitutes(Institute institute) {
        if (Objects.nonNull(institute)) {
            try {
                Map<String, Map<String, Ranking>> rankingDataMap =
                        getRankingMap(institute.getRankings());
                if (!CollectionUtils.isEmpty(rankingDataMap) && rankingDataMap.containsKey(NIRF)) {
                    Widget widget =
                            getSimilarCollegesByNirfRanking(institute, rankingDataMap.get(NIRF));
                    if (Objects.nonNull(widget)) {
                        return Arrays.asList(widget);
                    }
                }
                Widget widget = getSimilarCollegesByLocation(institute);
                if (Objects.nonNull(widget)) {
                    return Arrays.asList(widget);
                }
            } catch (Exception ex) {
                log.error("Error caught while getting similar colleges for the instituteId : "
                        + institute.getInstituteId(), ex);
            }
            return widgetsDataHelper
                    .getWidgets(INSTITUTE.name().toLowerCase(), institute.getInstituteId());
        }
        return null;
    }


    private Widget getSimilarCollegesByNirfRanking(Institute institute,
            Map<String, Ranking> nirfRanking) {
        //If Only one stream is present, show similar colleges based on the streams present
        Set<String> streams = getRankingStreams(nirfRanking);
        if (Objects.nonNull(streams) && streams.size() == ONE.getValue()) {
            return getSimilarCollegesByStreams(institute, NIRF, TOTAL_SIMILAR_COLLEGE, streams,
                    nirfRanking);
        }
        //otherwise show similar colleges based on the OVERALL rankings
        if (nirfRanking.containsKey(OVERALL_RANKING) || nirfRanking.containsKey(UNIVERSITIES)) {
            List<Institute> instituteList = getByOverAllRankings();
            int mainRank = nirfRanking.containsKey(OVERALL_RANKING)
                    ?
                    nirfRanking.get(OVERALL_RANKING).getRank() :
                    nirfRanking.get(UNIVERSITIES).getRank();
            List<Institute> resultList =
                    getTopSimilarInstitutes(institute, instituteList, NIRF, null, true, mainRank,
                            TOTAL_SIMILAR_COLLEGE);
            return buildWidgetResponse(resultList);
        }

        //If overall rank is not present, show institutes of maximum 2 streams in the priority order of the streams
        return getSimilarCollegesByStreams(institute, NIRF, COLLEGES_PER_STREAM,
                selectTopTwoStreams(streams), nirfRanking);
    }

    private Widget getSimilarCollegesByStreams(Institute institute, String rankingSource,
            int limitPerStream,
            Collection<String> rankingStreams, Map<String, Ranking> rankingMap) {
        List<Institute> instituteResultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rankingStreams)) {
            for (String stream : rankingStreams) {
                int mainRank = rankingMap.get(stream).getRank();
                List<Institute> instituteList =
                        instituteRepository.findAllBySourceAndStream(rankingSource, stream);
                if (!CollectionUtils.isEmpty(instituteList)) {
                    Collections.sort(instituteList, (institute1, institute2) -> {
                        int rank1 = getRankingBySourceAndStream(institute1.getRankings(),
                                rankingSource, stream, false).getRank();
                        int rank2 = getRankingBySourceAndStream(institute2.getRankings(),
                                rankingSource, stream, false).getRank();
                        return rank1 - rank2;
                    });
                }
                instituteList = getTopSimilarInstitutes(institute, instituteList,
                        rankingSource, stream, false, mainRank, limitPerStream);
                if (!CollectionUtils.isEmpty(instituteList)) {
                    instituteResultList.addAll(instituteList);
                }
            }
        }
        return buildWidgetResponse(instituteResultList);
    }

    private Widget buildWidgetResponse(List<Institute> instituteList) {
        if (!CollectionUtils.isEmpty(instituteList)) {
            Widget widget = new Widget();
            List<WidgetData> widgetDataList = new ArrayList<>();
            for (Institute institute : instituteList) {
                WidgetData widgetData = new WidgetData();
                widgetData.setEntityId(institute.getInstituteId());
                widgetData.setOfficialName(institute.getOfficialName());
                widgetData.setUrlDisplayKey(
                        CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
                if (Objects.nonNull(institute.getGallery())) {
                    widgetData.setLogoUrl(
                            CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                }
                widgetData.setOfficialAddress(CommonUtil
                        .getOfficialAddress(institute.getInstitutionState(),
                                institute.getInstitutionCity(), institute.getPhone(),
                                institute.getUrl(), institute.getOfficialAddress()));
                widgetDataList.add(widgetData);
            }
            widget.setData(widgetDataList);
            widget.setEntity(INSTITUTE.name());
            widget.setLabel(SIMILAR_COLLEGES);
            return widget;
        }
        return null;
    }

    private int getNextGreaterRankIndex(List<Institute> instituteList, String source, String stream,
            boolean isOverallRanking, int mainInstituteRank) {
        int nextGreaterIndex = -1;
        if (CollectionUtils.isEmpty(instituteList)) {
            return nextGreaterIndex;
        }
        int low = 0;
        int high = instituteList.size();
        while (low <= high) {
            int mid = (low + high) / 2;
            Ranking rankingObj =
                    getRankingBySourceAndStream(instituteList.get(mid).getRankings(), source,
                            stream, isOverallRanking);
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
            Ranking ranking1 =
                    getRankingBySourceAndStream(institute1.getRankings(), NIRF, null, true);
            Ranking ranking2 =
                    getRankingBySourceAndStream(institute2.getRankings(), NIRF, null, true);

            if (Objects.nonNull(ranking1) && Objects.nonNull(ranking2)) {
                return ranking1.getRank() - ranking2.getRank();
            } else if (Objects.isNull(ranking1)) {
                return 1;
            }
            return 0;
        });

        return instituteList;
    }

    private Widget getSimilarCollegesByLocation(Institute institute) {
        Set<String> streams = getCourseStreamForInstitute(institute.getInstituteId());
        Map<String, Object> instituteQueryMap =
                getInstituteQueryMapForLocationAndStreams(institute.getInstitutionState(),
                        institute.getInstitutionCity(), selectTopTwoStreams(streams));
        List<Institute> instituteList = commonMongoRepository
                .findAll(instituteQueryMap, Institute.class, projectionFields, AND);
        if (!CollectionUtils.isEmpty(instituteList)
                && instituteList.size() > TOTAL_SIMILAR_COLLEGE) {
            instituteList = instituteList.stream()
                    .filter(institute1 -> !institute1.getInstituteId()
                            .equals(institute.getInstituteId()))
                    .limit(TOTAL_SIMILAR_COLLEGE)
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(instituteList)
                || instituteList.size() < TOTAL_SIMILAR_COLLEGE) {
            instituteQueryMap.remove(INSTITUTION_CITY);
            instituteList = commonMongoRepository
                    .findAll(instituteQueryMap, Institute.class, projectionFields, AND);
            if (!CollectionUtils.isEmpty(instituteList)) {
                instituteList = instituteList.stream()
                        .filter(institute1 -> !institute1.getInstituteId()
                                .equals(institute.getInstituteId()))
                        .limit(TOTAL_SIMILAR_COLLEGE)
                        .collect(Collectors.toList());
            }
        }
        if (!CollectionUtils.isEmpty(instituteList)
                && instituteList.size() < TOTAL_SIMILAR_COLLEGE) {
            return null;
        }
        return buildWidgetResponse(instituteList);
    }

    private Map<String, Object> getInstituteQueryMapForLocationAndStreams(String instituteState,
            String instituteCity, Collection<String> streams) {
        List<Long> instituteIds = getInstituteIdsByStreams(IN_OPERATOR, streams);
        Map<String, Object> instituteIdMap = new HashMap<>();
        instituteIdMap.put(IN_OPERATOR, instituteIds);
        Map<String, Object> instituteQueryMap = new HashMap<>();
        instituteQueryMap.put(INSTITUTION_STATE, instituteState);
        instituteQueryMap.put(INSTITUTION_CITY, instituteCity);
        instituteQueryMap.put(INSTITUTE_ID, instituteIdMap);
        return instituteQueryMap;
    }

    private Ranking getRankingBySourceAndStream(List<Ranking> rankings, String source,
            String rankingStream,
            boolean isOverall) {
        if (!CollectionUtils.isEmpty(rankings)) {
            Optional<Ranking> rankingOptional = null;
            if (isOverall) {
                rankingOptional = rankings.stream()
                        .filter(ranking -> source.equalsIgnoreCase(ranking.getSource())
                                && StringUtils.isNotBlank(ranking.getRankingType())
                                && (ranking.getRankingType().equalsIgnoreCase(OVERALL_RANKING)
                                || ranking.getRankingType().equalsIgnoreCase(UNIVERSITIES))
                                && Objects.nonNull(ranking.getRank())).findFirst();
            } else {
                rankingOptional = rankings.stream()
                        .filter(ranking -> source.equalsIgnoreCase(ranking.getSource())
                                && StringUtils.isNotBlank(ranking.getStream())
                                && (ranking.getStream().equalsIgnoreCase(rankingStream))
                                && Objects.nonNull(ranking.getRank())).findFirst();
            }
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
                if (StringUtils.isNotBlank(ranking.getSource()) && Objects
                        .nonNull(ranking.getRank())) {
                    if (StringUtils.isNotBlank(ranking.getRankingType()) && OVERALL_RANKING
                            .equalsIgnoreCase(ranking.getRankingType())) {
                        rankingDataMap.get(ranking.getSource()).put(OVERALL_RANKING, ranking);
                    } else if (StringUtils.isNotBlank(ranking.getRankingType()) && UNIVERSITIES
                            .equalsIgnoreCase(ranking.getRankingType())) {
                        rankingDataMap.get(ranking.getSource()).put(UNIVERSITIES, ranking);
                    } else if (StringUtils.isNotBlank(ranking.getStream())) {
                        rankingDataMap.get(ranking.getSource()).put(ranking.getStream(), ranking);
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

    private Set<String> getRankingStreams(Map<String, Ranking> rankingMap) {
        if (!CollectionUtils.isEmpty(rankingMap)) {
            return rankingMap.keySet().stream()
                    .filter(rankStream -> !rankStream.equalsIgnoreCase(UNIVERSITIES) && !rankStream
                            .equalsIgnoreCase(OVERALL_RANKING)).collect(Collectors.toSet());
        }
        return null;
    }

    private Set<String> getCourseStreamForInstitute(Long instituteId) {
        List<String> courseFields = Arrays.asList(STREAMS);
        List<Course> courses = commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, Arrays.asList(instituteId), Course.class,
                        courseFields);
        Set<String> courseStreams = courses.stream().filter(c -> Objects.nonNull(c.getStreams()))
                .flatMap(course -> course.getStreams().stream()).collect(Collectors.toSet());
        return courseStreams;
    }

    private List<String> selectTopTwoStreams(Set<String> streams) {
        List<String> instituteStreams = new ArrayList<>(streams);
        Collections.sort(instituteStreams, (st1, st2) -> {
            CourseStream stream1 = CourseStream.valueOf(st1);
            CourseStream stream2 = CourseStream.valueOf(st2);
            return stream1.getValue() - stream2.getValue();
        });
        return instituteStreams.stream().limit(MAX_STREAMS).collect(Collectors.toList());
    }

    private List<Institute> getTopSimilarInstitutes(Institute institute,
            List<Institute> instituteList,
            String source, String stream, boolean isOverAllRank, int mainRank, int noOfInstitutes) {
        List<Institute> resultList = new ArrayList<>();
        int nextGreaterRankIndex =
                getNextGreaterRankIndex(instituteList, source, stream, isOverAllRank, mainRank);
        int noOfLowerRankInstitutes = noOfInstitutes / 2;
        int noOfHigherRankInstitutes = noOfInstitutes - noOfLowerRankInstitutes;
        if (nextGreaterRankIndex != -1 && nextGreaterRankIndex < instituteList.size()) {
            for (int i = nextGreaterRankIndex;
                 resultList.size() < noOfHigherRankInstitutes && i < instituteList.size(); i++) {
                if (!instituteList.get(i).getInstituteId().equals(institute.getInstituteId())) {
                    resultList.add(instituteList.get(i));
                }
            }
            for (int i = nextGreaterRankIndex - 1;
                 i > 0 && resultList.size() < noOfInstitutes; i--) {
                if (!instituteList.get(i).getInstituteId().equals(institute.getInstituteId())) {
                    resultList.add(instituteList.get(i));
                }
            }
            //If lower rank institutes not found fill all higher rank institutes
            if (resultList.size() < noOfInstitutes) {
                resultList.clear();
                for (int i = nextGreaterRankIndex;
                     resultList.size() < noOfInstitutes && i < instituteList.size(); i++) {
                    if (!instituteList.get(i).getInstituteId().equals(institute.getInstituteId())) {
                        resultList.add(instituteList.get(i));
                    }
                }
            }
        } else {
            //if higher rank colleges not found, get same or lower rank colleges
            for (int i = instituteList.size() - 1;
                 i > 0 && resultList.size() < noOfInstitutes; i--) {
                if (!instituteList.get(i).getInstituteId().equals(institute.getInstituteId())) {
                    resultList.add(instituteList.get(i));
                }
            }
        }
        return resultList;
    }

}
