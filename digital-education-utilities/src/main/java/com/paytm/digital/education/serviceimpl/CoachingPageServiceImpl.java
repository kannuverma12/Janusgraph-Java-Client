package com.paytm.digital.education.serviceimpl;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.database.entity.Page;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.repository.PageRepository;
import com.paytm.digital.education.database.repository.SectionRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.service.PageService;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.CommonConstants.LOGO;

@Service
@AllArgsConstructor
public class CoachingPageServiceImpl implements PageService {

    private PageRepository    pageRepository;
    private SectionRepository sectionRepository;

    @Override
    @Cacheable(value = "page", key = "#pageName", unless = "#result == null ")
    public List<Section> getPageSections(@NotBlank final String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (page == null) {
            throw new BadRequestException(ErrorEnum.INVALID_PAGE_NAME,
                    ErrorEnum.INVALID_PAGE_NAME.getExternalMessage());
        }

        final Collection<String> pageSectionNames = page.getSections();
        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(pageSectionNames);

        final Map<String, Section> sectionsByName = pageSections.stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));

        List<Section> response = new ArrayList<>();
        for (String sectionName : pageSectionNames) {
            Section section = sectionsByName.get(sectionName);
            if (Objects.nonNull(section) && Objects.nonNull(section.getType())) {
                if (!CollectionUtils.isEmpty(section.getItems())) {
                    String logoFieldName = LOGO;
                    if (Objects.nonNull(logoFieldName)) {
                        for (Map<String, Object> item : section.getItems()) {
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
