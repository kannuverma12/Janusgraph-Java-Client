package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.database.entity.Board;
import com.paytm.digital.education.database.entity.BoardData;
import com.paytm.digital.education.database.entity.RelevantLink;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolGallery;
import com.paytm.digital.education.database.entity.SchoolOfficialAddress;
import com.paytm.digital.education.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EntityRequiredFieldMissingInDBException;
import com.paytm.digital.education.constant.SchoolConstants;
import com.paytm.digital.education.explore.enums.ClassLevel;
import com.paytm.digital.education.explore.es.model.GeoLocation;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.ClassLevelRow;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ClassLevelTable;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacilityResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacultyDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.GeneralInformation;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ImportantDate;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolGalleryResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftDetailsResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftTable;
import com.paytm.digital.education.explore.response.dto.search.SchoolSearchData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.utility.SchoolUtilService;
import com.paytm.digital.education.utility.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paytm.digital.education.constant.ExploreConstants.CLIENT;
import static com.paytm.digital.education.constant.ExploreConstants.SORT_DISTANCE_FIELD;
import static com.paytm.digital.education.constant.ExploreConstants.TENTATIVE;
import static com.paytm.digital.education.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;
import static com.paytm.digital.education.constant.SchoolConstants.ACTUAL;
import static com.paytm.digital.education.constant.SchoolConstants.BOARD;
import static com.paytm.digital.education.constant.SchoolConstants.BOARD_DATA;
import static com.paytm.digital.education.constant.SchoolConstants.OFFICIAL_WEBSITE_LINK;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.constant.SchoolConstants.SCHOOL_OFFICIAL_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_SCHOOL_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;
import static com.paytm.digital.education.utility.CommonUtils.isNullOrZero;
import static com.paytm.digital.education.utility.FunctionUtils.fetchIfPresentFromNullable;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.elasticsearch.common.geo.parsers.GeoWKTParser.COMMA;

@Service
public class SchoolServiceImpl implements SchoolService {

    private final CommonMongoRepository    commonMongoRepository;
    private final DerivedAttributesHelper  derivedAttributesHelper;
    private final FacilityDataHelper       facilityDataHelper;
    private final CTAHelper                ctaHelper;
    private final SearchServiceImpl        searchService;
    private final SchoolConfig             schoolConfig;
    private final SchoolUtilService        schoolUtilService;
    private final BannerDataHelper         bannerDataHelper;
    private final int                      nearbySchoolsCount;

    public SchoolServiceImpl(
            CommonMongoRepository commonMongoRepository,
            DerivedAttributesHelper derivedAttributesHelper,
            FacilityDataHelper facilityDataHelper,
            CTAHelper ctaHelper,
            SearchServiceImpl searchService,
            SchoolConfig schoolConfig,
            SchoolUtilService schoolUtilService,
            BannerDataHelper bannerDataHelper,
            @Value("${nearby.schools.count}")
            int nearbySchoolsCount) {
        this.commonMongoRepository = commonMongoRepository;
        this.derivedAttributesHelper = derivedAttributesHelper;
        this.facilityDataHelper = facilityDataHelper;
        this.ctaHelper = ctaHelper;
        this.searchService = searchService;
        this.schoolConfig = schoolConfig;
        this.schoolUtilService = schoolUtilService;
        this.bannerDataHelper = bannerDataHelper;
        this.nearbySchoolsCount = nearbySchoolsCount;
    }

