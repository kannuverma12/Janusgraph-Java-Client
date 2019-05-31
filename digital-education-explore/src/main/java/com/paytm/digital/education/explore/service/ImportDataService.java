package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.database.entity.CampusEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ImportDataService {
    public Map<Long, List<CampusEvent>> importData() throws IOException,
            GeneralSecurityException, ParseException;
}
