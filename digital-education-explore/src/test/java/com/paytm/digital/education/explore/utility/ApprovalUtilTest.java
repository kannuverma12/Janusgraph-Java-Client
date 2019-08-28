package com.paytm.digital.education.explore.utility;

import com.paytm.digital.education.utility.CommonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import static com.paytm.digital.education.constant.ExploreConstants.CONSTITUENT_OF;
import static com.paytm.digital.education.constant.ExploreConstants.CONSTITUENT;
import static com.paytm.digital.education.constant.ExploreConstants.UGC;
import static com.paytm.digital.education.constant.ExploreConstants.AFFILIATED_TO;
import static com.paytm.digital.education.constant.ExploreConstants.AFFILIATED;
import static com.paytm.digital.education.constant.ExploreConstants.APPROVED_BY;

public class ApprovalUtilTest {

    @Test
    public void multipleApprovals() {
        List<String> approvals = new ArrayList<>();
        approvals.add(AFFILIATED);
        approvals.add(UGC);
        Map<String, String> expected = new HashMap<>();
        expected.put(AFFILIATED_TO, "du");
        expected.put(APPROVED_BY, UGC.toUpperCase());
        Assert.assertEquals(expected, CommonUtil.getApprovals(approvals, "du"));
    }

    @Test
    public void ugc() {
        List<String> approvals = new ArrayList<>();
        approvals.add(UGC);
        Map<String, String> expected = new HashMap<>();
        expected.put(APPROVED_BY, UGC.toUpperCase());
        Assert.assertEquals(expected, CommonUtil.getApprovals(approvals, null));
    }

    @Test
    public void constituentOf() {
        List<String> approvals = new ArrayList<>();
        approvals.add(CONSTITUENT);
        Map<String, String> expected = new HashMap<>();
        expected.put(CONSTITUENT_OF, "du");
        Assert.assertEquals(expected, CommonUtil.getApprovals(approvals, "du"));
    }

    @Test
    public void constituentOfWithoutUniversity() {
        List<String> approvals = new ArrayList<>();
        approvals.add(CONSTITUENT_OF);
        Map<String, String> expected = new HashMap<>();
        Assert.assertNull(CommonUtil.getApprovals(approvals, null));
    }

    @Test
    public void emptyApprovals() {
        List<String> approvals = new ArrayList<>();
        Map<String, String> expected = new HashMap<>();
        Assert.assertNull(CommonUtil.getApprovals(approvals, "du"));
    }
}
