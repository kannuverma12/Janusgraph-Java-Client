package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.ExamLogo;
import com.paytm.digital.education.database.repository.ExamLogoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class ExamLogoHelper {

    private ExamLogoRepository examLogoRepository;

    @Cacheable(value = "examLogoUrl")
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
