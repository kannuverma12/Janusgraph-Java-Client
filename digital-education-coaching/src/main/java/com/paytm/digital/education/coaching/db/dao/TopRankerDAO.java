package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.database.entity.TopRankerEntity;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.repository.TopRankerRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TopRankerDAO {

    @Autowired
    TopRankerRepository topRankerRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public TopRankerEntity save(@NonNull TopRankerEntity topRankerEntity) {
        if (Objects.isNull(topRankerEntity.getTopRankerId())) {
            topRankerEntity.setTopRankerId(sequenceGenerator
                    .getNextSequenceId(topRankerEntity.getClass().getSimpleName()));
        }
        return topRankerRepository.save(topRankerEntity);
    }

    public TopRankerEntity findByTopRankerId(@NonNull Long id) {
        return topRankerRepository.findByTopRankerId(id);
    }

    public List<TopRankerEntity> findAll() {
        return this.topRankerRepository.findAll();
    }

}
