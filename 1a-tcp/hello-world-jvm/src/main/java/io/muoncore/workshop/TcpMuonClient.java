package io.muoncore.workshop;

import io.muoncore.Muon;
import io.muoncore.MuonBuilder;
import io.muoncore.codec.DelegatingCodecs;
import io.muoncore.codec.json.JsonOnlyCodecs;
import io.muoncore.config.AutoConfiguration;
import io.muoncore.config.MuonConfigBuilder;
import io.muoncore.protocol.rpc.Response;
import io.muoncore.protocol.rpc.client.RpcClient;
import io.muoncore.protocol.rpc.server.RpcServer;
import io.muoncore.transport.tcp.TCPTransportFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TcpMuonClient {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Muon muon = getMuon();

        RpcClient rpc = new RpcClient(muon);

        Response response = rpc.request("rpc://hello-world-jvm/", "My Message is cool").get();

        String payload = response.getPayload(String.class);

        System.out.println("Got data " + payload);

    }

    private static Muon getMuon() {
        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("hello-world-jvm-client")
                .addWriter( autoConfiguration -> {
                    //use the TCP transport
                    autoConfiguration.getProperties().put("muon.transport.factories", TCPTransportFactory.class.getCanonicalName());
                }).build();

        return MuonBuilder
                .withConfig(config)
                .withCodecs(new DelegatingCodecs()
                        .withCodec(new HelloJvm.StringCodec())
                        .withCodecs(new JsonOnlyCodecs()))
                .build();
    }
}
