package io.muoncore.workshop;

import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.codec.Codecs;
import io.muoncore.codec.DelegatingCodecs;
import io.muoncore.codec.MuonCodec;
import io.muoncore.codec.json.JsonOnlyCodecs;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.exception.MuonEncodingException;
import io.muoncore.protocol.reactivestream.server.PublisherLookup;
import io.muoncore.protocol.reactivestream.server.ReactiveStreamServer;
import io.muoncore.protocol.rpc.server.RpcServer;
import reactor.core.processor.CancelException;
import reactor.rx.broadcast.Broadcaster;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collections;

import static io.muoncore.protocol.rpc.server.HandlerPredicates.path;


public class HelloJvm {

    public static void main(String[] args) throws IOException {
        Muon muon = getMuon();

        muon.getDiscovery().blockUntilReady();

        RpcServer rpc = new RpcServer(muon);

        rpc.handleRequest(path("/"), requestWrapper -> {
                    System.out.println(requestWrapper.getRequest().getPayload(String.class));
                    requestWrapper.ok("Hello World");
                }
            );

        tickTock(muon);
    }

    private static void tickTock(Muon muon) {
        Broadcaster b = Broadcaster.create();

        ReactiveStreamServer rs = new ReactiveStreamServer(muon);

        rs.publishSource("/ticktock", PublisherLookup.PublisherType.HOT, b);

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


        return MuonBuilder
                .withConfig(config)
                .withCodecs(new DelegatingCodecs()
                        .withCodec(new StringCodec())
                        .withCodecs(new JsonOnlyCodecs()))
                .build();
    }

    static class StringCodec implements MuonCodec {

        @Override
        public <T> T decode(byte[] bytes, Type type) {
            if (type == String.class) {
                try {
                    return (T) new String(bytes, "UTF8");
                } catch (UnsupportedEncodingException e) {
                    throw new MuonEncodingException("Unable to convert to String", e);
                }
            }
            throw new MuonEncodingException("Unable to convert to String: " + type);
        }

        @Override
        public byte[] encode(Object o) throws UnsupportedEncodingException {
            if (o instanceof String) {
                String val = (String) o;
                return val.getBytes("UTF8");
            }
            throw new UnsupportedEncodingException("Unable to encode to text/plain of type " + o);
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public boolean canEncode(Class aClass) {
            return aClass.isAssignableFrom(String.class);
        }

        @Override
        public boolean hasSchemasFor(Class aClass) {
            return false;
        }

        @Override
        public Codecs.SchemaInfo getSchemaInfoFor(Class aClass) {
            return null;
        }
    }
}
