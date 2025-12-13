package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel route using Timer component
 * This route triggers every 5 seconds and logs a message
 */
@Component
public class TimerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Timer Route - triggers every 5 seconds
        from("timer:myTimer?period=5000")
            .setBody(constant("Timer triggered at: " + System.currentTimeMillis()))
            .log("${body}")
            .to("log:timerLog");

        // Alternative Timer Route - triggers once after 10 seconds delay
        from("timer:delayedTimer?period=10000&delay=10000")
            .setBody(constant("Delayed timer executed!"))
            .log("${body}");

        // Timer Route with repeat count (executes 5 times)
        from("timer:repeatTimer?period=3000&repeatCount=5")
            .setBody(constant("Repeat timer - Execution #${header.CamelTimerCounter}"))
            .log("${body}");
    }
}
