package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.property.reader.PropertyReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.WIDGETS;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class WidgetsDataHelperTest {
    @Mock
    private PropertyReader propertyReader;

    @InjectMocks
    private WidgetsDataHelper widgetsDataHelper;


    @Before
    public void setUp() {
        List<WidgetData> widgetList = new ArrayList<>();
        WidgetData widgetData1 = new WidgetData((long) 1, "JEE Mains", "jee-mains",
                "ENGINEERING_AND_ARCHITECTURE", null);
        widgetList.add(widgetData1);
        WidgetData widgetData2 = new WidgetData((long) 1882, "BITSAT", "bitsat",
                "ENGINEERING_AND_ARCHITECTURE", null);
        widgetList.add(widgetData2);
        WidgetData widgetData3 =
                new WidgetData((long) 453, "NATA", "nata", "ENGINEERING_AND_ARCHITECTURE", null);
        widgetList.add(widgetData3);
        WidgetData widgetData4 =
                new WidgetData((long) 789, "NEET", "neet", "MEDICINE_AND_ALLIED_SCIENCES"
                        , null);
        widgetList.add(widgetData4);
        WidgetData widgetData5 =
                new WidgetData((long) 959, "AIIMS", "aiims", "MEDICINE_AND_ALLIED_SCIENCES", null);
        widgetList.add(widgetData5);
        Widget widget = new Widget("exam", "Similar Exams", widgetList);
        List<Widget> data = new ArrayList<>();
        data.add(widget);

        Map<String, Object> outerMap = new LinkedHashMap<>();
        outerMap.put("data", data);

        Mockito.when(propertyReader.getPropertiesAsMapByKey("explore", "exam",
                "widgets"))
                .thenReturn(outerMap);

    }

    @Test
    public void getSimiliarExamsExcludeEntityNotInList() {

        List<Widget> widgets = widgetsDataHelper.getWidgets("exam", 2,
                "ENGINEERING_AND_ARCHITECTURE");
        assertNotNull(widgets);
        assertEquals(widgets.get(0).getData().get(0).getOfficialName(), "JEE Mains");
        assertEquals(widgets.get(0).getData().size(), 3);

        List<Widget> widgets3 = widgetsDataHelper.getWidgets("exam", 2447,
                "MEDICINE_AND_ALLIED_SCIENCES");
        assertNotNull(widgets3);
        assertEquals(widgets3.get(0).getData().get(0).getOfficialName(), "NEET");
        assertEquals(widgets3.get(0).getData().size(), 2);
    }

    @Test
    public void getSimiliarExamsExcludeEntityInList() {
        List<Widget> widgets2 = widgetsDataHelper.getWidgets("exam", 1,
                "ENGINEERING_AND_ARCHITECTURE");
        assertNotNull(widgets2);
        assertEquals(widgets2.get(0).getData().get(0).getOfficialName(), "BITSAT");
        assertEquals(widgets2.get(0).getData().size(), 2);

        List<Widget> widgets3 = widgetsDataHelper.getWidgets("exam", 789,
                "MEDICINE_AND_ALLIED_SCIENCES");
        assertNotNull(widgets3);
        assertEquals(widgets3.get(0).getData().get(0).getOfficialName(), "AIIMS");
        assertEquals(widgets3.get(0).getData().size(), 1);
    }
}