package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import com.paytm.digital.education.database.entity.Page;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.repository.PageRepository;
import com.paytm.digital.education.database.repository.SectionRepository;
import com.paytm.digital.education.explore.service.PageService;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;

import static com.paytm.digital.education.explore.constants.ExploreConstants.APP_FOOTER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BANNER_MID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COLLEGE_FOCUS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ICON;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IMAGE_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUB_ITEMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LOCATIONS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LOGO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TOP_COLLEGES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TOP_EXAMS_APP;

@Service
@AllArgsConstructor
public class PageServiceImpl implements PageService {

    private PageRepository    pageRepository;
    private SectionRepository sectionRepository;

    @Override
    @Cacheable(value = "page", key = "#pageName", unless = "#result == null ")
    public List<Section> getPageSections(@NotBlank final String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (page == null) {
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
            if (section != null && section.getType() != null) {
                String logoFieldName = null;
                switch (section.getType()) {
                    case TOP_EXAMS_APP:
                    case COLLEGE_FOCUS:
                        logoFieldName = LOGO;
                        break;
                    case STREAMS:
                    case TOP_COLLEGES:
                    case LOCATIONS:
                    case APP_FOOTER:
                        logoFieldName = ICON;
                        break;
                    case BANNER_MID:
                        logoFieldName = IMAGE_URL;
                        break;
                    default:
                }
                if (Objects.nonNull(logoFieldName)) {
                    for (Map<String, Object> item : section.getItems()) {
                        if (TOP_EXAMS_APP.equals(section.getType())) {
                            for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {

                                Map<String, Object> subitems =
                                        (Map<String, Object>) topExamsPerLevel.getValue();
                                List<Map<String, String>> topExams =
                                        (List<Map<String, String>>) subitems.get(SUB_ITEMS);
                                for (Map<String, String> topExam : topExams) {
                                    String logo = CommonUtil
                                            .getAbsoluteUrl(topExam.get(logoFieldName),
                                                    section.getType());
                                    topExam.put(logoFieldName, logo);
                                }
                            }
                        } else {
                            String logo =
                                    CommonUtil.getAbsoluteUrl((String) item.get(logoFieldName),
                                            section.getType());
                            item.put(logoFieldName, logo);
                        }
                    }
                }
                response.add(section);
            }
        }
        return response;
    }
}
