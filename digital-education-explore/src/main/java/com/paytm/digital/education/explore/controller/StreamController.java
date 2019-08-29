package com.paytm.digital.education.explore.controller;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.explore.service.StreamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class StreamController {

    private StreamService streamService;

    @RequestMapping(method = RequestMethod.GET, path = "/v1/streams")
    public Iterable<StreamEntity> getStreams() {
        return streamService.getAllStreams();
    }
}
