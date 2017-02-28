package com.denm.impl;

import static akka.pattern.Patterns.ask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Created by Denys_Makarov on 2/1/2017.
 */
public class AkkaActorTest {

    @Before
    public void init(){
    }

    @Test
    public void actorTest() throws InterruptedException {
        final ActorSystem system = ActorSystem.create("MyTestSystem");
        final ActorRef myActor = system.actorOf(Props.create(AnswerActor.class), "answerActor");

//        // Simple actors messaging
//        myActor.tell("Hello!", null);
//        myActor.tell("Jack", null);
//
//        system.shutdown();
//        System.out.println("EXIT HoHoHo !!!");


        final Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));

        final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        futures.add(ask(myActor, "Hello!", 1000)); // using 1000ms timeout
        futures.add(ask(myActor, "Jack", t)); // using timeout from above

        final OnComplete<String> printFunc = new
                OnComplete<String>() {
                    @Override public void onComplete(final Throwable exception,
                                                     final String result) {
                        System.out.println(result);
                    }
                };

        final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
                system.dispatcher());


        final Future<String> transformed = aggregate.map(
                new Mapper<Iterable<Object>, String>() {
                    public String apply(Iterable<Object> coll) {
                        final Iterator<Object> it = coll.iterator();
                        final String x = (String) it.next();
                        final String s = (String) it.next();
                        return x + " " + s;
                    }
                }, system.dispatcher()
        );

        transformed.onComplete(printFunc, system.dispatcher());

        system.shutdown();

    }
}

class AnswerActor extends UntypedActor{

    @Override
    public void onReceive(Object message) throws Throwable {

        if (message instanceof String) {
            String messageStr = (String) message;
            switch(messageStr) {
                case "Hello!":
                    getSender().tell(message + " - " + "What is your name ?", getSelf());
                    break;

                default:
                    getSender().tell(message + " - " + "Hello " + messageStr + " !", getSelf());
                    break;
            }
        }
    }
}