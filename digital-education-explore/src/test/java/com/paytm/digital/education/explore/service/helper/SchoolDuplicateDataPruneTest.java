package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.entity.Board;
import com.paytm.digital.education.database.entity.BoardData;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.database.entity.SchoolOfficialAddress;
import com.paytm.digital.education.database.entity.ShiftDetails;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftTable;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.impl.SchoolServiceImpl;
import com.paytm.digital.education.explore.service.impl.SearchServiceImpl;
import com.paytm.digital.education.explore.utility.SchoolUtilService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.paytm.digital.education.enums.ClassType.NURSERY;
import static com.paytm.digital.education.enums.ClassType.PRE_NURSERY;
import static com.paytm.digital.education.enums.ClassType.SIX;
import static com.paytm.digital.education.enums.ClassType.THREE;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.enums.SchoolBoardType.CBSE;
import static com.paytm.digital.education.enums.ShiftType.Afternoon;
import static com.paytm.digital.education.enums.ShiftType.Morning;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchoolDuplicateDataPruneTest {

    @Mock
    private CommonMongoRepository commonMongoRepository;

    @Mock
    private FacilityDataHelper facilityDataHelper;

    @Mock
    private DerivedAttributesHelper derivedAttributesHelper;

    @Mock
    private CTAHelper ctaHelper;

    @Mock
    private SearchServiceImpl searchService;

    @Mock
    private SchoolConfig schoolConfig;

    private SchoolService schoolService;

    @Before
    public void setUpPrerequisites() {
        SchoolUtilService schoolUtilService = new SchoolUtilService(schoolConfig);
        schoolService = new SchoolServiceImpl(
                commonMongoRepository,
                derivedAttributesHelper,
                facilityDataHelper,
                ctaHelper,
                searchService,
                schoolConfig,
                schoolUtilService,
                null,
                4
        );

        when(derivedAttributesHelper.getDerivedAttributes(anyMap(), anyString(), any()))
                .thenReturn(Collections.emptyMap());

        SearchResponse searchResponse = new SearchResponse();
        SearchResult searchResult = new SearchResult();
        searchResponse.setResults(searchResult);

        when(searchService.search(any(), any(), any()))
                .thenReturn(searchResponse);
    }

    @Test
    public void shiftDetailsShouldBePrunedIfSame() {
        School school = getSchoolWithShifts(Lists.newArrayList(
                ShiftDetails.builder().shiftType(Morning).classFrom(PRE_NURSERY).classTo(NURSERY)
                        .build(),
                ShiftDetails.builder().shiftType(Morning).classFrom(PRE_NURSERY).classTo(NURSERY)
                        .build()
        ));

        setUpCommonMongoRepoResponse(school);

        String field = "test";
        List<String> fields = Collections.singletonList(field);


        SchoolDetail schoolDetail = schoolService.getSchoolDetails(
                1L, APP, school.getOfficialName(), fields, null);

        assertEquals(schoolDetail.getShiftTables().size(), 1);
        ShiftTable shiftTable = schoolDetail.getShiftTables().get(0);
        assertEquals(shiftTable.getShiftDetailsRows().size(), 1);
        assertEquals(shiftTable.getBoardType(), CBSE);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getClassFrom(), PRE_NURSERY);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getClassTo(), NURSERY);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getShiftType(), Morning);
    }

    @Test
    public void shiftDetailsShouldNotBePrunedIfNotSame() {
        School school = getSchoolWithShifts(Lists.newArrayList(
                ShiftDetails.builder().shiftType(Morning).classFrom(PRE_NURSERY).classTo(NURSERY)
                        .build(),
                ShiftDetails.builder().shiftType(Afternoon).classFrom(THREE).classTo(SIX).build()
        ));

        setUpCommonMongoRepoResponse(school);

        String field = "test";
        List<String> fields = Collections.singletonList(field);


        SchoolDetail schoolDetail = schoolService.getSchoolDetails(
                1L, APP, school.getOfficialName(), fields, null);

        assertEquals(schoolDetail.getShiftTables().size(), 1);
        ShiftTable shiftTable = schoolDetail.getShiftTables().get(0);
        assertEquals(shiftTable.getShiftDetailsRows().size(), 2);
        assertEquals(shiftTable.getBoardType(), CBSE);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getClassFrom(), PRE_NURSERY);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getClassTo(), NURSERY);
        assertEquals(shiftTable.getShiftDetailsRows().get(0).getShiftType(), Morning);
        assertEquals(shiftTable.getShiftDetailsRows().get(1).getClassFrom(), THREE);
        assertEquals(shiftTable.getShiftDetailsRows().get(1).getClassTo(), SIX);
        assertEquals(shiftTable.getShiftDetailsRows().get(1).getShiftType(), Afternoon);
    }

    @Test
    public void feesDetailsShouldBePrunedIfSame() {
        School school = getSchoolWithFees(Lists.newArrayList(
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure1").build(),
                SchoolFeeDetails.builder().feeAmount(2L).feeTenure("Tenure1").build()
        ));

        setUpCommonMongoRepoResponse(school);

        String field = "test";
        List<String> fields = Collections.singletonList(field);

        SchoolDetail schoolDetail = schoolService.getSchoolDetails(
                1L, APP, school.getOfficialName(), fields, null);

        assertEquals(schoolDetail.getFeesDetails().size(), 1);
        assertEquals(schoolDetail.getFeesDetails().get(0).getFeeAmount().longValue(), 1);
        assertEquals(schoolDetail.getFeesDetails().get(0).getFeeTenure(), "Tenure1");
    }

    @Test
    public void feesDetailsShouldNotBePrunedIfNotSame() {
        School school = getSchoolWithFees(Lists.newArrayList(
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure1").build(),
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure2").build()
        ));

        setUpCommonMongoRepoResponse(school);

        String field = "test";
        List<String> fields = Collections.singletonList(field);

        SchoolDetail schoolDetail = schoolService.getSchoolDetails(
                1L, APP, school.getOfficialName(), fields, null);

        assertEquals(schoolDetail.getFeesDetails().size(), 2);
        assertEquals(schoolDetail.getFeesDetails().get(0).getFeeAmount().longValue(), 1);
        assertEquals(schoolDetail.getFeesDetails().get(0).getFeeTenure(), "Tenure1");
        assertEquals(schoolDetail.getFeesDetails().get(1).getFeeAmount().longValue(), 1);
        assertEquals(schoolDetail.getFeesDetails().get(1).getFeeTenure(), "Tenure2");
    }

    private void setUpCommonMongoRepoResponse(School school) {
        when(commonMongoRepository.getEntityByFields(
                any(),
                anyLong(),
                any(),
                any()))
                .thenReturn(school);
    }

    private School getSchoolWithShifts(List<ShiftDetails> shiftDetails) {
        BoardData boardData = new BoardData();
        boardData.setShifts(shiftDetails);
        boardData.setRelevantLinks(emptyList());
        Board board = new Board(CBSE, boardData);
        SchoolOfficialAddress schoolOfficialAddress = new SchoolOfficialAddress();
        School school = new School();
        school.setBoardList(Collections.singletonList(board));
        school.setAddress(schoolOfficialAddress);
        school.setOfficialName("test");
        return school;
    }

    private School getSchoolWithFees(List<SchoolFeeDetails> schoolFeeDetails) {
        BoardData boardData = new BoardData();
        boardData.setFeesDetails(schoolFeeDetails);
        boardData.setRelevantLinks(emptyList());
        Board board = new Board(CBSE, boardData);
        SchoolOfficialAddress schoolOfficialAddress = new SchoolOfficialAddress();
        School school = new School();
        school.setBoardList(Collections.singletonList(board));
        school.setAddress(schoolOfficialAddress);
        school.setOfficialName("test");
        return school;
    }
}
