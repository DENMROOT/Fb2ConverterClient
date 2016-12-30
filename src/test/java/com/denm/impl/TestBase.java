package com.denm.impl;

import java.io.IOException;

import org.junit.BeforeClass;

import com.denm.service.ConvertService;
import com.denm.service.DownloadService;
import com.denm.service.UploadService;
import com.denm.service.impl.ConvertServiceImpl;
import com.denm.service.impl.DownloadServiceImpl;
import com.denm.service.impl.UploadServiceImpl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class TestBase {
    public static final String UPLOAD_URL = "http://go4convert.com/process/upload";
    public static final String CONVERT_URL = "http://go4convert.com/base/convert";
    public static final String DOWNLOAD_URL = "http://go4convert.com/process/download";

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    static UploadService uploadService;
    static ConvertService convertService;
    static DownloadService downloadService;

    @BeforeClass
    public static void init(){
        uploadService = new UploadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        convertService = new ConvertServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        downloadService = new DownloadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
    }
}
