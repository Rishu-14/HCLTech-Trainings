# Apache Camel Timer Component Project

This is a simple Apache Camel project demonstrating the use of the **Timer Component**.

## Project Overview

The Timer component in Apache Camel generates messages at specified intervals. This project includes multiple example timer routes:

1. **myTimer**: Triggers every 5 seconds
2. **delayedTimer**: Triggers every 10 seconds with a 10-second initial delay
3. **repeatTimer**: Triggers every 3 seconds but only 5 times

## Project Structure

```
src/
├── main/
│   ├── java/com/example/camel/
│   │   ├── CamelApplication.java    # Main Spring Boot application
│   │   └── TimerRoute.java          # Camel routes with timer component
│   └── resources/
│       └── application.properties   # Configuration file
```

## Building and Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/camel-timer-component-1.0.0.jar
```

## Timer Component Configuration

The Timer component supports the following options:

| Option | Description | Default |
|--------|-------------|---------|
| `period` | Interval in milliseconds between triggers | 1000 |
| `delay` | Initial delay before first trigger (ms) | 0 |
| `repeatCount` | Number of times to repeat (-1 for infinite) | -1 |
| `fixedRate` | Whether to use fixed-rate scheduling | false |

## Example Output

```
10:30:45.123 [myTimer] INFO com.example.camel.TimerRoute - Timer triggered at: 1234567890123
10:30:50.456 [myTimer] INFO com.example.camel.TimerRoute - Timer triggered at: 1234567895456
10:30:55.789 [repeatTimer] INFO com.example.camel.TimerRoute - Repeat timer - Execution #1
```

## Camel Routes Explanation

### Route 1: Basic Timer
```java
from("timer:myTimer?period=5000")
    .log("${body}");
```
Logs a message every 5 seconds indefinitely.

### Route 2: Delayed Timer
```java
from("timer:delayedTimer?period=10000&delay=10000")
    .log("${body}");
```
Waits 10 seconds before starting, then logs every 10 seconds.

### Route 3: Limited Repeat Count
```java
from("timer:repeatTimer?period=3000&repeatCount=5")
    .log("${body}");
```
Logs a message every 3 seconds, only 5 times total.

## Next Steps

You can extend this project by:
- Adding other Camel components (e.g., File, FTP, HTTP)
- Implementing error handling and retries
- Adding persistence or database integration
- Creating REST endpoints
- Implementing custom processors
- Adding unit tests

## References

- [Apache Camel Timer Component](https://camel.apache.org/components/latest/timer-component.html)
- [Camel Spring Boot](https://camel.apache.org/camel-spring-boot/latest/)
- [Camel Documentation](https://camel.apache.org/documentation/)
