package com.paytm.digital.education.explore.database.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Page: represents a UI Page (like a dashboard) on which {@link Section} can appear
 */
@Document
@Data
public class Page {
    private String id;
    private String name;
    private Map<String, String> sections;
}
