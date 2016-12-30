package com.denm.service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.denm.model.upload.UploadFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public interface UploadService {
    CompletableFuture<UploadFeed> upload(String url, File file);
}
