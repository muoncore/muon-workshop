package io.muoncore.workshop;

import com.google.gson.Gson;
import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.protocol.event.Event;
import io.muoncore.protocol.event.client.DefaultEventClient;
import io.muoncore.protocol.event.client.EventClient;
import io.muoncore.protocol.event.client.EventReplayMode;
import reactor.rx.broadcast.Broadcaster;

import java.io.IOException;

public class HelloJvm {

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        Muon muon = getMuon();

        muon.getDiscovery().blockUntilReady();

        EventClient client = new DefaultEventClient(muon);

        Broadcaster<Event> s = Broadcaster.create();

        s.consume(event -> {
          System.out.println("EVENT: " + gson.toJson(event));
        });

        client.replay("requests", EventReplayMode.REPLAY_THEN_LIVE, s);

    }


    private static Muon getMuon() {
        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("events-jvm")
                .addWriter( autoConfiguration -> {
        if (System.getenv().containsKey("MUON_URL")) {
            autoConfiguration.getProperties().put("amqp.transport.url", System.getenv().get("MUON_URL"));
            autoConfiguration.getProperties().put("amqp.discovery.url", System.getenv().get("MUON_URL"));
        }
        }).build();

        return MuonBuilder.withConfig(config).build();
    }
}
