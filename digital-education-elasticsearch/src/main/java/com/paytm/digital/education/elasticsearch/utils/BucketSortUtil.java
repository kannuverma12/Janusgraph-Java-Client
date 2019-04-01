package com.paytm.digital.education.elasticsearch.utils;

import java.util.Comparator;
import com.paytm.digital.education.elasticsearch.models.Bucket;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BucketSortUtil {

    public Comparator<Bucket> ascendingCountSort =  new Comparator<Bucket>() {
        @Override
        public int compare(Bucket bucket1, Bucket bucket2) {
            return (int) (bucket1.getDocCount() - bucket2.getDocCount());
        }
    };
    
    public Comparator<Bucket> descendingCountSort =  new Comparator<Bucket>() {
        @Override
        public int compare(Bucket bucket1, Bucket bucket2) {
            return (int) (bucket2.getDocCount() - bucket1.getDocCount());
        }
    };
    
}
