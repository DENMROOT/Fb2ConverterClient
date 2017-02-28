package com.denm.observable.service;

import java.io.File;

import com.denm.model.upload.UploadFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public interface UploadService {
    UploadFeed upload(File file);
}
