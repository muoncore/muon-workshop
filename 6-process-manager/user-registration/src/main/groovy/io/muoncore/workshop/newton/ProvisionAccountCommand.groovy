package io.muoncore.workshop.newton

import groovy.util.logging.Slf4j
import io.muoncore.newton.command.Command
import io.muoncore.newton.eventsource.EventSourceRepository
import io.muoncore.workshop.newton.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
@Slf4j
class ProvisionAccountCommand implements Command {

    @Autowired
    EventSourceRepository<User> repo
    String id

    @Override
    void execute() {
        log.info("Account ${id} will be marked as provisioned")
        User user = repo.load(id)
        user.accountProvisioned()
        repo.save(user)
    }
}
