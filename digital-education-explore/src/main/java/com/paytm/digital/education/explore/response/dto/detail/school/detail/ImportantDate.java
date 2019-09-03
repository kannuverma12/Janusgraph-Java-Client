package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.paytm.digital.education.explore.database.entity.SchoolAdmission;
import lombok.Data;

@Data
public class ImportantDate extends SchoolAdmission {
    private String type;

    public ImportantDate(SchoolAdmission schoolAdmission, String type) {
        super(schoolAdmission);
        this.type = type;
    }
}
