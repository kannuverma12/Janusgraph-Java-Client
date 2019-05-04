package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.response.dto.detail.CompareCourseDetail;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface CompareCourseService {
    List<CompareCourseDetail> compareCourses(List<Long> courseList, String fieldGroup, List<String> fields)
            throws IOException, TimeoutException;
}
