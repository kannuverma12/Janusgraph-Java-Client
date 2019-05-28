package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.database.repository.LeadRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.service.LeadService;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.database.entity.Lead.Constants.COURSE_ID;

@Service
@AllArgsConstructor
public class LeadServiceImpl implements LeadService {
    private LeadRepository        leadRepository;
    private CommonMongoRepository commonMongoRepository;
    private LeadCareer360Service  leadCareer360Service;
    ExecutorService myExecutor = Executors..newCachedThreadPool();

    private void sendLead(Lead lead) {
        myExecutor.execute(new Runnable() {
            public void run() {
                Lead c360Lead = new Lead();
                BeanUtils.copyProperties(lead, c360Lead);
                leadCareer360Service.send(c360Lead);
                leadRepository.upsertLead(c360Lead);
            }
        });
    }

    @Override
    public void captureLead(@NotNull Lead lead) {
        if (EducationEntity.COURSE.equals(lead.getEntityType())) {
            Course course = commonMongoRepository
                    .getEntityByFields(COURSE_ID, lead.getEntityId(), Course.class, null);
            if (Objects.isNull(course)) {
                throw new BadRequestException(ErrorEnum.INVALID_COURSE_ID,
                        ErrorEnum.INVALID_COURSE_ID.getExternalMessage());
            }
        } else if (EducationEntity.EXAM.equals(lead.getEntityType())) {
            Exam exam = commonMongoRepository
                    .getEntityByFields(EXAM_ID, lead.getEntityId(), Exam.class, null);
            if (Objects.isNull(exam)) {
                throw new BadRequestException(ErrorEnum.INVALID_EXAM_ID,
                        ErrorEnum.INVALID_EXAM_ID.getExternalMessage());
            }
        }
        sendLead(lead);
    }
}
