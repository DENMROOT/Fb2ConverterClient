package com.denm.impl;

import static com.denm.service.impl.DownloadServiceImpl.RESOURCES_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Denys_Makarov on 12/29/2016.
 */
public class CompletableFutureTest extends TestBase {
    private File file1 = new File("src\\main\\resources\\Fb2\\12 Chairs.fb2");
    private File file2 = new File("src\\main\\resources\\Fb2\\Les Miserables.fb2");
    private File file3 = new File("src\\main\\resources\\Fb2\\Martin Iden.fb2");
    ExecutorService executor = Executors.newFixedThreadPool(4);

    @BeforeClass
    public static void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(RESOURCES_OUTPUT_DIR));
    }

    @Test
    public void test_ShouldConvertFB2ToEpubCorrectly() throws IOException {
        List<File> files = Arrays.asList(file1, file2, file3);

        files.parallelStream().forEach(this::runConvertTask);   // sequential
        files.parallelStream().forEach(this::runConvertTask);   // parallel by parallel Streams

//        files.forEach(file -> CompletableFuture.runAsync(() -> runConvertTask(file), executor));

    }

    private void runConvertTask(File file) {
        uploadService.upload(UPLOAD_URL, file)
                .thenCompose(uploadFeed -> convertService.convert(CONVERT_URL, uploadFeed.data.id, file.getName()))
                .thenAccept(convertFeed -> downloadService.download(DOWNLOAD_URL, convertFeed.file, file.getName()));
    }
}
