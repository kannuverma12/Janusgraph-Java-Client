package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.Board;
import com.paytm.digital.education.database.entity.BoardData;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.SchoolDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.utility.UploadUtil;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.enums.SchoolBoardType.CBSE;
import static com.paytm.digital.education.enums.SchoolBoardType.KARNATKA_BOARD;
import static com.paytm.digital.education.enums.SchoolEducationLevelType.PRIMARY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

@RunWith(MockitoJUnitRunner.class)
public class TransformSchoolServiceTest {
    @InjectMocks
    private TransformSchoolService transformSchoolService;

    @Mock
    private CommonMongoRepository commonMongoRepository;

    @Mock
    private IncrementalDataHelper incrementalDataHelper;

    @Mock
    private UploadUtil uploadUtil;

    private static List<School> mockDatabaseSchoolResults() {
        return Lists.newArrayList(
                School.builder().id("1").schoolId(1L).shortName("1st School").paytmKeys(
                        SchoolPaytmKeys.builder().formId("form11id").mid(2L).pid(3L).build()
                ).build(),
                School.builder().id("2").schoolId(2L).shortName("2nd School").paytmKeys(
                        SchoolPaytmKeys.builder().formId("form12id").mid(8L).pid(9L).build()
                ).build()
        );
    }

    private static List<SchoolDto> mockSchoolDtos() {
        return Lists.newArrayList(
                SchoolDto.builder().id(1L).shortName("1st School New Name").boardList(
                        Lists.newArrayList(
                                Board.builder().name(CBSE).data(
                                        BoardData.builder().educationLevel(PRIMARY).build()
                                ).build(),
                                Board.builder().name(KARNATKA_BOARD).data(
                                        BoardData.builder().educationLevel(PRIMARY).build()
                                ).build()
                        )
                ).build(),
                SchoolDto.builder().id(2L).shortName("2nd School New Name").boardList(
                        Lists.newArrayList(
                                Board.builder().name(CBSE).data(
                                        BoardData.builder().educationLevel(PRIMARY).build()
                                ).build(),
                                Board.builder().name(KARNATKA_BOARD).data(
                                        BoardData.builder().educationLevel(PRIMARY).build()
                                ).build()
                        )
                ).build());
    }

    private static SchoolPaytmKeys copySchoolPaytmKeys(SchoolPaytmKeys schoolPaytmKeys) {
        return SchoolPaytmKeys.builder()
                .pid(schoolPaytmKeys.getPid())
                .mid(schoolPaytmKeys.getMid())
                .formId(schoolPaytmKeys.getFormId())
                .build();
    }

    @Test
    public void testTransformation() {
        final List<School> mockDBSchools = mockDatabaseSchoolResults();
        final List<SchoolPaytmKeys> schoolPaytmKeysList = mockDBSchools
                .stream()
                .map(School::getPaytmKeys)
                .map(TransformSchoolServiceTest::copySchoolPaytmKeys)
                .collect(Collectors.toList());
        when(commonMongoRepository.getEntityFieldsByValuesIn(any(), any(), any(Class.class), any()))
                .thenReturn(mockDBSchools);
        final List<SchoolDto> schoolDtos = mockSchoolDtos();
        transformSchoolService.transformAndSaveSchoolsData(schoolDtos);
        for (int i = 0; i < mockDBSchools.size(); i++) {
            Assert.assertEquals(mockDBSchools.get(i).getShortName(), schoolDtos.get(i).getShortName());
            Assert.assertThat(mockDBSchools.get(i).getPaytmKeys(), samePropertyValuesAs(schoolPaytmKeysList.get(i)));
        }
    }
}
