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

    public User(String name) {
        raiseEvent(new UserCreated(UUID.randomUUID().toString(), name));
    }

    public void userDidSomething(String text) {
        raiseEvent(new UserDidSomething(UUID.randomUUID().toString(), text));
    }

    @EventHandler
    public void on(UserCreated userCreated) {
        this.id = userCreated.getId();
        this.name = userCreated.getName();
    }

    @EventHandler
    public void on(UserDidSomething something) {
        somethings++;
    }
}
