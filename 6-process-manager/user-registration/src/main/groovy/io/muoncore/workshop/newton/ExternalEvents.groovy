package io.muoncore.workshop.newton

import io.muoncore.newton.NewtonEvent

//integration event from an external service hooked into slack
class SlackMessage implements NewtonEvent<String> {
    String id
    String text
}

//domain event generated from slack
class AdminApprovedUserName {
    String id
    String userId
}
