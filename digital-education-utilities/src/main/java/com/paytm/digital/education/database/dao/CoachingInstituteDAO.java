package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.metrics.annotations.NullValueAlert;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class CoachingInstituteDAO {

    @Autowired
    private CoachingInstituteRepositoryNew coachingInstituteRepositoryNew;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public CoachingInstituteEntity save(@NonNull CoachingInstituteEntity coachingInstitute) {
        if (Objects.isNull(coachingInstitute.getInstituteId())) {
            coachingInstitute.setInstituteId(
                    sequenceGenerator
                            .getNextSequenceId(coachingInstitute.getClass().getSimpleName()));
        }
        return coachingInstituteRepositoryNew.save(coachingInstitute);
    }

    public CoachingInstituteEntity findByInstituteId(@NonNull Long id) {
        return coachingInstituteRepositoryNew.findByInstituteId(id);
    }

    public List<CoachingInstituteEntity> findAllByInstituteId(@NonNull List<Long> ids) {
        return coachingInstituteRepositoryNew.findAllByInstituteId(ids);
    }

    public List<CoachingInstituteEntity> findAll() {
        return this.coachingInstituteRepositoryNew.findAll();
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public CoachingInstituteEntity findByInstituteId(String instituteIdField, long instituteId,
            List<String> projectionFields, List<String> mandatoryFields) {
        return commonMongoRepository.getEntityByFields(
                instituteIdField, instituteId, CoachingInstituteEntity.class, projectionFields);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public CoachingInstituteEntity findByPaytmMerchantId(String paytmMerchantIdFiled,
            String paytmMerchantId,
            List<String> projectionFields, List<String> mandatoryFields) {
        return commonMongoRepository.getEntityByFields(
                paytmMerchantIdFiled, paytmMerchantId, CoachingInstituteEntity.class,
                projectionFields);
    }
}
