package com.paytm.digital.education.explore.utility;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.util.Pair;
import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class GoogleDriveUtil {

    private static final String       APPLICATION_NAME        = "Google Drive API Java Quickstart";
    private static final JsonFactory  JSON_FACTORY            = JacksonFactory.getDefaultInstance();
    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER      =
            new java.io.File(System.getProperty("user.home"), "credentials");
    private static final String       CLIENT_SECRET_FILE_NAME = "client_secret.json";
    /*
     ** Global instance of the scopes required by this quickstart. If modifying these
     ** scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES                  =
            Collections.singletonList(DriveScopes.DRIVE);

    private  Credential getCredentials(final NetHttpTransport httpTransport) throws
            IOException {

        java.io.File clientSecretFilePath =
                new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }
        // Load client secrets.
        InputStream in = new FileInputStream(clientSecretFilePath);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow
                flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                .setAccessType("offline").build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private Drive createGoogleDriveService() throws IOException, GeneralSecurityException{
        // create parent directory (if necessary)
        System.out.println("CREDENTIALS_FOLDER: " + CREDENTIALS_FOLDER.getAbsolutePath());
        // 1: Create CREDENTIALS_FOLDER
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();
            System.out.println("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME
                    + " into folder above.. and rerun this class!!");
            return null;
        }
        // 2: Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(httpTransport);
        // 5: Create Google Drive Service.
        return new Drive.Builder(httpTransport, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
    }

    private Sheets createSheetService() throws IOException, GeneralSecurityException {
        // create parent directory (if necessary)
        System.out.println("CREDENTIALS_FOLDER: " + CREDENTIALS_FOLDER.getAbsolutePath());
        // 1: Create CREDENTIALS_FOLDER
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();
            System.out.println("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME
                    + " into folder above.. and rerun this class!!");
            return null;
        }
        // 2: Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(httpTransport);
        // 5: Create Google Drive Service.
        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
    }


    public Pair<String, InputStream> downloadFile(boolean useDirectDownload, String fileUrl)
            throws IOException, GeneralSecurityException {
        Drive service = createGoogleDriveService();
        URL url = new URL(fileUrl);
        String fileId = getQueryMap(url.getQuery()).get("id");
        File file = service.files().get(fileId).execute();
        String fileName = file.getName();
        return new Pair<>(fileName, service.files().get(fileId).executeMediaAsInputStream());
    }

    private Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public List<Object> readGoogleSheet(String sheetId, String range) throws GeneralSecurityException,
            IOException{
        Sheets sheetsService = createSheetService();
        ValueRange response = sheetsService.spreadsheets().values()
                .get(sheetId, range)
                .execute();
        List<List<Object>> data = response.getValues();
        if (Objects.nonNull(data)) {
            boolean start = true;
            List<String> headers = new ArrayList<>();
            List<Object> sheetDataList = new ArrayList<>();
            for (List row : data) {
                Map<String, Object> sheetData = new HashMap<>();
                int index = 0;
                for (Object column : row) {
                    if (start == true) {
                        headers.add(column.toString().trim().replace(" ", "_").toLowerCase());
                    } else {
                        sheetData.put(headers.get(index), column);
                    }
                    index++;
                }
                if (start == true) {
                    start = false;
                } else {
                    sheetDataList.add(sheetData);
                }
            }
            return sheetDataList;
        }
        return null;
    }


}
