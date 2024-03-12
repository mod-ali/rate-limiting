/* Mo Ali created on 3/12/2024 inside the package - com.rate.limiting.ratelimiting */

package com.rate.limiting.ratelimiting;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@RestController
@RequestMapping("/api")
class RateLimitingController {

    private final Bucket rateLimiter;

    public RateLimitingController() {
        int limit = 10;

        Bandwidth bandwidthLimit = Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofSeconds(1)));
        this.rateLimiter = Bucket.builder()
                .addLimit(bandwidthLimit)
                .build();
    }

    @GetMapping("/items")
    public ResponseEntity<String> getItems(@RequestParam(defaultValue = "1") String numTokens) {
        int tokens;

        try {
            tokens = Integer.parseInt(numTokens);
        } catch (NumberFormatException nfe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect input, numTokens must be a number");
        }

        if (tokens <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect input, numTokens must be more than 0");
        }

        if (rateLimiter.tryConsume(tokens)) {
            return ResponseEntity.ok("List of items");
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

}