    @Override
    @EduCache(cache = "schoolCache", keys = {"schoolId", "client.name", "fieldGroup"})
    public SchoolDetail getSchoolDetails(Long schoolId, Client client, String schoolName,
            List<String> fields, String fieldGroup) {
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
        if (StringUtils.isBlank(school.getOfficialName())) {
            throw new EntityRequiredFieldMissingInDBException(SchoolConstants.SCHOOL, SCHOOL_OFFICIAL_NAME);
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
            if (Objects.isNull(boardData)) {
                throw new EntityRequiredFieldMissingInDBException(BOARD, BOARD_DATA);
            }
            schoolDetail.setFacultyDetail(fetchFacultyDetailsIfPresent(boardData));
            schoolDetail.setFeesDetails(
                    Optional.of(boardData)
                            .map(BoardData::getFeesDetails)
                            .orElse(emptyList())
                            .stream()
                            .filter(schoolUtilService::isFeeDataValid)
                            .distinct()
                            .collect(Collectors.toList())
            );
            List<FacilityResponse> facilityResponseList =
                    facilityDataHelper
                            .mapSchoolFacilitiesToDataObject(
                                    Optional.of(boardData)
                                            .map(BoardData::getSchoolFacilities)
                                            .orElse(emptyList()));
            schoolDetail.setFacilities(facilityResponseList);
            List<ImportantDate> importantDates = Stream.concat(
                    Optional.of(boardData)
                            .map(BoardData::getSchoolAdmissionList)
                            .orElse(emptyList())
                            .stream()
                            .filter(x -> StringUtils.isNotBlank(x.getDateType()))
                            .map(x -> new ImportantDate(x, ACTUAL)),
                    Optional.of(boardData)
                            .map(BoardData::getSchoolAdmissionTentativeList)
                            .orElse(emptyList())
                            .stream()
                            .filter(x -> StringUtils.isNotBlank(x.getDateType()))
                            .map(x -> new ImportantDate(x, TENTATIVE))
            ).collect(Collectors.toList());
            schoolDetail.setImportantDateSections(importantDates);
            schoolDetail.setGallery(
                    ofNullable(school.getGallery())
                            .map(x -> new SchoolGalleryResponse(x, schoolUtilService))
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
            SchoolPaytmKeys schoolPaytmKeys = school.getPaytmKeys();
            schoolDetail
                    .setPid(fetchIfPresentFromNullable(schoolPaytmKeys, SchoolPaytmKeys::getPid));
            schoolDetail.setFormId(
                    fetchIfPresentFromNullable(schoolPaytmKeys, SchoolPaytmKeys::getFormId));
            schoolDetail.setBrochureUrl(boardData.getSchoolBrochureLink());
            List<CTA> ctaList = ctaHelper.buildCTA(schoolDetail, client);
            schoolDetail.setCtaList(ctaList);
            addNearbySchoolsInResponse(schoolDetail, school);

            if (!Client.APP.equals(client)) {
                schoolDetail.setBanners(bannerDataHelper.getBannerData(entityName, client));
            }

            schoolDetail.setClassInfoTable(
                    school.getBoardList()
                            .stream()
                            .filter(schoolUtilService::isClassLevelInfoValid)
                            .map(this::convertBoardToClassLevelTable)
                            .collect(Collectors.toList()));

            schoolDetail.setShiftTables(
                    school.getBoardList()
                            .stream()
                            .map(this::convertBoardToShiftTable)
                            .filter(schoolUtilService::isShiftTableDataCorrect)
                            .collect(Collectors.toList()));
        }

        return schoolDetail;
    }

    private ShiftTable convertBoardToShiftTable(Board board) {
        List<ShiftDetailsResponse> shiftDetailsResponseList = ofNullable(board)
                .map(Board::getData)
                .map(BoardData::getShifts)
                .orElse(emptyList())
                .stream()
                .filter(schoolUtilService::isShiftDetailsValid)
                .distinct()
                .map(x -> new ShiftDetailsResponse(x, schoolConfig))
                .collect(Collectors.toList());

        return ShiftTable.builder()
                .boardType(board.getName())
                .shiftDetailsRows(shiftDetailsResponseList)
                .build();
    }

    private ClassLevelTable convertBoardToClassLevelTable(Board board) {
        return new ClassLevelTable(
                board.getName(),
                schoolUtilService.generateClassInfoTable(board)
                        .stream()
                        .map(this::convertClassLevelTripleToClassLevelRow)
                        .collect(Collectors.toList())
        );
    }

    private ClassLevelRow convertClassLevelTripleToClassLevelRow(
            Triple<ClassType, ClassType, ClassLevel> classLevelTriple) {
        return ClassLevelRow.builder()
                .classFrom(classLevelTriple.getLeft())
                .classTo(classLevelTriple.getMiddle())
                .educationLevel(classLevelTriple.getRight())
                .build();
    }

    private void addNearbySchoolsInResponse(SchoolDetail schoolDetail, School school) {
        SearchRequest searchRequest = buildSearchRequestForSchool(school, true);
        if (Objects.isNull(searchRequest)) {
            return;
        }
        SearchResponse searchResponse = searchService.search(searchRequest, null, null);
        if (Objects.isNull(searchResponse)) {
            return;
        }
        List<SearchBaseData> searchBaseDataList = searchResponse.getResults().getValues();

        if (CollectionUtils.isEmpty(searchBaseDataList)) {
            /*** If there are no nearby schools from location or in same city,
             * search for schools in same state**/
            searchRequest = buildSearchRequestForSchool(school, false);
            if (Objects.isNull(searchRequest)) {
                return;
            }
            searchResponse = searchService.search(searchRequest, null, null);
            searchBaseDataList = searchResponse.getResults().getValues();
            if (CollectionUtils.isEmpty(searchBaseDataList)) {
                return;
            }
        }
        List<SchoolSearchData> schoolSearchDataList =
                searchBaseDataList
                        .stream()
                        .map(x -> (SchoolSearchData) x)
                        .filter(x -> !school.getSchoolId().equals(x.getSchoolId()))
                        .collect(Collectors.toList());
        schoolDetail.setNearbySchools(schoolSearchDataList);
    }



    private SearchRequest buildSearchRequestForSchool(School school, boolean isSameCityRequest) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(SCHOOL);
        searchRequest.setLimit(nearbySchoolsCount + 1);
        GeoLocation geoLocation = buildGeoLocationFromSchool(school);
        Map<String, List<Object>> filters = new HashMap<>();

        if (Objects.isNull(geoLocation) && Objects.isNull(school.getAddress())) {
            return null;
        }

        if (Objects.nonNull(geoLocation)) {
            searchRequest.setGeoLocation(geoLocation);
            LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
            sortOrder.put(SORT_DISTANCE_FIELD, ASC);
            searchRequest.setSortOrder(sortOrder);
        } else if (isSameCityRequest) {
            filters.put(SchoolConstants.SCHOOL_CITY,
                    Arrays.asList(school.getAddress().getCity()));
            filters.put(SchoolConstants.SCHOOL_STATE,
                    Arrays.asList(school.getAddress().getState()));
        } else {
            filters.put(SchoolConstants.SCHOOL_STATE,
                    Arrays.asList(school.getAddress().getState()));
        }
        searchRequest.setFilter(filters);
        return searchRequest;
    }

