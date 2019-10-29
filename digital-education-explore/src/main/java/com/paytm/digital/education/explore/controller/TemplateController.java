package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.FtlTemplate;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.utility.FileUtility;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;


@RestController
@AllArgsConstructor
@RequestMapping(EDUCATION_BASE_URL)
public class TemplateController {

    private CommonMongoRepository commonMongoRepository;

    @GetMapping("/v1/template/create")
    public @ResponseBody String insertHighlightsTemplate(
            @RequestHeader("x-user-id") @Min(1) String userId) throws Exception {
        //TODO - add validations if template already exists don't insert
        if (StringUtils.isBlank(userId) || !getDigest(userId)
                .equals("e568bedf3a8fa9c1adea53e020f9b2b4")) {
            throw new RuntimeException(
                    "You are not authorised to perform this action. Please sit back and enjoy your coffee !!");
        }
        String template = FileUtility.getResourceFileAsString("highlights_template.ftl");
        if (StringUtils.isNotBlank(template)) {
            FtlTemplate ftlTemplate = new FtlTemplate();
            ftlTemplate.setName("highlights");
            ftlTemplate.setActive(true);
            ftlTemplate.setEntity("institute");
            ftlTemplate.setTemplate(template);
            ftlTemplate.setCreatedAt(new Date());
            ftlTemplate.setUpdatedAt(new Date());

            commonMongoRepository.saveOrUpdate(ftlTemplate);
        }
        return "OK";
    }

    private String getDigest(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
