package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.entity.Lead;
import com.paytm.digital.education.explore.database.repository.LeadRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class LeadDetailHelper {

    private LeadRepository leadRepository;

    public List<Long> getLeadEntities(EducationEntity educationEntity, Long userId,
            List<Long> entityIds) {
        List<Lead> leadList =
                leadRepository.fetchLeadByEntityTypeAndUserIdAndEntityIdIn(educationEntity, userId,
                        entityIds);
        if (!CollectionUtils.isEmpty(leadList)) {
            return leadList.stream().map(lead -> lead.getEntityId()).collect(Collectors.toList());
        }
        return null;
    }

    public List<Long> getInterestedLeadEntitiesForInstitutes(Long userId, List<Long> instituteId) {
        List<Lead> leads =
                leadRepository.fetchInterestedLeadByInstituteIdANdUserId(userId, instituteId);
        if (!CollectionUtils.isEmpty(leads)) {
            return leads.stream().map(lead -> lead.getInstituteId()).collect(Collectors.toList());
        }
        return null;
    }

    public List<Long> getInterestedLeadByEntity(EducationEntity educationEntity, Long userId, Long entityId) {
        List<Lead> leads =
                leadRepository
                        .fetchInterestedLeadByEntityIdAndUserId(educationEntity, userId, entityId);
        if (!CollectionUtils.isEmpty(leads)) {
            return leads.stream().map(lead -> lead.getInstituteId()).collect(Collectors.toList());
        }
        return null;
    }
}
