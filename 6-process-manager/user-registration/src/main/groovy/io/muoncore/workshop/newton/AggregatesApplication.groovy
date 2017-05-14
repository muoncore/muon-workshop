package io.muoncore.workshop.newton

import io.muoncore.newton.EnableNewton
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableNewton
class AggregatesApplication {

    static void main(String[] args) {
        SpringApplication.run(AggregatesApplication.class, args)
    }
}
