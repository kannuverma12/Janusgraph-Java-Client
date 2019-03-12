package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AVERAGE_PACKAGE_LABEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAXIMUM_PACKAGE_LABEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MEDIAN_PACKAGE_LABEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MINIMUM_PACKAGE_LABEL;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.response.dto.detail.Placement;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlacementDataHelper {

    public List<Placement> getSalariesPlacements(Institute institute) {
        if (!CollectionUtils.isEmpty(institute.getSalariesPlacement())) {
            List<Placement> placementList = new ArrayList<>();
            for (com.paytm.digital.education.explore.database.entity.Placement salaryPlacement :
                    institute.getSalariesPlacement()) {
                Placement placement = new Placement();
                placement.setDegree(salaryPlacement.getDegree());
                placement.setYear(salaryPlacement.getYear());
                if (salaryPlacement.getMaximum() != null) {
                    placement.setSalary(salaryPlacement.getMaximum());
                    placement.setLabel(MAXIMUM_PACKAGE_LABEL);
                } else if (salaryPlacement.getMedian() != null) {
                    placement.setSalary(salaryPlacement.getMedian());
                    placement.setLabel(MEDIAN_PACKAGE_LABEL);
                } else if (salaryPlacement.getAverage() != null) {
                    placement.setSalary(salaryPlacement.getAverage());
                    placement.setLabel(AVERAGE_PACKAGE_LABEL);
                } else {
                    placement.setSalary(salaryPlacement.getMinimum());
                    placement.setLabel(MINIMUM_PACKAGE_LABEL);
                }
                placementList.add(placement);
            }
            return placementList;
        }
        return null;
    }
}
