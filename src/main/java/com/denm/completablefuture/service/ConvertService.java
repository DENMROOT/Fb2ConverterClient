package com.denm.completablefuture.service;

import java.util.concurrent.CompletableFuture;

import com.denm.model.convert.ConvertFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public interface ConvertService {
    CompletableFuture<ConvertFeed> convert(String url, String fileId, String originalName);
}
