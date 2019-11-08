package com.paytm.digital.education.advice;

import org.apache.commons.beanutils.PropertyUtilsBean;

import static java.lang.System.out;

public class Playground {
    public static void main(String[] args) {
        Test t = new Test();
        PropertyUtilsBean pub = new PropertyUtilsBean();
        try {
            Object o1 = pub.getProperty(t, "prop1");
            out.println(o1);
            Object o2 = pub.getProperty(t, "prop2");
            out.println(o2);
        } catch (Exception e) {
            e.printStackTrace();
            out.println("a");
        }
    }
}
