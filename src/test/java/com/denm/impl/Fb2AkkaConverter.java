package com.denm.impl;

import static com.denm.impl.TestBase.HTTP_TRANSPORT;
import static com.denm.impl.TestBase.JSON_FACTORY;

import java.io.File;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import com.denm.model.convert.ConvertFeed;
import com.denm.model.upload.UploadFeed;
import com.denm.observable.service.ConvertService;
import com.denm.observable.service.DownloadService;
import com.denm.observable.service.UploadService;
import com.denm.observable.service.impl.ConvertServiceImpl;
import com.denm.observable.service.impl.DownloadServiceImpl;
import com.denm.observable.service.impl.UploadServiceImpl;

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

class ConverterManagerActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(
            getContext().system(), this);

    private ActorRef uploadWorkers = this.getContext().actorOf(Props.create(UploadActor.class)
            .withRouter(new FromConfig()), "uploadRouter");
    private ActorRef convertWorkers = this.getContext().actorOf(Props.create(ConvertActor.class)
            .withRouter(new FromConfig()), "converterRouter");
    private ActorRef downloadWorkers = this.getContext().actorOf(Props.create(DownloadActor.class)
            .withRouter(new FromConfig()), "downloadRouter");

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof UploadMessage) {
            log.info("Upload message Received by Converter Manager Actor ->{}", ((UploadMessage) message).getFile());
            uploadWorkers.tell(message, self());
        } else if (message instanceof ConvertMessage) {
            log.info("Convert message Received by Converter Manager Actor ->{}", ((ConvertMessage) message).getOriginalName());
            convertWorkers.tell(message, self());
        } else if (message instanceof DownloadMessage) {
            log.info("Download message Received by Converter Manager Actor ->{}", ((DownloadMessage) message).getOriginalName());
            downloadWorkers.tell(message, self());
        } else if (message instanceof CompletedMessage) {
            log.info("COMPLETED message Received by Converter Manager Actor ->{}", ((CompletedMessage) message).getOriginalName());
            getSender().tell(message, self());
        }
        else
            unhandled(message);
    }
}

class UploadActor extends UntypedActor {
    private UploadService uploadService =  new UploadServiceImpl(HTTP_TRANSPORT, JSON_FACTORY);

    private LoggingAdapter log = Logging.getLogger(
            getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof UploadMessage) {
            log.info("Upload message Received by UPLOAD Actor ->{}", message);
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
            log.info("Convert message Received by CONVERT Actor ->{}", message);
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
            log.info("Download message Received by DOWNLOAD Actor ->{}", message);
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