    private GeoLocation buildGeoLocationFromSchool(School school) {
        String latLon = ofNullable(school.getAddress())
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
            generalInformation.setPhone(boardData.getContactNumberPrimary());
            generalInformation.setStreetAddress(school.getAddress().getStreetAddress());
            generalInformation.setLatLon(school.getAddress().getLatLon());
            generalInformation.setOfficialWebsiteLink(getOfficialWebsiteLinkFromData(boardData));
            generalInformation.setOfficialName(school.getOfficialName());
            generalInformation.setShortName(school.getShortName());
            final String logoUrl =
                    ofNullable(school.getGallery()).map(SchoolGallery::getLogo)
                            .orElse(null);
            generalInformation
                    .setLogo(schoolUtilService.buildLogoFullPathFromRelativePath(logoUrl));
            generalInformation.setCity(school.getAddress().getCity());
            generalInformation.setState(school.getAddress().getState());
            return generalInformation;
        }
        return null;
    }

    private String getOfficialWebsiteLinkFromData(BoardData boardData) {
        return ofNullable(boardData.getRelevantLinks())
                .orElse(emptyList())
                .stream()
                .filter(x -> OFFICIAL_WEBSITE_LINK.equals(x.getRelevantLinkType()))
                .map(RelevantLink::getRelevantLinkUrl)
                .findFirst()
                .orElse(null);
    }

    private List<String> getFieldsByGroupAndCollectioName(String collectionName,
            List<String> fields, String fieldGroup) {
        if (CollectionUtils.isEmpty(fields)) {
            List<String> dbFields = commonMongoRepository
                    .getFieldsByGroupAndCollectioName(collectionName, fieldGroup);
            if (CollectionUtils.isEmpty(dbFields)) {
                throw new BadRequestException(INVALID_FIELD_GROUP,
                        INVALID_FIELD_GROUP.getExternalMessage());
            }
            return dbFields;
        }
        return fields;
    }
}
