package com.denm.impl;

import static com.denm.completablefuture.service.impl.DownloadServiceImpl.RESOURCES_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denm.completablefuture.service.ConvertService;
import com.denm.completablefuture.service.DownloadService;
import com.denm.completablefuture.service.UploadService;
import com.denm.completablefuture.service.impl.ConvertServiceImpl;
import com.denm.completablefuture.service.impl.DownloadServiceImpl;
import com.denm.completablefuture.service.impl.UploadServiceImpl;

/**
 * Created by Denys_Makarov on 12/29/2016.
 */
public class CompletableFutureTest extends TestBase {

    private static UploadService uploadService;
    private static ConvertService convertService;
    private static DownloadService downloadService;

    @BeforeClass
    public static void cleanup() throws IOException {
        uploadService = new UploadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        convertService = new ConvertServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        downloadService = new DownloadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);

        FileUtils.cleanDirectory(new File(RESOURCES_OUTPUT_DIR));
    }

    @Test
    public void test_ShouldConvertFB2ToEpubCorrectly() throws IOException {
        List<File> files = Arrays.asList(file1, file2, file3);

//        files.parallelStream().forEach(this::runConvertTask);   // sequential
        files.parallelStream().forEach(this::runConvertTask);   // parallel by parallel Streams

    }

    private void runConvertTask(File file) {
        uploadService.upload(UPLOAD_URL, file)
                .thenCompose(uploadFeed -> convertService.convert(CONVERT_URL, uploadFeed.data.id, file.getName()))
                .thenAccept(convertFeed -> downloadService.download(DOWNLOAD_URL, convertFeed.file, file.getName()));
    }
}
