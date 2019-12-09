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
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository             pageRepository;
    private final SectionRepository          sectionRepository;
    private final EntityDataDiscoveryService dataDiscoveryService;

    private static final Logger log = LoggerFactory.getLogger(PageServiceImpl.class);

    @Override
    @Cacheable(value = "page", key = "'landing_page'+#pageName", unless = "#result == null")
    public List<Section> getPageSections(String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (Objects.isNull(page)) {
            throw ResourceNotFoundException.builder()
                    .errorCode(ErrorCode.DP_RESOURCE_NOT_FOUND)
                    .resourceName(pageName).build();
        }

        final Collection<String> pageSectionNames = page.getSections();
        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(pageSectionNames);
        final Map<String, Section> sectionsByName = pageSections.stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));
        List<Section> response = new ArrayList<>();
        for (String sectionName : pageSectionNames) {
            Section section = sectionsByName.get(sectionName);
            try {
                if (Objects.nonNull(section) && Objects.nonNull(section.getType())) {
                    switch (PageSection.fromValue(section.getType())) {
                        case COLLEGES_IN_FOCUS:
                        case TOP_COLLEGES:
                            dataDiscoveryService.updateInstituteData(section);
                            break;
                        case TOP_EXAMS_APP:
                        case BROWSE_BY_EXAM_LEVEL:
                            dataDiscoveryService.updateTopExamsAppData(section);
                            break;
                        case TOP_EXAMS:
                            dataDiscoveryService.updateTopExamsData(section);
                            break;
                        case STREAMS:
                            dataDiscoveryService.updatePaytmStreamData(section);
                            break;
                        case LOCATIONS:
                            dataDiscoveryService.updateLocationData(section);
                            break;
                        case APP_FOOTER:
                            dataDiscoveryService.updateAppFooterData(section);
                            break;
                        case EXAMS_FOOTER:
                            dataDiscoveryService.updateExamsFooterData(section);
                            break;
                        case POPULAR_EXAMS_APP:
                            dataDiscoveryService.updatePopularExamsData(section);
                            break;
                        case EXAM_FOCUS_APP:
                            dataDiscoveryService.updateExamFocusData(section);
                            break;
                        case TOP_SCHOOLS:
                        case TOP_SCHOOLS_APP:
                        case SCHOOLS_FOCUS:
                            dataDiscoveryService.updateSchoolData(section);
                            break;
                        default:
                    }
                    response.add(section);
                }
            } catch (Exception ex) {
                log.error("Error in processing landing page section : {}", ex, sectionName);
            }
        }
        return response;
    }
}
