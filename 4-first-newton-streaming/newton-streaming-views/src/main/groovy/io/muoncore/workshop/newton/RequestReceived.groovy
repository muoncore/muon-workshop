package io.muoncore.workshop.newton

import io.muoncore.newton.NewtonEvent

class RequestReceived implements NewtonEvent {
    String id
    String text
}
