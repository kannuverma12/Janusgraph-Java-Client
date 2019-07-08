package com.paytm.digital.education.form.model;

import com.paytm.digital.education.form.constants.FblConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.SimpleDateFormat;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MerchantFormData {

    @Field("orderId")
    private Long orderId;

    @Field("itemId")
    private Long itemId;

    @Field("fullName")
    private String name;

    @Field("email")
    private String email;

    @Field("mobileNumber")
    private String mobileNumber;

    @Field("amount")
    private Float amount;

    @Field("createdAt")
    private String createdDate;

    @Field("updatedAt")
    private String updatedDate;

    @Field("paymentStatus")
    private String paymentStatus;

    @Field("examType")
    private String examType;

    @Field("merchantCandidateId")
    private String registrationNumber;

    public MerchantFormData(FormData formData) {
        String format = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        if (formData.getFormFulfilment() != null) {
            this.orderId = formData.getFormFulfilment().getOrderId();
            this.itemId = formData.getFormFulfilment().getItemId();
            if (formData.getFormFulfilment().getCreatedDate() != null) {
                this.createdDate = simpleDateFormat.format(formData.getFormFulfilment().getCreatedDate());
            }
            if (formData.getFormFulfilment().getUpdatedDate() != null) {
                this.updatedDate = simpleDateFormat.format(formData.getFormFulfilment().getUpdatedDate());
            }
            if (formData.getFormFulfilment().getPaymentStatus() == null
                    || formData.getFormFulfilment().getPaymentStatus().isEmpty()) {
                this.paymentStatus = FblConstants.PENDING_STRING.toUpperCase();
            } else {
                this.paymentStatus = formData.getFormFulfilment().getPaymentStatus().toUpperCase();
            }
        }
        if (formData.getCandidateDetails() != null) {
            this.name = formData.getCandidateDetails().getFullName();
            this.email = formData.getCandidateDetails().getEmail();
            this.mobileNumber = formData.getCandidateDetails().getMobileNumber();
            this.amount = formData.getCandidateDetails().getAmount();
            this.examType = formData.getCandidateDetails().getExamType();
        }
        this.registrationNumber = formData.getMerchantCandidateId();
    }
}
