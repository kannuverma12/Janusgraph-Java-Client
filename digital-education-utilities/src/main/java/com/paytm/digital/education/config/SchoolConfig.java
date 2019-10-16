package com.paytm.digital.education.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class SchoolConfig {
    @Value("${school.total.teachers.image.url}")
    private String totalTeachersImageURL;

    @Value("${school.student.teachers.ratio.image.url}")
    private String studentToTeachersImageURL;

    @Value("${school.morning.shift.image.url}")
    private String morningShiftImageURL;

    @Value("${school.evening.shift.image.url}")
    private String eveningShiftImageURL;

    @Value("${school.default.shift.image.url}")
    private String defaultShiftImageURL;

    @Value("${school.placeholder.logo.url}")
    private String schoolPlaceholderLogoURL;

    @Value("${school.contact.image.url}")
    private String schoolContactImageUrl;

    @Value("${school.location.logo.url}")
    private String schoolLocationLogoUrl;
}
