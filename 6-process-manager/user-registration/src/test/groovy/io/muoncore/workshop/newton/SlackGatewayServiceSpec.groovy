package io.muoncore.workshop.newton

import io.muoncore.newton.StreamSubscriptionManager
import io.muoncore.newton.eventsource.EventSourceRepository
import io.muoncore.workshop.newton.user.User
import spock.lang.Specification

class SlackGatewayServiceSpec extends Specification {

    def "picks up name from message"() {

        def svc = new SlackGatewayService(Mock(StreamSubscriptionManager))
        svc.userList = Mock(UserListView)
        svc.userRepo = Mock(EventSourceRepository)

        def message = new SlackMessage(text: "approve someone")
        def user = new User()
        when:
        svc.approveUsersFromSlack(message)

        then:
        1 * svc.userList.findUserIdByName("someone") >> "1234"
        1 * svc.userRepo.load("1234") >> user
        1 * svc.userRepo.save(user)
    }

}
