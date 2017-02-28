package com.denm.observable.service.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denm.model.upload.UploadFeed;
import com.denm.observable.service.UploadService;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class UploadServiceImpl implements UploadService {
    static final String UPLOAD_URL = "http://go4convert.com/process/upload";
    private final static Logger LOG = LoggerFactory.getLogger(UploadServiceImpl.class);
    private  HttpTransport HTTP_TRANSPORT;
    private JsonFactory JSON_FACTORY;

    public UploadServiceImpl(HttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) {
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
        this.JSON_FACTORY = JSON_FACTORY;
    }

    public UploadFeed upload(File file) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
        GenericUrl uploadUrl = new GenericUrl(UPLOAD_URL);

        MultipartContent content = new MultipartContent().setMediaType(
                new HttpMediaType("multipart/form-data")
                        .setParameter("boundary", "__END_OF_PART__"));

        // Add file to multipart content
        FileContent fileContent =  new FileContent("fb2", file);
        MultipartContent.Part part = new MultipartContent.Part(fileContent);
        part.setHeaders(new HttpHeaders().set(
                "Content-Disposition",
                String.format("form-data; name=\"content\"; filename=\"%s\"", file.getName())));
        content.addPart(part);

        UploadFeed uploadFeed;
        try {
            HttpRequest request = requestFactory.buildPostRequest(uploadUrl, content);
            uploadFeed = request.execute().parseAs(UploadFeed.class);
        } catch (IOException e) {
            LOG.error("HTTP error during upload: ", e.getMessage());
            return null;
        }

        if (uploadFeed.success) {
            LOG.info(uploadFeed.toString());
            uploadFeed.data.fileName = file.getName();
            return uploadFeed;
        } else {
            return null;
        }
    }

}
