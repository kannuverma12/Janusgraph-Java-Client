package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.paytm.digital.education.database.entity.SchoolAdmission;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImportantDate extends SchoolAdmission {
    private String type;

    public ImportantDate(SchoolAdmission schoolAdmission, String type) {
        super(schoolAdmission);
        this.type = type;
    }
}
