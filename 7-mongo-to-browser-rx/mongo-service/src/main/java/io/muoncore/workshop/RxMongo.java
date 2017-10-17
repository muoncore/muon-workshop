package io.muoncore.workshop;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.protocol.reactivestream.server.PublisherLookup;
import io.muoncore.protocol.reactivestream.server.ReactiveStreamServer;
import io.muoncore.protocol.rpc.server.RpcServer;
import io.reactivex.Flowable;

import java.io.IOException;
import java.util.Collections;

import static io.muoncore.protocol.rpc.server.HandlerPredicates.path;


public class RxMongo {

    static MongoClient mongoClient = MongoClients.create();

    public static void main(String[] args) throws IOException {
        Muon muon = getMuon();

        muon.getDiscovery().blockUntilReady();

        RpcServer rpc = new RpcServer(muon);

        rpc.handleRequest(path("/"), requestWrapper -> {
                    requestWrapper.ok(Collections.singleton("Hello WOrld"));
                }
        );

        mongoQuery(muon);
    }

    private static void mongoQuery(Muon muon) {

        ReactiveStreamServer rs = new ReactiveStreamServer(muon);

        rs.publishGeneratedSource("/big-query", PublisherLookup.PublisherType.COLD, subscriptionRequest ->
                Flowable.fromPublisher(
                        mongoClient.getDatabase("photon-events").getCollection("eventRecord").find())
                        .map(document -> document.get("eventType")));
    }

    private static Muon getMuon() {
        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("rx-mongo")
                .addWriter(autoConfiguration -> {
                    if (System.getenv().containsKey("MUON_URL")) {
                        autoConfiguration.getProperties().put("amqp.transport.url", System.getenv().get("MUON_URL"));
                        autoConfiguration.getProperties().put("amqp.discovery.url", System.getenv().get("MUON_URL"));
                    }
                }).build();


        return MuonBuilder
                .withConfig(config)
                .build();
    }
}
