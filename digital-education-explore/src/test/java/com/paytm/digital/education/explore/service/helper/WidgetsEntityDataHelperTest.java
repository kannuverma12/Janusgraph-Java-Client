package com.paytm.digital.education.explore.service.helper;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class WidgetsEntityDataHelperTest {
    @Mock
    private PropertyReader propertyReader;

    @InjectMocks
    private WidgetsDataHelper widgetsDataHelper;


    @Before
    public void setUp() {
        List<WidgetData> widgetList = new ArrayList<>();
        Map<String, String> importantDates = new HashMap<>();
        importantDates.put("Application", "10 May 2019");
        importantDates.put("Exam", "15 July 19 (Tentative)");
        importantDates.put("Result", "30 September (Tentative)");

        WidgetData widgetData1 =
                new WidgetData((long) 1, "JEE Mains", "JEE Mains", "jee-mains", true,
                        "ENGINEERING_AND_ARCHITECTURE", 1L, "ENGINEERING", null, null, importantDates);
        widgetList.add(widgetData1);
        WidgetData widgetData2 = new WidgetData((long) 1882, "BITSAT", "BITSAT", "bitsat", true,
                "ENGINEERING_AND_ARCHITECTURE", 1L, "Engineering", null, null, importantDates);
        widgetList.add(widgetData2);
        WidgetData widgetData3 =
                new WidgetData((long) 453, "NATA", "NATA", "nata", true,
                        "ENGINEERING_AND_ARCHITECTURE",1L, "Engineering", null, null, importantDates);
        widgetList.add(widgetData3);
        WidgetData widgetData4 =
                new WidgetData((long) 789, "NEET", "NEET", "neet", true,
                        "MEDICINE_AND_ALLIED_SCIENCES",2L, "MEDICINE", null, null, importantDates);
        widgetList.add(widgetData4);
        WidgetData widgetData5 =
                new WidgetData((long) 959, "AIIMS", "AIIMS", "aiims", true,
                        "MEDICINE_AND_ALLIED_SCIENCES", 2L, "MEDICINE", null, null, importantDates);
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
