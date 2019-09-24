package com.paytm.digital.education.coaching.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

public class CoachingExceptionTranslator {

    public static void translate(final Exception e, final String requestFor)
            throws CoachingBaseException {

        try {
            throw e;
        } catch (final ResourceAccessException rae) {
            throw new CoachingTimeoutException(e, HttpStatus.GATEWAY_TIMEOUT, rae.getMessage());
        } catch (final HttpClientErrorException | HttpServerErrorException hte) {
            final HttpStatus httpStatus = hte.getStatusCode();
            throw new CoachingBaseException(e, httpStatus, hte.getResponseBodyAsString());
        } catch (final Exception ex) {
            throw new CoachingBaseException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
