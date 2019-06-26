package com.paytm.digital.education.explore.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

public interface ImportDataService {
    public boolean importData() throws IOException,
            GeneralSecurityException, ParseException;
}