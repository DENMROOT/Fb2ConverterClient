package com.denm.model.convert;

import com.google.api.client.util.Key;

/** Represents a Convert feed. */
public class ConvertFeed {
    @Key("Success")
    public boolean success;

    @Key("File")
    public String file;

    public String fileName;

    @Override
    public String toString() {
        return "ConvertFeed{" +
                "success=" + success +
                ", file='" + file + '\'' +
                '}';
    }
}
