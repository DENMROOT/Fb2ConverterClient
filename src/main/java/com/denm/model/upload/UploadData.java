package com.denm.model.upload;

import com.google.api.client.util.Key;

/** Represents a Upload Data feed. */
public class UploadData {
    @Key
    public String format;

    @Key
    public String id;

    @Key
    public String ds;

    public String fileName;

    @Override
    public String toString() {
        return "UploadData{" +
                "format='" + format + '\'' +
                ", id='" + id + '\'' +
                ", ds='" + ds + '\'' +
                '}';
    }
}
