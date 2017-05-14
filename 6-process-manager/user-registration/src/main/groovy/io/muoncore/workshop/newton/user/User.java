package io.muoncore.workshop.newton.user;

import io.muoncore.newton.AggregateRoot;
import io.muoncore.newton.EventHandler;
import io.muoncore.newton.eventsource.AggregateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AggregateConfiguration(context="user")
@Getter
@NoArgsConstructor
public class User extends AggregateRoot<String> {
    private String id;
    private String name;
    private int somethings = 0;
    private boolean provisioned = false;
    private boolean approved = false;

    public User(String name) {
        raiseEvent(new UserCreated(UUID.randomUUID().toString(), name));
    }

    public void confirmAccount() {
        raiseEvent(new UserConfirmed(id));
    }

    public void accountProvisioned() {
        raiseEvent(new AccountProvisioned(id));
    }

    public void approve() {
        raiseEvent(new UserApproved(UUID.randomUUID().toString(), id));
    }

    @EventHandler
    public void on(AccountProvisioned accountProvisioned) {
        provisioned = true;
    }

    @EventHandler
    public void on(UserApproved userApproved) {
        approved = true;
    }

    @EventHandler
    public void on(UserCreated userCreated) {
        this.id = userCreated.getId();
        this.name = userCreated.getName();
    }

    @EventHandler
    public void on(UserConfirmed something) {
        somethings++;
    }

}
