package com.denm.service;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public interface DownloadService {
    void download(String url, String fileId, String originalName);
}
