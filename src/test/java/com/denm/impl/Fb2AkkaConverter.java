package com.denm.impl;

import static com.denm.impl.TestBase.HTTP_TRANSPORT;
import static com.denm.impl.TestBase.JSON_FACTORY;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.denm.model.convert.ConvertFeed;
import com.denm.model.upload.UploadFeed;
import com.denm.observable.service.ConvertService;
import com.denm.observable.service.DownloadService;
import com.denm.observable.service.UploadService;
import com.denm.observable.service.impl.ConvertServiceImpl;
import com.denm.observable.service.impl.DownloadServiceImpl;
import com.denm.observable.service.impl.UploadServiceImpl;
import scala.concurrent.duration.Duration;

/**
 * Created by Denys_Makarov on 2/27/2017.
 */
public class Fb2AkkaConverter extends TestBase{

    @Test
    public void should_convert(){
        final ActorSystem system = ActorSystem.create("FB2Converter");
        final ActorRef converterManager = system.actorOf(Props.create(ConverterManagerActor.class), "converterManager");

        converterManager.tell(new UploadMessage(file1), ActorRef.noSender());
        converterManager.tell(new UploadMessage(file2), ActorRef.noSender());
        converterManager.tell(new UploadMessage(file3), ActorRef.noSender());

        system.awaitTermination();
    }
}

class ConverterManagerActor extends AbstractLoggingActor {
    private static final SupervisorStrategy STRATEGY = new OneForOneStrategy(
            3,
            Duration.create(10, TimeUnit.SECONDS),
            DeciderBuilder
                    .match(RuntimeException.class, ex -> SupervisorStrategy.restart())
                    .build()
    );

    private ActorRef uploadWorkers = this.getContext().actorOf(Props.create(UploadActor.class)
            .withDispatcher("blocking-dispatcher")
            .withRouter(new FromConfig()), "uploadRouter");
    private ActorRef convertWorkers = this.getContext().actorOf(Props.create(ConvertActor.class)
            .withDispatcher("blocking-dispatcher")
            .withRouter(new FromConfig()), "converterRouter");
    private ActorRef downloadWorkers = this.getContext().actorOf(Props.create(DownloadActor.class)
            .withDispatcher("blocking-dispatcher")
            .withRouter(new FromConfig()), "downloadRouter");

    public ConverterManagerActor() {
        receive(ReceiveBuilder
                .match(UploadMessage.class, this::onUpload)
                .match(ConvertMessage.class, this::onConvert)
                .match(DownloadMessage.class, this::onDownload)
                .match(CompletedMessage.class, this::onCompleted)
                .build());
    }

    private void onUpload(UploadMessage uploadMessage) {
        log().info("Upload message Received by Converter Manager Actor -> {}", uploadMessage.getFile());
        uploadWorkers.tell(uploadMessage, self());
    }

    private void onConvert(ConvertMessage convertMessage) {
        log().info("Convert message Received by Converter Manager Actor -> {}", convertMessage.getOriginalName());
        convertWorkers.tell(convertMessage, self());
    }

    private void onDownload(DownloadMessage downloadMessage) {
        log().info("Download message Received by Converter Manager Actor -> {}", downloadMessage.getOriginalName());
        downloadWorkers.tell(downloadMessage, self());
    }

    private void onCompleted(CompletedMessage completedMessage) {
        log().info("COMPLETED message Received by Converter Manager Actor -> {}", completedMessage.getOriginalName());
        context().sender().tell(completedMessage, self());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }
}

class UploadActor extends UntypedActor {
    private UploadService uploadService =  new UploadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);

    private LoggingAdapter log = Logging.getLogger(
            getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof UploadMessage) {
            log.info("Upload message Received by UPLOAD Actor -> {}", message);
            UploadFeed uploadFeed = uploadService.upload(((UploadMessage) message).getFile());
            getSender().tell(new ConvertMessage(uploadFeed.data.id, uploadFeed.data.fileName), self());
        }
        else
            unhandled(message);
    }
}

class ConvertActor extends UntypedActor {
    private ConvertService convertService =  new ConvertServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
    private LoggingAdapter log = Logging.getLogger(
            getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ConvertMessage) {
            ConvertMessage msg = ((ConvertMessage) message);
            log.info("Convert message Received by CONVERT Actor -> {}", message);
            ConvertFeed convertFeed = convertService.convert(
                    msg.getFileId(),
                    msg.getOriginalName());
            getSender().tell(new DownloadMessage(convertFeed.file, convertFeed.fileName), self());
        }
        else
            unhandled(message);
    }

}
class DownloadActor extends UntypedActor {
    private DownloadService downloadService =  new DownloadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);
    private LoggingAdapter log = Logging.getLogger(
            getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof DownloadMessage) {
            DownloadMessage msg = (DownloadMessage) message;
            log.info("Download message Received by DOWNLOAD Actor -> {}", message);
            downloadService.download(
                    msg.getFileId(),
                    msg.getOriginalName());
            getSender().tell(new CompletedMessage(msg.getOriginalName()), self());
        }
        else
            unhandled(message);
    }

}

class UploadMessage {
    private final File file;

    public UploadMessage(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

class ConvertMessage {
    private final String fileId;
    private final String originalName;

    public ConvertMessage(String fileId, String originalName) {
        this.fileId = fileId;
        this.originalName = originalName;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOriginalName() {
        return originalName;
    }
}

class DownloadMessage {
    private final String fileId;
    private final String originalName;

    public DownloadMessage(String fileId, String originalName) {
        this.fileId = fileId;
        this.originalName = originalName;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOriginalName() {
        return originalName;
    }
}

class CompletedMessage {
    private final String originalName;

    public CompletedMessage(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalName() {
        return originalName;
    }
}
