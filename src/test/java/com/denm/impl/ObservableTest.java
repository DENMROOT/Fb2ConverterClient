package com.denm.impl;

import static com.denm.completablefuture.service.impl.DownloadServiceImpl.RESOURCES_OUTPUT_DIR;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denm.model.convert.ConvertFeed;
import com.denm.model.upload.UploadFeed;
import com.denm.observable.service.ConvertService;
import com.denm.observable.service.DownloadService;
import com.denm.observable.service.UploadService;
import com.denm.observable.service.impl.ConvertServiceImpl;
import com.denm.observable.service.impl.DownloadServiceImpl;
import com.denm.observable.service.impl.UploadServiceImpl;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Denys_Makarov on 1/27/2017.
 */
public class ObservableTest extends TestBase{
    private static UploadService uploadService;
    private static ConvertService convertService;
    private static DownloadService downloadService;

    @BeforeClass
    public static void init(){
        uploadService = new UploadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        convertService = new ConvertServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
        downloadService = new DownloadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
    }

    @BeforeClass
    public static void cleanup() throws IOException {
        FileUtils.cleanDirectory(new File(RESOURCES_OUTPUT_DIR));
    }

    @Test
    public void test_ShouldConvertFB2ToEpubCorrectly() throws IOException {
        ExecutorService pool = newFixedThreadPool(10);
        Scheduler scheduler = Schedulers.from(pool);

        Observable<UploadFeed> uploads = Observable.just(file1, file2, file3)
                .flatMap(i -> uploadFile(i).subscribeOn(scheduler));

        Observable<ConvertFeed> converts = uploads
                .flatMap(i -> convertFile(i.data.id, i.data.fileName).subscribeOn(scheduler));

        converts
                .doOnNext(i -> download(i.file, i.fileName).subscribeOn(scheduler))
                .blockingSubscribe();
    }

    Observable<UploadFeed> uploadFile(File file) {
        return Observable.fromCallable(() -> upload(file));
    }

    UploadFeed upload(File file) {
        return uploadService.upload(UPLOAD_URL, file);
    }

    Observable<ConvertFeed> convertFile(String id, String fileName) {
        return Observable.fromCallable(() -> convert(id, fileName));
    }

    ConvertFeed convert(String id, String fileName) {
        return convertService.convert(CONVERT_URL, id, fileName);
    }

    Observable<Void> download(String id, String fileName) {
        downloadService.download(DOWNLOAD_URL, id, fileName);
        return Observable.empty();
    }
}
