package io.muoncore.workshop.newton

import io.muoncore.newton.EventHandler
import io.muoncore.newton.StreamSubscriptionManager
import io.muoncore.newton.eventsource.muon.EventStreamProcessor
import io.muoncore.newton.query.NewtonView
import io.muoncore.newton.query.RebuildingDatastoreView
import io.muoncore.workshop.newton.user.UserCreated
import org.springframework.stereotype.Component

@Component
@NewtonView(streams = "user/User")
class UserListView extends RebuildingDatastoreView {

    Map<String,String> users = [:]

    UserListView(StreamSubscriptionManager streamSubscriptionManager, EventStreamProcessor eventStreamProcessor) throws IOException {
        super(streamSubscriptionManager, eventStreamProcessor)
    }

    @EventHandler
    public void on(UserCreated ev) {
        users[ev.name] = ev.id
    }
}
