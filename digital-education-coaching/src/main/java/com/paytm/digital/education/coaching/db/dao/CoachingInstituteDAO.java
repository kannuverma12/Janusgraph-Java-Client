package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
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


}
