package com.denm.model.upload;

import com.google.api.client.util.Key;

/** Represents a Upload feed. */
public class UploadFeed {
    @Key("Success")
    public boolean success;

    @Key("Data")
    public UploadData data;

    @Override
    public String toString() {
        return "UploadFeed{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}
