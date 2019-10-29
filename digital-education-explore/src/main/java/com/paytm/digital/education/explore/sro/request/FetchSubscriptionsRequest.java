package com.paytm.digital.education.explore.sro.request;

import com.paytm.digital.education.enums.SubscribableEntityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
public class FetchSubscriptionsRequest extends FieldsAndFieldGroupRequest {
    @NotNull
    private Long userId;

    @NotNull
    private SubscribableEntityType entity;

    @Min(0)
    private Long offset;

    @Min(1)
    @Max(50)
    private Long limit;

    public FetchSubscriptionsRequest(Long userId, SubscribableEntityType entity, Long offset, Long limit,
                                   List<String> fields, String fieldGroup) {
        super(fields, fieldGroup);
        this.userId = userId;
        this.entity = entity;
        this.offset = offset;
        this.limit = limit;
    }

}
