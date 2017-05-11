package io.muoncore.workshop.newton

import io.muoncore.Muon
import io.muoncore.protocol.reactivestream.server.PublisherLookup
import io.reactivex.BackpressureStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static io.muoncore.protocol.requestresponse.server.HandlerPredicates.path

@Component
class ViewApi {

    @Autowired
    CountRequestsView search

    @Autowired
    StreamingCountRequestsView sub

    @Autowired
    Muon muon

    @PostConstruct
    def init() {
        muon.handleRequest(path("/")) {
            it.ok([
                    requests: search.requestMade
            ])
        }


        muon.publishSource("/", PublisherLookup.PublisherType.HOT, sub.subject.toFlowable(BackpressureStrategy.BUFFER))
    }
}
