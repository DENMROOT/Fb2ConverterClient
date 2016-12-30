package com.denm.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import com.denm.model.convert.ConvertFeed;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class ConvertServiceImplTest extends TestBase {

    @Test
    public void test_uploadFileSuccessfully() throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<ConvertFeed> feed = convertService.convert(CONVERT_URL,
                "29129c49-757c-4600-80b2-0a8b90c30f2a",
                "12 Chairs.fb2");
        assertTrue(feed.get().success);
        assertNotNull(feed.get().file);
    }
}
