package com.paytm.digital.education.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * A utility class which will return the Map of 
 * - path of each variable in a class 
 * - caters nested class as well 
 * - takes care of primitive and java language specific classes for finding nested class
 * 
 * @author himanshujain
 *
 */

@UtilityClass
public class HierarchyIdentifierUtils {

    private final String startLevelName          = "";
    private final String keyNameInJsonAnnotation = "value=";


    /**
     * Takes care of json annotated name for now 
     * TODO: add functionalities as and when required
     */
    private static void iterateClass(Class<?> cls, Map<String, String> hierarchyMap,
            String hierarchyPath) {

        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {

            String fieldName = field.getName();
            Annotation[] annotationArr = field.getAnnotations();

            /*
             * extract value field from the below annotation str:
             * `@com.fasterxml.jackson.annotation.JsonProperty(index=-1, access=AUTO,
             * value=exams_accepted, required=false, defaultValue=)`
             */
            if (annotationArr.length != 0) {
                for (Annotation val : annotationArr) {
                    fieldName = CommonUtils.extractValueOfSubstringKey(val.toString(),
                            keyNameInJsonAnnotation, ",");
                }
            }

            String levelName = hierarchyPath.equals(startLevelName) ? fieldName
                    : hierarchyPath + "." + fieldName;
            hierarchyMap.put(fieldName, hierarchyPath);

            Type type = field.getGenericType();

            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                for (Type t : pt.getActualTypeArguments()) {
                    if (!CommonUtils.isLangSpecific((Class<?>) t)) {
                        iterateClass((Class<?>) t, hierarchyMap, levelName);
                    }
                }
            }
        }
    }

    public static Map<String, String> getClassHierarchy(Class<?> cls) {
        Map<String, String> hierarchyMap = new HashMap<>();
        iterateClass(cls, hierarchyMap, startLevelName);
        return hierarchyMap;
    }
}
