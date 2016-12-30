package com.denm.impl;

import static com.denm.service.impl.DownloadServiceImpl.RESOURCES_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Denys_Makarov on 12/28/2016.
 */
public class DownloadServiceImplTest extends TestBase {

    @Before
    public static void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(RESOURCES_OUTPUT_DIR));
    }

    @Test
    public void test_uploadFileSuccessfully() throws IOException {
        downloadService.download(DOWNLOAD_URL,
                "d914c942-1af6-4d67-b6bc-fe64403d9de9.epub",
                "12 Стульев");
    }
}
