package com.denm.observable.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denm.model.convert.ConvertFeed;
import com.denm.observable.service.ConvertService;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.util.GenericData;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class ConvertServiceImpl implements ConvertService {
    private final static Logger LOG = LoggerFactory.getLogger(ConvertServiceImpl.class);
    private HttpTransport HTTP_TRANSPORT;
    private  JsonFactory JSON_FACTORY;

    public ConvertServiceImpl(HttpTransport HTTP_TRANSPORT, JsonFactory JSON_FACTORY) {
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
        this.JSON_FACTORY = JSON_FACTORY;
    }

    @Override
    public ConvertFeed convert(String url, String fileId, String originalName) {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
        GenericUrl uploadUrl = new GenericUrl(url);

        GenericData params = new GenericData();
        params.put("File", fileId);
        params.put("ToFormat", "to-epub");
        params.put("FromFormat", "Fb2");
        params.put("Original", originalName);

        ConvertFeed convertFeed;
        try {
            HttpRequest request = requestFactory.buildPostRequest(uploadUrl, new UrlEncodedContent(params));
            convertFeed = request.execute().parseAs(ConvertFeed.class);
        } catch (IOException e) {
            LOG.error("HTTP error during converting: ", e.getMessage());
            return null;
        }

        if (convertFeed.success) {
            LOG.info(convertFeed.toString());
            convertFeed.fileName = originalName;
            return convertFeed;
        } else {
            return null;
        }
    }

}
