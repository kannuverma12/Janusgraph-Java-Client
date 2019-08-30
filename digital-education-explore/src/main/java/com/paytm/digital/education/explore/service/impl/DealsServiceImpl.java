package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.service.helper.StreamDataHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YEARS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BLANK;

@Service
@AllArgsConstructor
public class DealsServiceImpl {

    private StreamDataHelper streamDataHelper;

    public List<String> getSelectItems(String type) {
        List<String> itemsList = new ArrayList<>();
        if (!StringUtils.isEmpty(type)) {
            if (type.equalsIgnoreCase(STREAMS)) {
                Set<String> streams = streamDataHelper.getStreamLabelMap().entrySet().stream()
                        .map(e -> e.getValue()).filter(e -> !e.equalsIgnoreCase(OVERALL_RANKING))
                        .collect(Collectors.toSet());
                itemsList.addAll(new ArrayList<>(streams));
            } else if (type.equalsIgnoreCase(YEARS)) {
                LocalDate localDate = LocalDate.now();
                Integer year = localDate.getYear();
                List<String> yearList = IntStream.range(year, (year + SIX)).mapToObj(i -> BLANK + i)
                        .collect(Collectors.toList());
                itemsList.addAll(yearList);
            }
        }
        return itemsList;
    }
}
