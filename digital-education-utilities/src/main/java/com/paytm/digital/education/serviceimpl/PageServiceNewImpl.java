package com.paytm.digital.education.serviceimpl;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Page;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.repository.PageRepository;
import com.paytm.digital.education.database.repository.SectionRepository;
import com.paytm.digital.education.enums.PageSection;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import com.paytm.digital.education.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PageServiceNewImpl implements PageService {

    private final PageRepository             pageRepository;
    private final SectionRepository          sectionRepository;
    private final StreamDAO                  streamDAO;
    private final EntityDataDiscoveryService dataDiscoveryService;

    @Override
    public List<Section> getPageSections(String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (Objects.isNull(page)) {
            throw ResourceNotFoundException.builder()
                    .errorCode(ErrorCode.DP_RESOURCE_NOT_FOUND)
                    .resourceName(pageName).build();
        }

        final Collection<String> pageSectionNames = page.getSections();
        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(pageSectionNames);
        List<Section> response = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pageSections)) {
            for (Section section : pageSections) {
                Section responseSection = null;
                switch (PageSection.fromValue(section.getName())) {
                    case COLLEGES_IN_FOCUS:
                    case TOP_COLLEGES:
                        responseSection = dataDiscoveryService.updateInstituteData(section);
                        break;
                    case TOP_EXAMS_APP:
                    case BROWSE_BY_EXAM_LEVEL:
                        responseSection = dataDiscoveryService.updateTopExamsAppData(section);
                        break;
                    case STREAMS:
                        responseSection = dataDiscoveryService.updatePaytmStreamData(section);
                        break;
                    case LOCATIONS:
                        responseSection = dataDiscoveryService.updateLocationData(section);
                        break;
                    case TOP_SCHOOLS:
                    case TOP_SCHOOLS_APP:
                        responseSection = dataDiscoveryService.updateSchoolData(section);
                        break;
                    case BANNER_MID:
                        responseSection = dataDiscoveryService.updateBannerLinks(section);
                        break;
                }
                if (Objects.nonNull(responseSection)) {
                    response.add(responseSection);
                }
            }
        }
        return response;
    }
}
