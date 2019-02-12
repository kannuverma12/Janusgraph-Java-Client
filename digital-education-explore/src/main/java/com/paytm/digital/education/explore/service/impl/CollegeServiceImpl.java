package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollegeServiceImpl {

    @Autowired
    private CollegeRepository collegeRepository;

    public Institute getCollegeData(long collegeId) {
        Institute insti = collegeRepository.findByInstituteId(collegeId);

        return insti;
    }


}
