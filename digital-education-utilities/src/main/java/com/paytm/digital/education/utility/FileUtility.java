package com.paytm.digital.education.utility;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

public class FileUtility {

    public static void deleteFileFromLocalHost(String localFilePath) {
        org.apache.commons.io.FileUtils.deleteQuietly(new File(localFilePath));
    }

    public static String getFileName(String fileName) throws MalformedURLException {
        return FilenameUtils.getName(new URL(fileName).getPath());
    }

    public static String getResourceFileAsString(String fileName) {
        InputStream is = FileUtility.class.getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

    public static URI getResourcePath(String fileName) {
        ClassLoader classLoader = FileUtility.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file.toURI();
    }

    public static void storeFile(String filePath, byte[] csvData) throws IOException {

        if (Utility.nullOrEmpty(filePath)) {
            throw new IllegalArgumentException("STORE_FILE:EMPTY_FILE_PATH");
        }

        if (Utility.nullOrEmpty(csvData)) {
            throw new IllegalArgumentException("STORE_FILE:EMPTY_CSV_DATA");
        }

        FileOutputStream stream = new FileOutputStream(filePath);
        stream.write(csvData);
        stream.close();
    }

}
