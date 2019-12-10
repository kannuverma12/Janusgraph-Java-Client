package com.paytm.digital.education.explore.validators;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

@Service
@AllArgsConstructor
public class UrlParamsValidator {

    CommonEntityMongoDAO  commonEntityMongoDAO;

    public void validateInstuteUrlKey(long instituteId, String instituteUrlKey) {
        List<String> instituteFields = Arrays.asList(ExploreConstants.OFFICIAL_NAME);
        Institute institute =
                commonEntityMongoDAO.getInstituteByIdsIn(instituteId, instituteFields);
        if (Objects.isNull(institute)) {
            throw new BadRequestException(INVALID_INSTITUTE_ID,
                    INVALID_INSTITUTE_ID.getExternalMessage());
        }
        if (!instituteUrlKey
                .equals(CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()))) {
            throw new BadRequestException(INVALID_INSTITUTE_NAME,
                    INVALID_INSTITUTE_NAME.getExternalMessage());
        }
    }

    public void validateExamUrlKey(long examId, String examUrlKey) {
        List<String> examFields = Arrays.asList(ExploreConstants.EXAM_FULL_NAME);
        Exam exam = commonEntityMongoDAO.getExamById(examId, examFields);
        if (Objects.isNull(exam)) {
            throw new BadRequestException(INVALID_EXAM_ID,
                    INVALID_EXAM_ID.getExternalMessage());
        }
        if (!examUrlKey
                .equals(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()))) {
            throw new BadRequestException(INVALID_EXAM_NAME,
                    INVALID_EXAM_NAME.getExternalMessage());
        }
    }

}
