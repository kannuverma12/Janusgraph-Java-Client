package com.paytm.digital.education.profiles.db.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.entity.Base;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Document("profile")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@CompoundIndex(def = "{'customer_id':1, 'name':1, 'date_of_birth':1}", unique = true, name = "unique_profile")
public class ProfileIdentifierEntity extends Base {

    @Id
    @Field("profile_id")
    private Long profileId;

    @Field("customer_id")
    private Long customerId;

    @Field("name")
    private String name;

    @Field("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Transient
    public Integer getPriority() {
        return super.getPriority();
    }

}
