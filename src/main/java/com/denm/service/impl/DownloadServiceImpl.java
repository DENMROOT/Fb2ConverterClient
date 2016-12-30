package com.denm.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denm.service.DownloadService;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.GenericData;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class DownloadServiceImpl implements DownloadService {
    private final static Logger LOG = LoggerFactory.getLogger(DownloadServiceImpl.class);
    public static final String RESOURCES_OUTPUT_DIR = "src\\main\\resources\\output\\";

    private HttpTransport HTTP_TRANSPORT;
    private  JsonFactory JSON_FACTORY;

    public DownloadServiceImpl(HttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) {
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
        this.JSON_FACTORY = JSON_FACTORY;
    }

    @Override
    public void download(String url, String fileId, String originalName) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
        GenericUrl uploadUrl = new GenericUrl(url);

        GenericData params = new GenericData();
        params.put("doc", fileId);
        params.put("to", "");
        params.put("original", originalName);

        HttpRequest request = null;
        try {
            request = requestFactory.buildPostRequest(uploadUrl, new UrlEncodedContent(params));
        } catch (IOException e) {
            LOG.error("HTTP error during downloading: ", e.getMessage());
        }

        File file = new File(RESOURCES_OUTPUT_DIR + originalName + ".epub");
        try(OutputStream outputStream = new FileOutputStream(file)){
            HttpResponse downloadFeed = request.execute();
            downloadFeed.download(outputStream);
            LOG.info("File successfully saved: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error writing file: {}", file.getAbsolutePath());
        }
    }
}
