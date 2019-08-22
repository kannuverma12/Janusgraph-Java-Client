package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingInstitute;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoachingInstituteDAO {

    @Autowired
    private CoachingInstituteRepositoryNew coachingInstituteRepositoryNew;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingInstitute save(CoachingInstitute coachingInstitute) {
        coachingInstitute.setInstituteId(
                sequenceGenerator.getNextSequenceId(coachingInstitute.getClass().getSimpleName()));
        return coachingInstituteRepositoryNew.save(coachingInstitute);
    }

    public CoachingInstitute findByInstituteId(Long id) {
        return coachingInstituteRepositoryNew.findByInstituteId(id);
    }
}
