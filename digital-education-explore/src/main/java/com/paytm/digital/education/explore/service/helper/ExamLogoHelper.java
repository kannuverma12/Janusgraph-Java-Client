package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.ExamLogo;
import com.paytm.digital.education.explore.database.repository.ExamLogoRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ExamLogoHelper {

    private ExamLogoRepository examLogoRepository;

    public String getExamLogoUrl(Long examId, String dbLogoUrl) {

        ExamLogo examLogo = examLogoRepository.findByExamId(examId);
        if (Objects.nonNull(examLogo) && StringUtils.isNotBlank(examLogo.getLogo())) {
            return CommonUtil.getLogoLink(examLogo.getLogo(), EducationEntity.EXAM);
        } else if (StringUtils.isNotBlank(dbLogoUrl)) {
            return CommonUtil.getLogoLink(dbLogoUrl, EducationEntity.EXAM);
        } else {
            return CommonUtil.getLogoLink(ExploreConstants.DUMMY_EXAM_ICON, EducationEntity.EXAM);
        }

    }

}
