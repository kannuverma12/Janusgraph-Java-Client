package com.paytm.digital.education.form.model;

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

    public MerchantFormData(FormData formData) {
        String format = "dd-MM-yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        this.orderId = formData.getFormFulfilment().getOrderId();
        this.itemId = formData.getFormFulfilment().getItemId();
        this.name = formData.getCandidateDetails().getFullName();
        this.email = formData.getCandidateDetails().getEmail();
        this.mobileNumber = formData.getCandidateDetails().getMobileNumber();
        this.amount = formData.getCandidateDetails().getAmount();
        if (formData.getFormFulfilment().getCreatedDate() != null) {
            this.createdDate = simpleDateFormat.format(formData.getFormFulfilment().getCreatedDate());
        }
        if (formData.getFormFulfilment().getUpdatedDate() != null) {
            this.updatedDate = simpleDateFormat.format(formData.getFormFulfilment().getUpdatedDate());
        }
        this.paymentStatus = formData.getFormFulfilment().getPaymentStatus();
        this.examType = formData.getCandidateDetails().getExamType();
    }

}
