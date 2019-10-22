package com.paytm.digital.education.explore.utility;

import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.explore.database.entity.Board;
import com.paytm.digital.education.explore.database.entity.BoardData;
import com.paytm.digital.education.explore.enums.ClassLevel;
import com.paytm.digital.education.explore.enums.ClassType;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.paytm.digital.education.explore.enums.ClassLevel.KINDERGARTEN;
import static com.paytm.digital.education.explore.enums.ClassLevel.MIDDLE_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.PRIMARY_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.SECONDARY_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.SENIOR_SECONDARY_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassType.EIGHT;
import static com.paytm.digital.education.explore.enums.ClassType.ELEVEN;
import static com.paytm.digital.education.explore.enums.ClassType.FIVE;
import static com.paytm.digital.education.explore.enums.ClassType.FOUR;
import static com.paytm.digital.education.explore.enums.ClassType.LKG;
import static com.paytm.digital.education.explore.enums.ClassType.NINE;
import static com.paytm.digital.education.explore.enums.ClassType.NURSERY;
import static com.paytm.digital.education.explore.enums.ClassType.ONE;
import static com.paytm.digital.education.explore.enums.ClassType.PRE_NURSERY;
import static com.paytm.digital.education.explore.enums.ClassType.SEVEN;
import static com.paytm.digital.education.explore.enums.ClassType.SIX;
import static com.paytm.digital.education.explore.enums.ClassType.TEN;
import static com.paytm.digital.education.explore.enums.ClassType.THREE;
import static com.paytm.digital.education.explore.enums.ClassType.TWELVE;
import static com.paytm.digital.education.explore.enums.ClassType.TWO;
import static com.paytm.digital.education.explore.enums.ClassType.UKG;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SchoolUtilServiceTest {

    @Mock
    private SchoolConfig schoolConfig;

    @InjectMocks
    private SchoolUtilService schoolUtilService;

    @Test
    public void testClassLevelAscendancy() {
        assertTrue(PRE_NURSERY.compareTo(NURSERY) < 0);
        assertTrue(NURSERY.compareTo(LKG) < 0);
        assertTrue(LKG.compareTo(UKG) < 0);
        assertTrue(UKG.compareTo(ONE) < 0);
        assertTrue(ONE.compareTo(TWO) < 0);
        assertTrue(TWO.compareTo(THREE) < 0);
        assertTrue(THREE.compareTo(FOUR) < 0);
        assertTrue(FOUR.compareTo(FIVE) < 0);
        assertTrue(FIVE.compareTo(SIX) < 0);
        assertTrue(SIX.compareTo(SEVEN) < 0);
        assertTrue(SEVEN.compareTo(EIGHT) < 0);
        assertTrue(EIGHT.compareTo(NINE) < 0);
        assertTrue(NINE.compareTo(TEN) < 0);
        assertTrue(TEN.compareTo(ELEVEN) < 0);
        assertTrue(ELEVEN.compareTo(TWELVE) < 0);
    }

    @Test
    public void testGenerateClassInfoTableWithRegularInfo() {
        Board board = new Board();
        BoardData boardData = new BoardData();
        boardData.setClassFrom(FOUR);
        boardData.setClassTo(SIX);
        board.setData(boardData);

        List<Triple<ClassType, ClassType, ClassLevel>> expected1 = Arrays.asList(
                Triple.of(FOUR, FIVE, PRIMARY_SCHOOL),
                Triple.of(SIX, SIX, MIDDLE_SCHOOL)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual1 = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual1)
                .isEqualToComparingFieldByFieldRecursively(expected1);

        boardData.setClassFrom(LKG);
        boardData.setClassTo(SEVEN);

        List<Triple<ClassType, ClassType, ClassLevel>> expected2 = Arrays.asList(
                Triple.of(LKG, UKG, KINDERGARTEN),
                Triple.of(ONE, FIVE, PRIMARY_SCHOOL),
                Triple.of(SIX, SEVEN, MIDDLE_SCHOOL)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual2 = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual2)
                .isEqualToComparingFieldByFieldRecursively(expected2);

        boardData.setClassFrom(UKG);
        boardData.setClassTo(EIGHT);

        List<Triple<ClassType, ClassType, ClassLevel>> expected3 = Arrays.asList(
                Triple.of(UKG, UKG, KINDERGARTEN),
                Triple.of(ONE, FIVE, PRIMARY_SCHOOL),
                Triple.of(SIX, EIGHT, MIDDLE_SCHOOL)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual3 = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual3)
                .isEqualToComparingFieldByFieldRecursively(expected3);
    }


    @Test
    public void testGenerateClassInfoTableWithNurseryInfo() {
        Board board = new Board();
        BoardData boardData = new BoardData();
        boardData.setClassFrom(PRE_NURSERY);
        boardData.setClassTo(PRE_NURSERY);
        board.setData(boardData);

        List<Triple<ClassType, ClassType, ClassLevel>> expected1 = Arrays.asList(
                Triple.of(PRE_NURSERY, PRE_NURSERY, KINDERGARTEN)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual1 = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual1)
                .isEqualToComparingFieldByFieldRecursively(expected1);

        boardData.setClassFrom(NURSERY);
        boardData.setClassTo(NURSERY);

        List<Triple<ClassType, ClassType, ClassLevel>> expected2 = Arrays.asList(
                Triple.of(NURSERY, NURSERY, KINDERGARTEN)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual2 = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual2)
                .isEqualToComparingFieldByFieldRecursively(expected2);

    }

    @Test
    public void testGenerateClassInfoTableWithNurseryInfoAllRanges() {
        Board board = new Board();
        BoardData boardData = new BoardData();
        boardData.setClassFrom(PRE_NURSERY);
        boardData.setClassTo(TWELVE);
        board.setData(boardData);

        List<Triple<ClassType, ClassType, ClassLevel>> expected = Arrays.asList(
                Triple.of(PRE_NURSERY, NURSERY, KINDERGARTEN),
                Triple.of(ONE, FIVE, PRIMARY_SCHOOL),
                Triple.of(SIX, EIGHT, MIDDLE_SCHOOL),
                Triple.of(NINE, TEN, SECONDARY_SCHOOL),
                Triple.of(ELEVEN, TWELVE, SENIOR_SECONDARY_SCHOOL)
        );

        List<Triple<ClassType, ClassType, ClassLevel>> actual = schoolUtilService.generateClassInfoTable(board);
        assertThat(actual)
                .isEqualToComparingFieldByFieldRecursively(expected);
    }
}
