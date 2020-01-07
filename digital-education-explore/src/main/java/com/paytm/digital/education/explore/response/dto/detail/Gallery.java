package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gallery implements Serializable {

    private static final long serialVersionUID = 4722870371287156879L;

    @JsonProperty("images")
    private Map<String, List<String>> images;

    @JsonProperty("videos")
    private Map<String, List<String>> videos;

    @JsonIgnore
    public boolean isEmpty() {
        return (CollectionUtils.isEmpty(this.images) && CollectionUtils.isEmpty(this.videos));
    }
}
