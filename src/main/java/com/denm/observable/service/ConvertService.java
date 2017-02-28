package com.denm.observable.service;

import com.denm.model.convert.ConvertFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public interface ConvertService {
    ConvertFeed convert(String fileId, String originalName);
}
