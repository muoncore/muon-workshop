package io.muoncore.workshop.newton

import io.muoncore.newton.EnableNewton
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableNewton
class StreamingViewApplication {

    static void main(String[] args) {
        SpringApplication.run(StreamingViewApplication.class, args)
    }
}
