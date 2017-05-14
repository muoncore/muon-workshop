package io.muoncore.workshop.newton

import groovy.util.logging.Slf4j
import io.muoncore.newton.EventHandler
import io.muoncore.newton.StreamSubscriptionManager
import io.muoncore.newton.domainservice.EventDrivenDomainService
import io.muoncore.newton.eventsource.EventSourceRepository
import io.muoncore.workshop.newton.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
@Slf4j
class SlackGatewayService extends EventDrivenDomainService {

    @Autowired
    UserListView userList

    @Autowired
    EventSourceRepository<User> userRepo

    SlackGatewayService(StreamSubscriptionManager streamSubscriptionManager) throws IOException {
        super(streamSubscriptionManager)
    }

    @Override
    protected String[] eventStreams() {
        return ["slack"]
    }

    @EventHandler
    void approveUsersFromSlack(SlackMessage message) {
        if (message.text.startsWith("approve")) {
            def username = message.text.trim()[7..-1]?.trim()
            log.info("Loading user record ${username}")
            def id = userList.findUserIdByName(username)
            User user = userRepo.load(id)
            user.approve()
            userRepo.save(user)
        }
    }
}
