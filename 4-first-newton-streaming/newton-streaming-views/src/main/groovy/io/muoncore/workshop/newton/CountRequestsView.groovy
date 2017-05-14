package io.muoncore.workshop.newton

import io.muoncore.newton.EventHandler
import io.muoncore.newton.StreamSubscriptionManager
import io.muoncore.newton.query.RebuildingDatastoreView
import org.springframework.stereotype.Service

@Service
class CountRequestsView extends RebuildingDatastoreView {

    int requestMade = 0

    CountRequestsView(StreamSubscriptionManager streamSubscriptionManager) throws IOException {
        super(streamSubscriptionManager)
    }

    @Override
    protected String[] eventStreams() {
        return ["requests"] as String[]
    }

    @EventHandler
    def on(RequestReceived request) {
        println "REQUEST!"
        requestMade++
    }
}
