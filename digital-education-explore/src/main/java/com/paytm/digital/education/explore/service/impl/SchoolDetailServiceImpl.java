package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.constants.SchoolConstants;
import com.paytm.digital.education.explore.database.entity.Board;
import com.paytm.digital.education.explore.database.entity.BoardData;
import com.paytm.digital.education.explore.database.entity.RelevantLink;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.explore.database.entity.SchoolOfficialAddress;
import com.paytm.digital.education.explore.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.GeoLocation;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacilityResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacultyDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.GeneralInformation;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ImportantDate;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolGalleryResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftDetailsResponse;
import com.paytm.digital.education.explore.response.dto.search.SchoolSearchData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.SchoolDetailsResponseHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;

import com.paytm.digital.education.explore.utility.SchoolUtilService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paytm.digital.education.elasticsearch.enums.DataSortOrder.ASC;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CLIENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SORT_DISTANCE_FIELD;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TENTATIVE;
import static com.paytm.digital.education.explore.constants.SchoolConstants.ACTUAL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.OFFICIAL_WEBSITE_LINK;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.explore.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_SCHOOL_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;
import static com.paytm.digital.education.utility.CommonUtils.isNullOrZero;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.elasticsearch.common.geo.parsers.GeoWKTParser.COMMA;


@Service
public class SchoolDetailServiceImpl implements SchoolService {

    private final CommonMongoRepository    commonMongoRepository;
    private final DerivedAttributesHelper  derivedAttributesHelper;
    private final FacilityDataHelper       facilityDataHelper;
    private final CTAHelper                ctaHelper;
    private final SearchServiceImpl        searchService;
    private final int                      similarSchoolsCount;
    private final SubscriptionDetailHelper subscriptionDetailHelper;
    private final SchoolConfig             schoolConfig;
    private final SchoolUtilService        schoolUtilService;

    public SchoolDetailServiceImpl(
            CommonMongoRepository commonMongoRepository,
            DerivedAttributesHelper derivedAttributesHelper,
            FacilityDataHelper facilityDataHelper,
            CTAHelper ctaHelper,
            SearchServiceImpl searchService,
            @Value("${similar.schools.count}") int similarSchoolsCount,
            SubscriptionDetailHelper subscriptionDetailHelper,
            SchoolConfig schoolConfig,
            SchoolUtilService schoolUtilService) {
        this.commonMongoRepository = commonMongoRepository;
        this.derivedAttributesHelper = derivedAttributesHelper;
        this.facilityDataHelper = facilityDataHelper;
        this.ctaHelper = ctaHelper;
        this.searchService = searchService;
        this.similarSchoolsCount = similarSchoolsCount;
        this.subscriptionDetailHelper = subscriptionDetailHelper;
        this.schoolConfig = schoolConfig;
        this.schoolUtilService = schoolUtilService;
    }

