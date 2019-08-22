package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.TopRankerRepository;
import com.paytm.digital.education.coaching.producer.model.request.CreateTopRankerRequest;
import com.paytm.digital.education.coaching.producer.model.request.UpdateTopRankerRequest;
import com.paytm.digital.education.coaching.producer.transformer.TopRankerTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TopRankerService {

    private final TopRankerTransformer topRankerTransformer;
    private final TopRankerRepository  topRankerRepository;

    public void create(final CreateTopRankerRequest request) {
        final TopRankerEntity topRankerEntity = this.topRankerTransformer.transform(request);
        this.topRankerRepository.save(topRankerEntity);
    }

    public void update(final UpdateTopRankerRequest request) {

        final Optional<TopRankerEntity> existingTopRankerEntityOptional =
                this.topRankerRepository.findByTopRankerId(request.getTopRankerId());

        if (!existingTopRankerEntityOptional.isPresent()) {
            log.error("TopRanker doesn't exist in db for id: {}", request.getTopRankerId());
            return;
        }

        final TopRankerEntity topRankerEntity =
                this.topRankerTransformer.transform(request, existingTopRankerEntityOptional.get());

        this.topRankerRepository.save(topRankerEntity);
    }
}
