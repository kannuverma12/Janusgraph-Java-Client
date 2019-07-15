package com.paytm.digital.education.explore.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DataIngestionSftpConfig {
    public static String  host;
    public static String  username;
    public static String  keyPath;
    public static Integer port;
    public static String  filePath;

    @Value("${sftp.username}")
    public void setUsername(String name) {
        username = name;
    }

    @Value("${sftp.host}")
    public void setHost(String hostname) {
        host = hostname;
    }

    @Value("${sftp.keypath}")
    public void setKeyPath(String path) {
        keyPath = path;
    }

    @Value("${sftp.port}")
    public void setPort(Integer portNo) {
        port = portNo;
    }

    @Value("${sftp.filepath}")
    public void setFilePath(String path) {
        filePath = path;
    }

    public static String getHost() {
        return host;
    }

    public static String getUsername() {
        return username;
    }

    public static String getKeyPath() {
        return keyPath;
    }

    public static Integer getPort() {
        return port;
    }

    public static String getFilePath() {
        return filePath;
    }
}