    public List<School> getSchools(List<Long> entityIds, List<String> groupFields) {
        if (CollectionUtils.isEmpty(groupFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        Set<Long> searchIds = new HashSet<>(entityIds);
        List<School> schools =
                commonMongoRepository
                        .getEntityFieldsByValuesIn(SCHOOL_ID, new ArrayList<>(searchIds),
                                School.class,
                                groupFields);

        return schools;
    }

    @Override
    public SchoolDetail getSchoolDetails(Long schoolId, Client client, String schoolName,
            List<String> fields, String fieldGroup, Long userId) {
        List<String> fieldsToBeFetched =
                getFieldsByGroupAndCollectioName(SchoolConstants.SCHOOL, fields,
                                fieldGroup);
        School school =
                commonMongoRepository
                        .getEntityByFields(SCHOOL_ID, schoolId, School.class, fieldsToBeFetched);
        if (Objects.isNull(school)) {
            throw new BadRequestException(NO_ENTITY_FOUND,
                    new Object[] {SchoolConstants.SCHOOL, SCHOOL_ID, schoolId});
        }
        if (!schoolName.equals(CommonUtil.convertNameToUrlDisplayName(school.getOfficialName()))) {
            throw new BadRequestException(INVALID_SCHOOL_NAME,
                    INVALID_SCHOOL_NAME.getExternalMessage());
        }
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setSchoolId(school.getSchoolId());
        List<Board> boards = school.getBoardList();
        if (Objects.nonNull(boards) && boards.size() > 0) {
            BoardData boardData = boards.get(0).getData();
            schoolDetail.setShiftDetailsList(
                    boardData.getShifts().stream().map(x -> new ShiftDetailsResponse(x, schoolConfig))
                            .collect(Collectors.toList()));
            schoolDetail.setFacultyDetail(fetchFacultyDetailsIfPresent(boardData));
            schoolDetail.setFeesDetails(boardData.getFeesDetails());
            List<FacilityResponse> facilityResponseList =
                    facilityDataHelper
                            .mapSchoolFacilitiesToDataObject(boardData.getSchoolFacilities());
            schoolDetail.setFacilities(facilityResponseList);
            List<ImportantDate> importantDates = Stream.concat(
                    boardData.getSchoolAdmissionList().stream()
                            .filter(x -> StringUtils.isNotBlank(x.getDateType()))
                            .map(x -> new ImportantDate(x, ACTUAL)),
                    boardData.getSchoolAdmissionTentativeList().stream()
                            .filter(x -> StringUtils.isNotBlank(x.getDateType()))
                            .map(x -> new ImportantDate(x, TENTATIVE))
            ).collect(Collectors.toList());
            schoolDetail.setImportantDateSections(importantDates);
            schoolDetail.setGallery(
                    Optional.ofNullable(school.getGallery()).map(x -> new SchoolGalleryResponse(x, schoolUtilService))
                            .orElse(null));
            String entityName = SCHOOL.name().toLowerCase();
            schoolDetail.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(
                            Maps.newHashMap(ImmutableMap.of(
                                    entityName, school,
                                    CLIENT, client
                            )),
                            entityName,
                            client));
            schoolDetail.setGeneralInformation(collectGeneralInformationFromSchool(school));
            schoolDetail.setStreams(boardData.getStreams());
            SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
            Optional<SchoolPaytmKeys> schoolPaytmKeys = Optional.ofNullable(school.getPaytmKeys());
            schoolDetail.setPid(schoolPaytmKeys.map(SchoolPaytmKeys::getPid).orElse(null));
            schoolDetail.setFormId(schoolPaytmKeys.map(SchoolPaytmKeys::getFormId).orElse(null));
            schoolDetail.setBrochureUrl(boardData.getSchoolBrochureLink());
            List<CTA> ctaList = ctaHelper.buildCTA(schoolDetail, client);
            schoolDetail.setCtaList(ctaList);
            addSimilarSchoolsInResponse(schoolDetail, school);

            if (Objects.nonNull(userId) && userId > 0) {
                updateShortList(schoolDetail, SCHOOL, userId);
            }
        }
        return schoolDetail;
    }

    private void updateShortList(SchoolDetail schoolDetail, EducationEntity educationEntity,
            Long userId) {
        List<Long> schoolIds = new ArrayList<>();
        schoolIds.add(schoolDetail.getSchoolId());

        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(educationEntity, userId, schoolIds);

        schoolDetail.setShortlisted(!CollectionUtils.isEmpty(subscribedEntities));
    }

    private void addSimilarSchoolsInResponse(SchoolDetail schoolDetail, School school) {
        SearchRequest searchRequest = buildSearchRequestForSchool(school);
        if (Objects.isNull(searchRequest)) {
            return;
        }
        SearchResponse searchResponse = searchService.search(searchRequest, null, null);
        List<SearchBaseData> searchBaseDataList = searchResponse.getResults().getValues();
        if (CollectionUtils.isEmpty(searchBaseDataList)) {
            return;
        }
        List<SchoolSearchData> schoolSearchDataList =
                searchBaseDataList
                        .stream()
                        .map(x -> (SchoolSearchData)  x)
                        .filter(x -> !school.getSchoolId().equals(x.getSchoolId()))
                        .collect(Collectors.toList());
        schoolDetail.setSimilarSchools(schoolSearchDataList);
    }

    private SearchRequest buildSearchRequestForSchool(School school) {
        GeoLocation geoLocation = buildGeoLocationFromSchool(school);
        if (Objects.isNull(geoLocation)) {
            return null;
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setGeoLocation(geoLocation);
        searchRequest.setEntity(SCHOOL);
        searchRequest.setFilter(Collections.emptyMap());
        searchRequest.setLimit(similarSchoolsCount + 1);
        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(SORT_DISTANCE_FIELD, ASC);
        searchRequest.setSortOrder(sortOrder);
        return searchRequest;
    }

    private GeoLocation buildGeoLocationFromSchool(School school) {
        String latLon = Optional.ofNullable(school.getAddress())
                .map(SchoolOfficialAddress::getLatLon)
                .orElse(EMPTY);
        String[] latLonArray = latLon.split(COMMA);
        if (latLonArray.length != 2) {
            return null;
        }
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setLat(Double.parseDouble(latLonArray[0]));
        geoLocation.setLon(Double.parseDouble(latLonArray[1]));
        return geoLocation;
    }

    private FacultyDetail fetchFacultyDetailsIfPresent(BoardData boardData) {
        if (Objects.isNull(boardData)) {
            return null;
        }
        Integer numberOfTeachers = boardData.getNoOfTeachers();
        Integer numberOfTrainedTeachers = boardData.getNoOfTrainedTeachers();
        Integer numberOfUntrainedTeachers = boardData.getNoOfUntrainedTeachers();
        String studentRatio = boardData.getStudentRatio();
        if (isNullOrZero(numberOfTeachers)
            || StringUtils.isBlank(studentRatio)) {
            return null;
        }
        FacultyDetail facultyDetail = new FacultyDetail(numberOfTeachers, studentRatio);
        facultyDetail.setNoOfTrainedTeachers(numberOfTrainedTeachers);
        facultyDetail.setNoOfUntrainedTeachers(numberOfUntrainedTeachers);
        facultyDetail.setTotalTeachersImageUrl(schoolConfig.getTotalTeachersImageURL());
        facultyDetail.setStudentToTeacherRatioImageUrl(schoolConfig.getStudentToTeachersImageURL());
        return facultyDetail;
    }

    private GeneralInformation collectGeneralInformationFromSchool(School school) {
        List<Board> boards = school.getBoardList();
        if (boards.size() > 0) {
            BoardData boardData = boards.get(0).getData();
            GeneralInformation generalInformation = new GeneralInformation();
            generalInformation.setEmail(boardData.getEmail());
            generalInformation.setPhone(school.getPhone());
            generalInformation.setStreetAddress(school.getAddress().getStreetAddress());
            generalInformation.setLatLon(school.getAddress().getLatLon());
            generalInformation.setOfficialWebsiteLink(getOfficialWebsiteLinkFromData(boardData));
            generalInformation.setOfficialName(school.getOfficialName());
            generalInformation.setShortName(school.getShortName());
            final String logoUrl =
                    Optional.ofNullable(school.getGallery()).map(SchoolGallery::getLogo).orElse(null);
            generalInformation.setLogo(schoolUtilService.buildLogoFullPathFromRelativePath(logoUrl));
            generalInformation.setCity(school.getAddress().getCity());
            generalInformation.setState(school.getAddress().getState());
            return generalInformation;
        }
        return null;
    }

    private String getOfficialWebsiteLinkFromData(BoardData boardData) {
        return boardData.getRelevantLinks()
                .stream()
                .filter(x -> OFFICIAL_WEBSITE_LINK.equals(x.getRelevantLinkType()))
                .map(RelevantLink::getRelevantLinkUrl)
                .findFirst()
                .orElse(null);
    }

    private List<String> getFieldsByGroupAndCollectioName(String collectionName, List<String> fields,
            String fieldGroup) {
        if (CollectionUtils.isEmpty(fields)) {
            List<String> dbFields = commonMongoRepository.getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
            if (CollectionUtils.isEmpty(dbFields)) {
                throw new BadRequestException(INVALID_FIELD_GROUP,
                        INVALID_FIELD_GROUP.getExternalMessage());
            }
            return dbFields;
        }
        return fields;
    }
}
