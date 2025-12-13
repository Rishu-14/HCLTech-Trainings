# Apache Camel Timer Component - Detailed Explanation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Project Architecture](#project-architecture)
3. [Detailed Code Explanation](#detailed-code-explanation)
4. [Project Flow](#project-flow)
5. [Execution Timeline](#execution-timeline)

---

## Project Overview

**What is Apache Camel?**
Apache Camel is an open-source integration framework that makes it easy to integrate various applications using different protocols and technologies. It uses a **route-based** approach to define how data flows between different systems.

**What is a Timer Component?**
The Timer component generates messages at specified time intervals. It's useful for:
- Periodic polling of resources
- Scheduled tasks and cron-like operations
- Testing and development
- Triggering workflows at regular intervals

**Our Project Purpose:**
This project demonstrates how to use Apache Camel's Timer component with Spring Boot to create scheduled tasks that execute at different intervals.

---

## Project Architecture

```
┌─────────────────────────────────────────────┐
│        Spring Boot Application              │
│    (CamelApplication - Entry Point)         │
└────────────┬────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────┐
│     Camel Context (Routing Engine)          │
│  - Manages all routes                       │
│  - Processes messages                       │
│  - Coordinates components                   │
└─────┬──────────┬──────────┬─────────────────┘
      │          │          │
      ▼          ▼          ▼
┌──────────┐ ┌─────────┐ ┌──────────┐
│ Timer 1  │ │ Timer 2 │ │ Timer 3  │
│ (5 sec)  │ │(10 sec) │ │(3 sec,5x)│
└────┬─────┘ └────┬────┘ └────┬─────┘
     │            │            │
     ▼            ▼            ▼
┌──────────────────────────────────────┐
│        Message Processing            │
│  - Set body content                  │
│  - Log messages                      │
│  - Route to endpoints                │
└──────────────────────────────────────┘
```

---

## Detailed Code Explanation

### 1. **pom.xml** - Maven Configuration File

#### **Lines 1-7: XML Declaration & Project Definition**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
```
- **Line 1**: XML declaration stating this is an XML file with UTF-8 encoding
- **Lines 2-4**: Declares this is a Maven POM (Project Object Model) file
- **Line 5**: XML schema location for validation
- **Line 6**: Specifies Maven version 4.0.0 format

#### **Lines 8-14: Project Identity**
```xml
    <groupId>com.example</groupId>
    <artifactId>camel-timer-component</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Camel Timer Component</name>
    <description>Apache Camel Project with Timer Component</description>
```
- **groupId**: Package namespace (like domain name for Java)
- **artifactId**: Project name (how it's identified in Maven)
- **version**: Current version (follows semantic versioning)
- **packaging**: Type of output (jar = executable JAR file)
- **name/description**: Human-readable project information

#### **Lines 16-22: Build Properties**
```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <camel.version>3.20.0</camel.version>
    </properties>
```
- **sourceEncoding**: All source files use UTF-8 encoding
- **compiler.source**: Code written in Java 11 syntax
- **compiler.target**: Compiled code runs on Java 11
- **camel.version**: All Camel dependencies use version 3.20.0 (consistency)

#### **Lines 24-57: Dependencies**
```xml
    <dependencies>
        <!-- Camel Spring Boot Starter -->
        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-spring-boot-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
```
**Camel Spring Boot Starter**
- Integrates Apache Camel with Spring Boot
- Provides auto-configuration
- Enables Camel routes to work as Spring components
- Makes timer components available

```xml
        <!-- Camel Timer Component -->
        <dependency>
            <groupId>org.apache.camel.springboot</groupId>
            <artifactId>camel-timer-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
```
**Timer Starter**
- Adds the Timer component to Camel
- Allows `timer:` endpoints in routes
- Without this, timer routes would fail

```xml
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.7.12</version>
        </dependency>
```
**Spring Boot Starter**
- Core Spring Boot framework
- Provides embedded application server
- Auto-configuration capabilities
- Main application runtime

```xml
        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.36</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.11</version>
        </dependency>
```
**SLF4J & Logback**
- SLF4J: Standard logging facade (abstraction layer)
- Logback: Actual logging implementation
- Allows us to use `.log()` in routes to output messages

#### **Lines 59-78: Build Plugins**
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
```
**Maven Compiler Plugin**
- Compiles Java source code to bytecode
- Source/target Java 11 ensures compatibility

```xml
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.7.12</version>
            </plugin>
```
**Spring Boot Maven Plugin**
- Enables `mvn spring-boot:run` command
- Packages app as executable JAR
- Simplifies development and deployment

---

### 2. **CamelApplication.java** - Application Entry Point

#### **Lines 1-4: Package & Imports**
```java
package com.example.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
```
- **package**: Groups this class in `com.example.camel` namespace
- **import SpringApplication**: Main Spring Boot launcher class
- **import SpringBootApplication**: Annotation that enables Spring Boot magic

#### **Lines 6-11: Class Definition & Main Method**
```java
@SpringBootApplication
public class CamelApplication {
    public static void main(String[] args) {
        SpringApplication.run(CamelApplication.class, args);
    }
}
```

**@SpringBootApplication Annotation:**
- Enables auto-configuration
- Scans for Spring components
- Configures Spring Application context
- Enables Camel integration

**public static void main():**
- Entry point of the Java application
- Called when you run `java -jar app.jar`
- `args`: command-line arguments (ignored here)

**SpringApplication.run():**
- Creates the Spring Application context
- Starts the embedded Tomcat server
- Initializes Camel routes
- Starts the application

**Execution Flow:**
```
JVM starts
    ↓
main() method called
    ↓
SpringApplication.run()
    ↓
Spring Boot auto-configuration activates
    ↓
Scans for @Component classes (finds TimerRoute)
    ↓
Instantiates TimerRoute
    ↓
Calls TimerRoute.configure()
    ↓
Camel routes start (timers begin firing)
    ↓
Application runs indefinitely
```

---

### 3. **TimerRoute.java** - Route Configuration

#### **Lines 1-4: Package & Imports**
```java
package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
```
- **RouteBuilder**: Camel class for defining routes using fluent API
- **@Component**: Spring annotation to register this as a managed bean

#### **Lines 9-12: Class Declaration**
```java
@Component
public class TimerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
```

**@Component Annotation:**
- Tells Spring to instantiate this class
- Spring will manage the lifecycle
- Called by Spring Boot during startup

**extends RouteBuilder:**
- Inherits Camel's routing capabilities
- Provides methods like `from()`, `to()`, `.log()`

**public void configure():**
- Must be implemented (abstract method from RouteBuilder)
- Called by Camel after instantiation
- Defines all routes here
- `throws Exception`: Can throw exceptions during configuration

---

#### **Route 1: Basic Timer (Lines 14-18)**

```java
        from("timer:myTimer?period=5000")
            .setBody(constant("Timer triggered at: " + System.currentTimeMillis()))
            .log("${body}")
            .to("log:timerLog");
```

**Step-by-step execution:**

1. **`from("timer:myTimer?period=5000")`**
   - **from()**: Start of a Camel route (producer endpoint)
   - **timer:myTimer**: Timer component with name "myTimer"
   - **?period=5000**: Configuration parameter (fires every 5000 milliseconds = 5 seconds)
   - **Creates a message every 5 seconds**

2. **`.setBody(constant(...))`**
   - **setBody()**: Sets the message content (body)
   - **constant()**: Static value that doesn't change
   - **System.currentTimeMillis()**: Current time in milliseconds since epoch
   - **Result**: Message body = "Timer triggered at: 1765631341514" (example)

3. **`.log("${body}")`**
   - **log()**: Logs the message
   - **${body}**: Expression language syntax to access message body
   - **Logs to console**: Shows the message content

4. **`.to("log:timerLog")`**
   - **to()**: Send message to another endpoint
   - **log:timerLog**: Another log endpoint with name "timerLog"
   - **Double logging**: Both `.log()` and `.to("log:...")` log the message

**Complete Flow:**
```
Timer fires every 5 seconds
    ↓
Creates new message with counter
    ↓
Sets body: "Timer triggered at: 1765631341514"
    ↓
Logs: "Timer triggered at: 1765631341514"
    ↓
Sends to timerLog endpoint (logs again with more details)
```

---

#### **Route 2: Delayed Timer (Lines 20-23)**

```java
        from("timer:delayedTimer?period=10000&delay=10000")
            .setBody(constant("Delayed timer executed!"))
            .log("${body}");
```

**Configuration breakdown:**
- **timer:delayedTimer**: Timer name
- **period=10000**: Fires every 10 seconds
- **delay=10000**: Waits 10 seconds BEFORE first trigger

**Timeline:**
```
Time 0s:    Application starts, route registered
Time 0-10s: Waiting... (delay=10000ms)
Time 10s:   First execution! Logs "Delayed timer executed!"
Time 20s:   Second execution! Logs "Delayed timer executed!"
Time 30s:   Third execution! Logs "Delayed timer executed!"
(continues indefinitely)
```

**Different from Route 1:**
- Route 1 starts immediately (no delay)
- Route 2 waits 10 seconds before first trigger
- Both continue indefinitely (no repeat limit)

---

#### **Route 3: Limited Repeat Timer (Lines 25-28)**

```java
        from("timer:repeatTimer?period=3000&repeatCount=5")
            .setBody(constant("Repeat timer - Execution #${header.CamelTimerCounter}"))
            .log("${body}");
```

**Configuration breakdown:**
- **timer:repeatTimer**: Timer name
- **period=3000**: Fires every 3 seconds
- **repeatCount=5**: Stops after 5 executions

**Special feature - Header Access:**
```
${header.CamelTimerCounter}
```
- **header**: Message headers (metadata)
- **CamelTimerCounter**: Camel's built-in counter (1, 2, 3, 4, 5...)
- **Result**: "Repeat timer - Execution #1", "Repeat timer - Execution #2", etc.

**Timeline:**
```
Time 0-3s:   Waiting...
Time 3s:     Execution #1 → Logs "Repeat timer - Execution #1"
Time 6s:     Execution #2 → Logs "Repeat timer - Execution #2"
Time 9s:     Execution #3 → Logs "Repeat timer - Execution #3"
Time 12s:    Execution #4 → Logs "Repeat timer - Execution #4"
Time 15s:    Execution #5 → Logs "Repeat timer - Execution #5"
Time 18s+:   STOPS! No more executions
```

---

### 4. **application.properties** - Configuration File

#### **Lines 1-2: Application Identity**
```properties
spring.application.name=camel-timer-component
server.port=8080
```
- **spring.application.name**: Application identifier (for logs, monitoring)
- **server.port**: Tomcat HTTP server listens on port 8080
- (Note: Our app doesn't expose HTTP endpoints, but server still runs)

#### **Lines 4-5: Camel Configuration**
```properties
camel.springboot.main-run-controller=true
camel.springboot.use-mdc-logging=true
```
- **main-run-controller=true**: Keeps main thread alive (routes run indefinitely)
- **use-mdc-logging=true**: MDC = Mapped Diagnostic Context (adds thread/context info to logs)

#### **Lines 7-10: Logging Configuration**
```properties
logging.level.root=INFO
logging.level.org.apache.camel=INFO
logging.level.org.springframework=INFO
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

**Logging Levels:**
- **root=INFO**: Root logger shows INFO and above (INFO, WARN, ERROR)
- **org.apache.camel=INFO**: Camel logs at INFO level
- **org.springframework=INFO**: Spring logs at INFO level
- (DEBUG level would show more details but spam the console)

**Log Pattern:**
```
%d{HH:mm:ss.SSS}    → Time: 18:39:00.316
[%thread]           → Thread: [main] or [Camel (camel-1) thread #1 - timer://myTimer]
%-5level            → Level: INFO, WARN, ERROR (left-aligned, 5 chars)
%logger{36}         → Logger class (shortened to 36 chars)
%msg%n              → Message + newline

Example Output:
18:39:00.316 [Camel (camel-1) thread #1 - timer://myTimer] INFO route1 - Timer triggered at: 1765631341514
```

---

## Simple Project Flow (Easy Understanding)

### **What Happens When You Run the Project?**

```
STEP 1: You Type "mvn spring-boot:run"
           ↓
STEP 2: Application Starts
           ↓
STEP 3: Three Timer Tasks Begin (Running in Background)
           ├─ Task 1: Logs a message every 5 seconds (Never stops)
           ├─ Task 2: Waits 10 seconds, then logs every 10 seconds (Never stops)
           └─ Task 3: Logs a message every 3 seconds (Only 5 times, then stops)
           ↓
STEP 4: Application Keeps Running Forever
           ↓
STEP 5: You Press Ctrl+C to Stop It
```

### **Simplified Timeline**

```
⏱️ Time  │ What Happens?
─────────┼────────────────────────────────────────────
  0 sec  │ App starts. Timers are set up.
  3 sec  │ Task 3 logs: "Repeat timer - Execution #1"
  5 sec  │ Task 1 logs: "Timer triggered at: ..."
  6 sec  │ Task 3 logs: "Repeat timer - Execution #2"
  9 sec  │ Task 3 logs: "Repeat timer - Execution #3"
 10 sec  │ Task 1 logs again + Task 2 logs: "Delayed timer executed!"
 12 sec  │ Task 3 logs: "Repeat timer - Execution #4"
 15 sec  │ Task 1 logs again + Task 3 logs: "Repeat timer - Execution #5"
 18 sec  │ Task 3 STOPS (it only runs 5 times)
 20 sec  │ Task 1 logs again + Task 2 logs again
 25 sec  │ Task 1 logs again
 30 sec  │ Task 1 logs again + Task 2 logs again
 ... continues forever until you press Ctrl+C
```

### **Simple Way to Understand Each Task**

**Task 1: The Basic Timer**
```
┌─────────────────────────────┐
│  Task 1: Every 5 Seconds    │
├─────────────────────────────┤
│ 5 sec → Log message         │
│ 10 sec → Log message        │
│ 15 sec → Log message        │
│ 20 sec → Log message        │
│ ... continues forever ...   │
└─────────────────────────────┘
```

**Task 2: The Delayed Timer**
```
┌────────────────────────────────────────┐
│  Task 2: Wait 10 sec, then Every 10 sec│
├────────────────────────────────────────┤
│ 0-10 sec → Waiting (doing nothing)     │
│ 10 sec → Log message (first time!)     │
│ 20 sec → Log message                   │
│ 30 sec → Log message                   │
│ 40 sec → Log message                   │
│ ... continues forever ...              │
└────────────────────────────────────────┘
```

**Task 3: The Limited Timer**
```
┌───────────────────────────────────┐
│  Task 3: Every 3 Seconds (5 times)│
├───────────────────────────────────┤
│ 3 sec → Log message #1            │
│ 6 sec → Log message #2            │
│ 9 sec → Log message #3            │
│ 12 sec → Log message #4           │
│ 15 sec → Log message #5           │
│ 18 sec onwards → NOTHING (stops)  │
└───────────────────────────────────┘
```

### **Visual Picture of All Tasks Running Together**

```
Task 1:  ───── 5s ───── 10s ───── 15s ───── 20s ───── 25s ───── 30s ──→
         |log| |log |  |log |  |log |  |log |  |log |  |log |

Task 2:  ────────────── 10s ────────────── 20s ────────────── 30s ──→
                        |log|            |log|            |log|

Task 3:  ─── 3s ─── 6s ─── 9s ─── 12s ─── 15s ✗ DONE
         |log| |log| |log| |log| |log| (stops here)

Timeline: 0    3    6    9   12   15   18   20   25   30
```

---

## Project Flow

### **Complete Execution Timeline**

```
┌──────────────────────────────────────────────────────────────────┐
│ PHASE 1: APPLICATION STARTUP (0 seconds)                         │
└──────────────────────────────────────────────────────────────────┘

1. User runs: mvn spring-boot:run
   
2. JVM loads CamelApplication.class
   
3. main() method executes
   
4. SpringApplication.run(CamelApplication.class, args) called
   
5. Spring Boot:
   - Creates ApplicationContext
   - Scans classpath for @Component, @SpringBootApplication
   - Finds CamelApplication and TimerRoute
   
6. TimerRoute instantiated:
   - @Component annotation triggers creation
   - configure() method automatically called
   - Three routes defined:
     • Route 1: from("timer:myTimer?period=5000")
     • Route 2: from("timer:delayedTimer?period=10000&delay=10000")
     • Route 3: from("timer:repeatTimer?period=3000&repeatCount=5")
   
7. Camel starts all routes
   
8. Console output:
   Started CamelApplication in 1.798 seconds
   Started route1 (timer://myTimer)
   Started route2 (timer://delayedTimer)
   Started route3 (timer://repeatTimer)

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 2: ROUTE 3 STARTS (3 seconds)                              │
└──────────────────────────────────────────────────────────────────┘

Time: 3s
Route 3 fires (first execution after initial delay)

Execution:
1. Timer generates message
2. CamelTimerCounter = 1
3. setBody() sets: "Repeat timer - Execution #1"
4. log() outputs to console
5. Message completed

Console Output:
18:39:02.758 [Camel (camel-1) thread #3 - timer://repeatTimer] 
INFO route3 - Repeat timer - Execution #1

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 3: ROUTE 1 STARTS (5 seconds)                              │
└──────────────────────────────────────────────────────────────────┘

Time: 5s
Route 1 fires (first execution, no initial delay)

Execution:
1. Timer generates message
2. setBody() sets: "Timer triggered at: 1765631341514"
3. log() outputs to console
4. to("log:timerLog") sends to another logger
5. Message completed

Console Output:
18:39:02.758 [Camel (camel-1) thread #1 - timer://myTimer] 
INFO route1 - Timer triggered at: 1765631341514

18:39:02.762 [Camel (camel-1) thread #1 - timer://myTimer] 
INFO timerLog - Exchange[ExchangePattern: InOnly, BodyType: String, Body: Timer triggered at: 1765631341514]

Note: Two log entries because of .log() and .to("log:...")

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 4: ROUTE 3 - EXECUTION 2 (6 seconds)                       │
└──────────────────────────────────────────────────────────────────┘

Time: 6s
Route 3 fires (second execution, CamelTimerCounter = 2)

Console Output:
18:39:05.726 [Camel (camel-1) thread #3 - timer://repeatTimer] 
INFO route3 - Repeat timer - Execution #2

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 5: ROUTE 1 - SECOND EXECUTION (10 seconds)                 │
└──────────────────────────────────────────────────────────────────┘

Time: 10s
Route 1 fires again (every 5 seconds)

Also: Route 2 fires for FIRST time (10 second delay expired)

Console Output:
18:39:07.723 [Camel (camel-1) thread #1 - timer://myTimer] 
INFO route1 - Timer triggered at: 1765631341514

18:39:07.724 [Camel (camel-1) thread #1 - timer://myTimer] 
INFO timerLog - Exchange[...Body: Timer triggered at: 1765631341514]

18:39:11.730 [Camel (camel-1) thread #2 - timer://delayedTimer] 
INFO route2 - Delayed timer executed!

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 6: ROUTE 3 - EXECUTION 3 (9 seconds) & 4 (12s) & 5 (15s)  │
└──────────────────────────────────────────────────────────────────┘

Time: 9s  → Execution #3
Time: 12s → Execution #4
Time: 15s → Execution #5 (FINAL - repeatCount=5)
Time: 18s → NO MORE EXECUTIONS (Route 3 stops)

┌──────────────────────────────────────────────────────────────────┐
│ PHASE 7: STEADY STATE (After 15 seconds)                         │
└──────────────────────────────────────────────────────────────────┘

Continuous Pattern:

Route 1 (every 5 seconds):  ═══════════════════════
Time:     5s  10s  15s  20s  25s  30s  35s...

Route 2 (every 10s, after 10s delay): ═══════════════════════════
Time:    10s  20s  30s  40s  50s...

Route 3 (every 3s, 5 times max):  ═══════════════════════
Time:     3s   6s   9s  12s  15s ✗ (stops here)

Visual Timeline:
0s   3s   6s   9s  12s  15s  20s  25s  30s  35s
|    |    |    |    |    |    |    |    |    |
          R3#1       R3#2  R3#3  R3#4  R3#5
                      R1     R1    R2    R1    R1    R1    R1
                              R2          R2

Legend:
R1 = Route 1 fires (every 5s, infinite)
R2 = Route 2 fires (every 10s after 10s delay, infinite)
R3 = Route 3 fires (every 3s, only 5 times)

```

---

## Key Concepts Explained

### **Timer Endpoint URI Syntax**
```
timer:name?option1=value1&option2=value2&option3=value3
```

| Option | Default | Description |
|--------|---------|-------------|
| `period` | 1000 | Milliseconds between executions |
| `delay` | 0 | Initial delay before first execution (ms) |
| `repeatCount` | -1 | How many times to repeat (-1 = infinite) |
| `fixedRate` | false | Use fixed-rate scheduling instead of fixed-delay |
| `timer` | default | Timer scheduler name |

### **Expression Language (EL) in Camel**
```
${body}                    → Message content
${header.CamelTimerCounter → Timer's execution counter (1, 2, 3...)
${headers}                 → All message headers
${exchangeId}              → Unique message ID
${threadName}              → Current thread name
```

### **Message vs Exchange vs Body**
```
Exchange = Complete message envelope
├── Headers (metadata)
│   ├── CamelTimerCounter: 1
│   ├── CamelTimerId: myTimer
│   └── ... other headers
│
├── Body (actual content)
│   └── "Timer triggered at: 1765631341514"
│
└── Properties
    └── ... internal Camel data
```

---

## Why This Architecture?

**Advantages:**
1. **Decoupled**: Routes are independent
2. **Scalable**: Easy to add more timer routes
3. **Testable**: Each route can be tested separately
4. **Maintainable**: Clear separation of concerns
5. **Configurable**: Change intervals without code changes

**Use Cases:**
- Health check polling every minute
- Database sync every hour
- Cache refresh every 30 seconds
- Report generation every day at 2 AM
- API rate limit checks

---

## Running the Project

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Stop (Ctrl+C in terminal)
```

**Expected Console Output:**
- Application starts (1.798 seconds)
- All 3 routes start successfully
- Every 3 seconds: Route 3 logs (5 times max)
- Every 5 seconds: Route 1 logs
- After 10 seconds: Route 2 logs (starting from 10s mark)

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| "Cannot find symbol: CamelSpringBootApplication" | Missing dependency | Use `@SpringBootApplication` instead |
| No logs appearing | Logging not configured | Check `application.properties` logging levels |
| Routes not starting | Missing `@Component` annotation | Add `@Component` to TimerRoute |
| Timer fires every 1 second | `period` parameter missing | Add `?period=5000` to timer URI |
| App exits immediately | `main-run-controller` disabled | Set `camel.springboot.main-run-controller=true` |

