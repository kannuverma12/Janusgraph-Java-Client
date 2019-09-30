package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.database.entity.ShiftDetails;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.paytm.digital.education.enums.ClassType.NURSERY;
import static com.paytm.digital.education.enums.ClassType.PRE_NURSERY;
import static com.paytm.digital.education.enums.ClassType.SIX;
import static com.paytm.digital.education.enums.ClassType.THREE;
import static com.paytm.digital.education.enums.ShiftType.Afternoon;
import static com.paytm.digital.education.enums.ShiftType.Morning;

@RunWith(MockitoJUnitRunner.class)
public class SchoolDetailsResponseHelperTest {

    @Test
    public void feesDetailsShouldBePrunedIfSame() {
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setFeesDetails(Lists.newArrayList(
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure1").build(),
                SchoolFeeDetails.builder().feeAmount(2L).feeTenure("Tenure1").build()
        ));
        SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
        Assert.assertEquals(schoolDetail.getFeesDetails().size(), 1);
        Assert.assertEquals(schoolDetail.getFeesDetails().get(0).getFeeAmount().longValue(), 1);
        Assert.assertEquals(schoolDetail.getFeesDetails().get(0).getFeeTenure(), "Tenure1");
    }

    @Test
    public void feesDetailsShouldNotBePrunedIfNotSame() {
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setFeesDetails(Lists.newArrayList(
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure1").build(),
                SchoolFeeDetails.builder().feeAmount(1L).feeTenure("Tenure2").build()
        ));
        SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
        Assert.assertEquals(schoolDetail.getFeesDetails().size(), 2);
        Assert.assertEquals(schoolDetail.getFeesDetails().get(0).getFeeAmount().longValue(), 1);
        Assert.assertEquals(schoolDetail.getFeesDetails().get(0).getFeeTenure(), "Tenure1");
        Assert.assertEquals(schoolDetail.getFeesDetails().get(1).getFeeAmount().longValue(), 1);
        Assert.assertEquals(schoolDetail.getFeesDetails().get(1).getFeeTenure(), "Tenure2");
    }

    @Test
    public void shiftDetailsShouldBePrunedIfSame() {
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setShiftDetailsList(Lists.newArrayList(
                ShiftDetails.builder().shiftType(Morning).classFrom(PRE_NURSERY).classTo(NURSERY).build(),
                ShiftDetails.builder().shiftType(Morning).classFrom(THREE).classTo(SIX).build()
        ));
        SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().size(), 1);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getShiftType(), Morning);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getClassFrom(), PRE_NURSERY);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getClassTo(), NURSERY);
    }

    @Test
    public void shiftDetailsShouldNotBePrunedIfNotSame() {
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setShiftDetailsList(Lists.newArrayList(
                ShiftDetails.builder().shiftType(Morning).classFrom(PRE_NURSERY).classTo(NURSERY).build(),
                ShiftDetails.builder().shiftType(Afternoon).classFrom(THREE).classTo(SIX).build()
        ));
        SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().size(), 2);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getShiftType(), Morning);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getClassFrom(), PRE_NURSERY);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(0).getClassTo(), NURSERY);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(1).getShiftType(), Afternoon);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(1).getClassFrom(), THREE);
        Assert.assertEquals(schoolDetail.getShiftDetailsList().get(1).getClassTo(), SIX);
    }
}
