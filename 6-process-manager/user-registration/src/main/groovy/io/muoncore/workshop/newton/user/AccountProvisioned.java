package io.muoncore.workshop.newton.user;

import io.muoncore.newton.NewtonEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountProvisioned implements NewtonEvent<String> {
    private final String id = UUID.randomUUID().toString();
    private String userId;
}
