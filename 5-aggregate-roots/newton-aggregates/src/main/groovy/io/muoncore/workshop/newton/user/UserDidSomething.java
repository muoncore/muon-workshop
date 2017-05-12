package io.muoncore.workshop.newton.user;

import io.muoncore.newton.NewtonEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by david on 12/05/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDidSomething implements NewtonEvent<String> {
    private final String id = UUID.randomUUID().toString();
    private String userId;
    private String text;
}
