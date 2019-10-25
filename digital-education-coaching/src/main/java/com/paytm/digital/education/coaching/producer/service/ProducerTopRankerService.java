package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.TopRankerDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ProducerTopRankerService {

    private final TopRankerDAO topRankerDAO;

    public TopRankerEntity create(final TopRankerDataRequest request) {
        TopRankerEntity topRankerEntity = new TopRankerEntity();
        ConverterUtil.setTopRanker(request, topRankerEntity);

        try {
            return topRankerDAO.save(topRankerEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }

    }

    public TopRankerEntity update(final TopRankerDataRequest request) {

        final TopRankerEntity existingTopRankerEntity =
                Optional.ofNullable(
                        topRankerDAO.findByTopRankerId(request.getTopRankerId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "top ranker id not present : " + request.getTopRankerId()));
        ConverterUtil.setTopRanker(request, existingTopRankerEntity);

        try {
            return topRankerDAO.save(existingTopRankerEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }
}
