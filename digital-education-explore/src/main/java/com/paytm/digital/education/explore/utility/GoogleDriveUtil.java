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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.paytm.digital.education.explore.config.GoogleConfig;
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

import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.FILENAME;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.ID;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.INPUTSTREAM;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.MIMETYPE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.OFFLINE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.USER;

@UtilityClass
public class GoogleDriveUtil {

    private static final String       APPLICATION_NAME        = "Education";
    private static final JsonFactory  JSON_FACTORY            = JacksonFactory.getDefaultInstance();
    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER      =
            new java.io.File(GoogleConfig.getCredentialFolderPath());
    private static final String CLIENT_SECRET_FILE_NAME = GoogleConfig.getCredentialFileName();
    /*
     ** Global instance of the scopes required by this quickstart. If modifying these
     ** scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES                  =
            Collections.singletonList(DriveScopes.DRIVE);

    private Credential getCredentials(final NetHttpTransport httpTransport) throws
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
                .setAccessType(OFFLINE).build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(USER);
    }

    private Drive createGoogleDriveService() throws IOException, GeneralSecurityException {
        // 1: Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // 2: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(httpTransport);
        // 3: Create Google Drive Service.
        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    private Sheets createSheetService() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(httpTransport);
        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }


    public Map<String, Object> downloadFile(String fileUrl)
            throws IOException, GeneralSecurityException {
        Drive service = createGoogleDriveService();
        URL url = new URL(fileUrl);
        String fileId = getQueryMap(url.getQuery()).get(ID);
        File file = service.files().get(fileId).execute();
        String fileName = file.getName();
        String mimeType = file.getMimeType();
        InputStream is = service.files().get(fileId).executeMediaAsInputStream();
        Map<String, Object> fileData = new HashMap<>();
        fileData.put(FILENAME, fileName);
        fileData.put(MIMETYPE, mimeType);
        fileData.put(INPUTSTREAM, is);
        return fileData;
    }

    private Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    private List<List<Object>> readGoogleSheet(String sheetId, String range)
            throws GeneralSecurityException,
            IOException {
        Sheets sheetsService = createSheetService();
        ValueRange response = sheetsService.spreadsheets().values()
                .get(sheetId, range)
                .execute();
        return response.getValues();
    }

    public List<Object> getDataFromSheet(String sheetId, String range, String headerRange)
            throws GeneralSecurityException, IOException {
        List<List<Object>> data = readGoogleSheet(sheetId, range);
        if (Objects.nonNull(data)) {
            List<List<Object>> headerData =
                    readGoogleSheet(sheetId, headerRange);
            List<String> headers = new ArrayList<>();
            for (List row : headerData) {
                for (Object column : row) {
                    headers.add(column.toString().trim().replace(" ", "_").toLowerCase());
                }
            }
            List<Object> sheetDataList = new ArrayList<>();
            for (List row : data) {
                Map<String, Object> sheetData = new HashMap<>();
                int index = 0;
                for (Object column : row) {
                    sheetData.put(headers.get(index), column);
                    index++;
                }
                sheetDataList.add(sheetData);
            }
            return sheetDataList;
        }
        return null;
    }
}
