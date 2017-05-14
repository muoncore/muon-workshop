package io.muoncore.workshop.newton

import io.muoncore.newton.command.Command
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
class SendEmailCommand implements Command {

    String to
    String message

    @Override
    void execute() {
        println "========  SEND EMAIL =========="
        println "========  TO : ${to}"
        println "========  MSG: $message "
        println "========  END EMAIL =========="
    }
}
