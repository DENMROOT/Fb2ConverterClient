package com.denm.impl;

import java.io.File;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class TestBase {
    static final String UPLOAD_URL = "http://go4convert.com/process/upload";
    static final String CONVERT_URL = "http://go4convert.com/base/convert";
    static final String DOWNLOAD_URL = "http://go4convert.com/process/download";

    File file1 = new File("src\\main\\resources\\Fb2\\12 Chairs.fb2");
    File file2 = new File("src\\main\\resources\\Fb2\\Les Miserables.fb2");
    File file3 = new File("src\\main\\resources\\Fb2\\Martin Iden.fb2");

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
}
