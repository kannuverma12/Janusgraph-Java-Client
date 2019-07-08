package com.paytm.digital.education.service;



import java.io.IOException;
import java.nio.charset.Charset;

import com.paytm.digital.education.dto.SftpConfigData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SftpService {
    private static final String STRICT_HOST_KEY_CHECKING_KEY   = "StrictHostKeyChecking";
    private static final String STRICT_HOST_KEY_CHECKING_VALUE = "no";
    private static final String CHANNEL_TYPE                   = "sftp";
    private static final String DEFAULT_ENCODING               = "UTF-8";
    private static final String KEY                            = "key";
    private JSch jsch = null;

    public Session createSession(SftpConfigData sftpConfigData) {

        Session session = null;
        jsch = new JSch();
        log.info("[SftpServiceImpl.getFile] properties {}");

        try {
            if (StringUtils.isNotBlank(sftpConfigData.getKeyPath())) {
                String key = StreamUtils.copyToString(
                        new ClassPathResource(sftpConfigData.getKeyPath()).getInputStream(),
                        Charset.defaultCharset());
                jsch.addIdentity(KEY, key.getBytes(), null,
                        sftpConfigData.getPassword().getBytes());
                session = jsch.getSession(sftpConfigData.getUsername(), sftpConfigData.getHost(),
                        sftpConfigData.getPort());
            } else {
                session = jsch.getSession(sftpConfigData.getUsername(), sftpConfigData.getHost(),
                        sftpConfigData.getPort());
                session.setPassword(sftpConfigData.getPassword());
            }

        } catch (JSchException | IOException e) {
            log.error("[SftpServiceImpl.createSession] error while creating session", e);
        }
        session.setConfig(STRICT_HOST_KEY_CHECKING_KEY, STRICT_HOST_KEY_CHECKING_VALUE);
        try {
            session.connect();
        } catch (JSchException e) {
            log.error("[SftpServiceImpl.createSession] error while connecting to session", e);
        }
        return session;
    }



}
