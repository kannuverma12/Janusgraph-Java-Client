package com.paytm.digital.education.explore.utility;

import com.paytm.digital.education.config.SchoolConfig;
import com.paytm.digital.education.database.entity.Board;
import com.paytm.digital.education.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.database.entity.ShiftDetails;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.explore.enums.ClassLevel;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftDetailsResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftTable;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.enums.ClassType.EIGHT;
import static com.paytm.digital.education.enums.ClassType.ELEVEN;
import static com.paytm.digital.education.enums.ClassType.FIVE;
import static com.paytm.digital.education.enums.ClassType.LKG;
import static com.paytm.digital.education.enums.ClassType.NINE;
import static com.paytm.digital.education.enums.ClassType.NOT_PROVIDED;
import static com.paytm.digital.education.enums.ClassType.NURSERY;
import static com.paytm.digital.education.enums.ClassType.ONE;
import static com.paytm.digital.education.enums.ClassType.PRE_NURSERY;
import static com.paytm.digital.education.enums.ClassType.SIX;
import static com.paytm.digital.education.enums.ClassType.TEN;
import static com.paytm.digital.education.enums.ClassType.TWELVE;
import static com.paytm.digital.education.enums.ClassType.UKG;
import static com.paytm.digital.education.explore.enums.ClassLevel.KINDERGARTEN;
import static com.paytm.digital.education.explore.enums.ClassLevel.MIDDLE_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.PRIMARY_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.SECONDARY_SCHOOL;
import static com.paytm.digital.education.explore.enums.ClassLevel.SENIOR_SECONDARY_SCHOOL;

@Service
@Data
public class SchoolUtilService {

    private final SchoolConfig schoolConfig;

    private static final Logger log = LoggerFactory.getLogger(SchoolUtilService.class);

    private static final String INCORRECT_DATA_FORMAT_ERROR_TEMPLATE
            = "classFrom classTo have mixed nursery and kindergarten values - %s %s";

    private static final List<Triple<ClassType, ClassType, ClassLevel>> CLASS_LEVEL_TABLE =
            Arrays.asList(
                    Triple.of(LKG, UKG, KINDERGARTEN),
                    Triple.of(ONE, FIVE, PRIMARY_SCHOOL),
                    Triple.of(SIX, EIGHT, MIDDLE_SCHOOL),
                    Triple.of(NINE, TEN, SECONDARY_SCHOOL),
                    Triple.of(ELEVEN, TWELVE, SENIOR_SECONDARY_SCHOOL)
            );

    public String buildLogoFullPathFromRelativePath(String relativeLogoPath) {
        return StringUtils.isBlank(relativeLogoPath)
                ? schoolConfig.getSchoolPlaceholderLogoURL()
                : CommonUtils.addCDNPrefixAndEncode(relativeLogoPath);
    }

    public List<Triple<ClassType, ClassType, ClassLevel>> generateClassInfoTable(Board board) {
        ClassType classFrom = board.getData().getClassFrom();
        ClassType classTo = board.getData().getClassTo();
        List<Triple<ClassType, ClassType, ClassLevel>> output = new ArrayList<>();
        int nextRangeIndex = findAndHandleStartRangeAndReturnNextRange(classFrom, classTo, output);
        handleSubsequentRanges(classTo, nextRangeIndex, output);
        return output;
    }

    public boolean isClassLevelInfoValid(Board board) {
        ClassType classFrom = board.getData().getClassFrom();
        ClassType classTo = board.getData().getClassTo();
        return isClassTypeNotEmpty(classFrom)
                && isClassTypeNotEmpty(classTo) && isOrderProper(classFrom, classTo)
                && doesNotHaveBothNurseryAndKindergarten(classFrom, classTo);
    }

    public boolean isFeeDataValid(SchoolFeeDetails feeDetails) {
        Long feeAmount = feeDetails.getFeeAmount();
        String feeTenure = feeDetails.getFeeTenure();
        return Objects.nonNull(feeAmount) && StringUtils.isNotBlank(feeTenure);
    }

    private boolean handleSpecialNurseryFormatRange(
            ClassType classFrom, ClassType classTo, List<Triple<ClassType, ClassType, ClassLevel>> output) {
        boolean isSpecialNurseryForm = PRE_NURSERY.equals(classFrom) || NURSERY.equals(classFrom);
        if (isSpecialNurseryForm) {
            ClassType outputClassTo = NURSERY.compareTo(classTo) < 0 ? NURSERY : classTo;
            ClassLevel firstRangeClassLevel = CLASS_LEVEL_TABLE.get(0).getRight();
            output.add(Triple.of(classFrom, outputClassTo, firstRangeClassLevel));
        }
        return isSpecialNurseryForm;
    }

