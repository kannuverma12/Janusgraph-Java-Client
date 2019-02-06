package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.explore.database.entity.Stream;
import com.paytm.digital.education.explore.service.StreamService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class StreamController {

    private StreamService streamService;

    @RequestMapping(method = RequestMethod.GET, path = "/streams")
    public Iterable<Stream> getStreams() {
        return streamService.getAllStreams();
    }
}
