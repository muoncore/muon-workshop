package io.muoncore.workshop.newton.user;

import io.muoncore.newton.NewtonEvent;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreated implements NewtonEvent<String> {
    private String id;
    private String name;
}
