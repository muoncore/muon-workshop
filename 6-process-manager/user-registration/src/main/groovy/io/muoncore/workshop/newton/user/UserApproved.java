package io.muoncore.workshop.newton.user;

import io.muoncore.newton.NewtonEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApproved implements NewtonEvent<String> {
    private String id;
    private String userId;
}
