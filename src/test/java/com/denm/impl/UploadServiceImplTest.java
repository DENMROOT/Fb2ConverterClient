package com.denm.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.denm.model.upload.UploadFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class UploadServiceImplTest extends TestBase {

    @Test
    public void test_uploadFileSuccessfully() throws IOException, ExecutionException, InterruptedException {
        File file = new File("src\\main\\resources\\Fb2\\12 Chairs.fb2");
        CompletableFuture<UploadFeed> uploadFeed = uploadService.upload(UPLOAD_URL,
                file);
        assertTrue(uploadFeed.get().success);
        assertNotNull(uploadFeed.get().data);
    }
}
