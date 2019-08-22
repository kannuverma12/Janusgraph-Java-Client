package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.WebLayout;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

/**
 * Section: represents a UI component which will show list of items and
 * actions to perform on an item
 */
@Document
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Section {
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private String name;
    private String label;
    private Layout layout;
    private Action action;
    private String type;

    @Field("sub_label")
    private String subLabel;

    @Field("view_all_label") @JsonProperty("view_all_label")
    private String viewAllLabel;

    @Field("view_less_label") @JsonProperty("view_less_label")
    private String viewLessLabel;

    private List<Map<String, Object>> items;


    @Data
    private class Layout {
        private WebLayout web;
    }


    @Data
    private class Action {
        private String       view;
        private List<String> fields;
    }
}
