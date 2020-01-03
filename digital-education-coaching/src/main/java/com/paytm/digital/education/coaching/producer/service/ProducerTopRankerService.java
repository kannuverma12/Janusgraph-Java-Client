package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.database.dao.TopRankerDAO;
import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProducerTopRankerService {

    private static final Logger log = LoggerFactory.getLogger(ProducerTopRankerService.class);

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
