package io.muoncore.workshop.newton

import io.muoncore.newton.AggregateRoot
import io.muoncore.newton.EventHandler
import io.muoncore.newton.StreamSubscriptionManager
import io.muoncore.newton.query.RebuildingDatastoreView
import io.muoncore.workshop.newton.user.User
import io.muoncore.workshop.newton.user.UserCreated
import org.springframework.stereotype.Component

@Component
class UserListView extends RebuildingDatastoreView {

    Map<String,String> users = [:]

    UserListView(StreamSubscriptionManager streamSubscriptionManager) throws IOException {
        super(streamSubscriptionManager)
    }

    @Override
    protected Collection<Class<? extends AggregateRoot>> aggregateRoots() {
        return [User]
    }

    @EventHandler
    void on(UserCreated ev) {
        users[ev.name] = ev.id
    }

    String findUserIdByName(String name) {
        users[name]
    }
}