    private int findAndHandleStartRangeAndReturnNextRange(
            ClassType classFrom, ClassType classTo, List<Triple<ClassType, ClassType, ClassLevel>> output) {
        if (handleSpecialNurseryFormatRange(classFrom, classTo, output)) {
            int nextRangeIndex = 1;
            return nextRangeIndex;
        }

        int i;
        for (i = 0; i < CLASS_LEVEL_TABLE.size()
                && CLASS_LEVEL_TABLE.get(i).getLeft().compareTo(classFrom) <= 0; i++) {
        }
        output.add(Triple.of(
                classFrom, CLASS_LEVEL_TABLE.get(i - 1).getMiddle(), CLASS_LEVEL_TABLE.get(i - 1).getRight()));
        return i;
    }

    private void handleSubsequentRanges(
            ClassType classTo, int nextRangeIndex,
            List<Triple<ClassType, ClassType, ClassLevel>> output) {
        for (int i = nextRangeIndex; i < CLASS_LEVEL_TABLE.size()
                && CLASS_LEVEL_TABLE.get(i).getLeft().compareTo(classTo) <= 0; i++) {
            ClassType rightValueInRange = CLASS_LEVEL_TABLE.get(i).getMiddle();
            ClassType outputClassTo = rightValueInRange.compareTo(classTo) < 0
                    ? rightValueInRange : classTo;
            output.add(
                    Triple.of(
                            CLASS_LEVEL_TABLE.get(i).getLeft(),
                            outputClassTo,
                            CLASS_LEVEL_TABLE.get(i).getRight()));
        }
    }

    private boolean isClassTypeNotEmpty(ClassType classType) {
        return Objects.nonNull(classType) && !NOT_PROVIDED.equals(classType);
    }

    private boolean isOrderProper(ClassType classFrom, ClassType classTo) {
        return classTo.compareTo(classFrom) >= 0;
    }

    private boolean doesNotHaveBothNurseryAndKindergarten(ClassType classFrom, ClassType classTo) {
        try {
            if (PRE_NURSERY.equals(classFrom) || NURSERY.equals(classFrom)
                    || PRE_NURSERY.equals(classTo) || NURSERY.equals(classTo)) {
                Assert.isTrue(!LKG.equals(classFrom), "classFrom should not be LKG");
                Assert.isTrue(!UKG.equals(classFrom), "classFrom should not be UKG");
                Assert.isTrue(!LKG.equals(classTo), "classTo should not be LKG");
                Assert.isTrue(!UKG.equals(classTo), "classTo should not be UKG");
            }
        } catch (IllegalArgumentException e) {
            log.debug(INCORRECT_DATA_FORMAT_ERROR_TEMPLATE, e, classFrom, classTo);
            return false;
        }
        return true;
    }

    public boolean isShiftDetailsValid(ShiftDetails shiftDetails) {
        return areShiftFieldsNonEmpty(shiftDetails)
                && isShiftClassRangeWellFormed(shiftDetails)
                && doesNotHaveBothNurseryAndKindergarten(
                shiftDetails.getClassFrom(),
                shiftDetails.getClassTo());
    }

    public boolean isShiftTableDataCorrect(ShiftTable shiftTable) {
        List<ShiftDetailsResponse> shiftDetailsList = shiftTable.getShiftDetailsRows();
        if (shiftDetailsList.size() == 0) {
            return false;
        }
        if (shiftDetailsList.size() == 1) {
            return true;
        }
        List<ShiftDetailsResponse> shiftDetailsResponseList =
                shiftDetailsList
                        .stream()
                        .sorted(Comparator.comparing(ShiftDetailsResponse::getClassFrom))
                        .collect(Collectors.toList());
        for (int i = 1; i < shiftDetailsResponseList.size(); i++) {
            ClassType currentClassFrom = shiftDetailsResponseList.get(i).getClassFrom();
            ClassType previousClassTo = shiftDetailsResponseList.get(i - 1).getClassTo();
            if (currentClassFrom.compareTo(previousClassTo) <= 0) {
                return false;
            }
        }
        return true;
    }

    private boolean areShiftFieldsNonEmpty(ShiftDetails shiftDetails) {
        return isClassTypeNotEmpty(shiftDetails.getClassFrom())
                && isClassTypeNotEmpty(shiftDetails.getClassTo())
                && Objects.nonNull(shiftDetails.getShiftType());
    }

    private boolean isShiftClassRangeWellFormed(ShiftDetails shiftDetails) {
        return shiftDetails.getClassFrom().compareTo(shiftDetails.getClassTo()) <= 0;
    }

}
