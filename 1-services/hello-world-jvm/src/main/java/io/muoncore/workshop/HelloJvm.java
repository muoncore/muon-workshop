package io.muoncore.workshop;

import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.protocol.reactivestream.server.PublisherLookup;
import reactor.core.processor.CancelException;
import reactor.rx.broadcast.Broadcaster;

import java.io.IOException;
import java.util.Collections;

import static io.muoncore.protocol.requestresponse.server.HandlerPredicates.path;

public class HelloJvm {

    public static void main(String[] args) throws IOException {
        Muon muon = getMuon();

        muon.getDiscovery().blockUntilReady();

        muon.handleRequest(path("/"), requestWrapper -> requestWrapper.ok("Hello World"));

        tickTock(muon);
    }

    private static void tickTock(Muon muon) {
        Broadcaster b = Broadcaster.create();

        muon.publishSource("/ticktock", PublisherLookup.PublisherType.HOT, b);

        Thread t = new Thread(() -> {
            while(true) {
                try {
                    try {
                        b.accept(Collections.singletonMap("time", "hello " + System.currentTimeMillis()));
                    } catch (CancelException e) {}
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }


    private static Muon getMuon() {
        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("hello-world-jvm")
                .addWriter( autoConfiguration -> {
        if (System.getenv().containsKey("MUON_URL")) {
            autoConfiguration.getProperties().put("amqp.transport.url", System.getenv().get("MUON_URL"));
            autoConfiguration.getProperties().put("amqp.discovery.url", System.getenv().get("MUON_URL"));
        }
        }).build();

        return MuonBuilder.withConfig(config).build();
    }
}
