package com.paytm.digital.education.exception;

import static com.paytm.digital.education.mapping.ErrorEnum.ENTITY_FIELD_MISSING_IN_DATABASE;

public class EntityRequiredFieldMissingInDBException extends EducationException {

    private static final String ENTITY_FIELD_ERROR = "Error in field %s of entity %s";

    public EntityRequiredFieldMissingInDBException(String entity, String field) {
        super(
                ENTITY_FIELD_MISSING_IN_DATABASE,
                String.format(ENTITY_FIELD_ERROR, entity, field),
                new Object[] {entity, field}
        );
    }

}
