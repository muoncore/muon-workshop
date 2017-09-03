package io.muoncore.workshop;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import io.muoncore.MultiTransportMuon;
import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.codec.json.JsonOnlyCodecs;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.discovery.MultiDiscovery;
import io.muoncore.eventstore.TestEventStore;
import io.muoncore.memory.discovery.InMemDiscovery;
import io.muoncore.memory.transport.InMemTransport;
import io.muoncore.protocol.event.ClientEvent;
import io.muoncore.protocol.event.Event;
import io.muoncore.protocol.event.client.DefaultEventClient;
import io.muoncore.protocol.event.client.EventClient;
import io.muoncore.protocol.event.client.EventReplayMode;
import io.muoncore.protocol.rpc.server.RpcServer;
import reactor.rx.broadcast.Broadcaster;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static io.muoncore.protocol.rpc.server.HandlerPredicates.*;

public class HelloJvm {

    static int numRequests = 0;

    public static void main(String[] args) throws IOException {
        inMemEventStore();

        Gson gson = new Gson();

        Muon muon = getMuon();

        muon.getDiscovery().blockUntilReady();

        EventClient client = new DefaultEventClient(muon);

        emit(client);

        Broadcaster<Event> s = Broadcaster.create();

        s.consume(event -> {
          System.out.println("EVENT: " + gson.toJson(event));
          numRequests++;
        });

        client.replay("requests", EventReplayMode.REPLAY_THEN_LIVE, s);

        RpcServer rpcServer = new RpcServer(muon);

        rpcServer.handleRequest(all(), requestWrapper -> {
            requestWrapper.ok(Collections.singletonMap("requests", numRequests));
        });
    }


    private static EventBus bus = new EventBus();
    public static final InMemDiscovery IN_MEM_DISCOVERY = new InMemDiscovery();

    private static Muon getMuon() {
        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("events-jvm").build();

        return new MultiTransportMuon(config, new MultiDiscovery(Arrays.asList(IN_MEM_DISCOVERY)), Arrays.asList(new InMemTransport(config, bus)), new JsonOnlyCodecs());
    }

    private static TestEventStore inMemEventStore() {

        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("in-mem-eventstore")
                .withTags("eventstore").build();

        return new TestEventStore(new MultiTransportMuon(config,
                new MultiDiscovery(
                        Arrays.asList(IN_MEM_DISCOVERY)),
                Arrays.asList(new InMemTransport(config, bus)), new JsonOnlyCodecs()));
    }


    private static void emit(EventClient cl) {
        for(int i=0; i<20; i++) {
            cl.event(
                    ClientEvent.ofType("Awesome")
                            .stream("requests")
                            .build()
            );
        }
    }
}
