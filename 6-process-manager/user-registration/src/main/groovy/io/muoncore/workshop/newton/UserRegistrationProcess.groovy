package io.muoncore.workshop.newton

import groovy.util.logging.Slf4j
import io.muoncore.newton.EventHandler
import io.muoncore.newton.command.CommandIntent
import io.muoncore.newton.saga.SagaStreamConfig
import io.muoncore.newton.saga.StartSagaWith
import io.muoncore.newton.saga.StatefulSaga
import io.muoncore.workshop.newton.user.User
import io.muoncore.workshop.newton.user.UserApproved
import io.muoncore.workshop.newton.user.UserCreated
import io.muoncore.workshop.newton.user.UserConfirmed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope("prototype")
@Component
@SagaStreamConfig(aggregateRoots = [User])
@Slf4j
class UserRegistrationProcess extends StatefulSaga {

    private String userId
    private boolean confirmed = false
    private boolean approved = false

    @StartSagaWith
    void start(UserCreated created) {
        userId = created.id

        notifyOn(UserConfirmed, "userId", created.id)
        notifyOn(UserApproved, "userId", created.id)

        raiseCommand(CommandIntent.builder(
                SendEmailCommand.class.name)
                .request([
                "to"     : "someone@hello.com",
                "message": "Your account has been created! It will now be approved and provioned. Please confirm your account"
        ]).build())

        raiseCommand(CommandIntent.builder(
                SendEmailCommand.class.name)
                .request([
                "to"     : "admin@simple.com",
                "message": "A user has been created.... can you approve them now?"
        ]).build())
    }

    @EventHandler
    void on(UserConfirmed something) {
        confirmed = true;
        assessAccount()
    }

    @EventHandler
    void on(UserApproved userApproved) {
        approved = true
        assessAccount()
    }

    void assessAccount() {
        if (approved && confirmed) {
            log.info("Account ${userId} has been confirmed and approved. Process is completed")

            raiseCommand(CommandIntent.builder(
                    ProvisionAccountCommand.class.name)
                    .id(userId).build())

            raiseCommand(CommandIntent.builder(
                    SendEmailCommand.class.name)
                    .request([
                    "to"     : "someone@hello.com",
                    "message": "Your account is all ready to go!"
            ]).build())

            end()
        }
    }
}
