package com.paytm.digital.education.explore.utility;

import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.explore.dto.SchoolDto;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertNotNull;

public class BeanUtilTest {
    @Test
    public void beanCopyTest() {
        SchoolPaytmKeys schoolPaytmKeys = new SchoolPaytmKeys(10L, 10L, "form1234");
        School school = School.builder().schoolId(1L).shortName("school entity1")
                .formerName("former name school entity 1")
                .paytmKeys(schoolPaytmKeys).build();
        SchoolDto schoolDto = SchoolDto.builder().id(2L).shortName("school dto1")
                .formerName("former name school dto 1").build();
        BeanUtils.copyProperties(schoolDto, school);
        assertNotNull(school.getPaytmKeys());
        assertThat(school.getPaytmKeys())
                .isEqualToComparingFieldByFieldRecursively(schoolPaytmKeys);
    }
}
