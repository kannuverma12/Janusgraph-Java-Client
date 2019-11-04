package com.paytm.digital.education.explore.service;


import com.paytm.digital.education.database.entity.StreamEntity;

public interface StreamService {

    Iterable<StreamEntity> getAllStreams();
}
