# Service Integration - Week 7

## Table of Contents
- [Part 1: Service Integration Overview](#part-1-service-integration-overview)
- [Part 2: gRPC - Synchronous Communication](#part-2-grpc---synchronous-communication)
- [Part 3: Message Broker - Asynchronous Communication](#part-3-message-broker---asynchronous-communication)
- [Part 4: RabbitMQ](#part-4-rabbitmq)
- [Part 5: Apache Kafka](#part-5-apache-kafka)
- [Part 6: Comparing gRPC vs Message Broker](#part-6-comparing-grpc-vs-message-broker)
- [Part 7: Comparing RabbitMQ vs Kafka](#part-7-comparing-rabbitmq-vs-kafka)
- [Part 8: When to Use Each Technology?](#part-8-when-to-use-each-technology)
- [Part 9: Hybrid Architecture](#part-9-hybrid-architecture)
- [Part 10: Demo & Practice](#part-10-demo--practice)
- [Practice Exercises](#practice-exercises)
- [References](#references)
- [Discussion Questions](#discussion-questions)

---

## Part 1: Service Integration Overview

### 1.1. What is Service Integration?

Microservices architecture was created to overcome the limitations of large monolithic applications — mainly issues with scalability, deployment speed, and tight coupling.
By dividing a big system into smaller, independent services, teams can scale parts individually, deploy faster, and work more autonomously.

Service integration refers to the process of connecting and coordinating different services—often in a microservices, SOA (Service-Oriented Architecture), or enterprise system—so they can communicate and work together seamlessly.

### 1.2. Integration Methods

#### 1. Synchronous Communication

Synchronous communication happens when two services interact in real time — the caller sends a request and waits for a response before proceeding.

Method:

- HTTP

- gRPC 

- GraphQL

- SOAP

When to Use Synchronous Communication?

- You need immediate feedback (e.g., validation, authentication, payment confirmation).

- Operations are short-lived (milliseconds to seconds).

- Strong consistency or sequential workflow is required.

- The system can tolerate tight coupling for that specific operation.

Why use synchronous communication?
- Simplicity – Easy to implement and reason about.

- Immediate response – Client knows success or failure right away.

- Strong consistency – State changes can be validated and committed in one flow.

- Ease of debugging – You can test endpoints directly via Postman or curl.
#### 2. Asynchronous Communication

Asynchronous communication is a pattern where services exchange messages without waiting for an immediate response. The sender posts a message and continues; the receiver processes the message later. This decouples timing and availability between services and is a core pattern in resilient, scalable distributed systems.

Method:

- Message queue

- Publish - subscribe

- Work queue with competing consumers

- Event streaming


Why use asynchronous communication?

- Loose coupling: Sender and receiver don’t have to be up at the same time.

- Scalability: Work can be buffered in queues and processed by many consumers.

- Resilience: Temporary failures of consumers don’t block producers. Messages persist until processed.

- Throughput: Producers can push messages rapidly; consumers handle them at their own pace.

- Event-driven design: Naturally supports event-driven architectures, CQRS, and event sourcing.
---

## Part 2: gRPC - Synchronous Communication

### 2.1. What is gRPC?

**gRPC** is a high-performance, open-source framework for remote procedure calls (RPC) that allows client and server applications to communicate transparently and efficiently. It uses HTTP/2 for transport, Protocol Buffers to define service and message structures.

### 2.2. gRPC Architecture

![gRPC architecture](./image/gRPC-architecture-21.ppm)

- Protobuf: interface definition language (IDL) and serialization format. 
- gRPC client: sends requests to the server as if calling a local method.
- gRPC server: implements the service interface defined in .proto, exposes endpoints that clients can call remotely, can handle multiple concurrent RPC calls efficiently.
- Transport layer: use HTTP/2
    + Multiplexing: Multiple requests/responses over a single connection

    + Bi-directional streaming: Both client and server can send data simultaneously

    + Header compression: Reduces network overhead

    + Server push: Useful for streaming responses
### 2.3. Protocol Buffers

Protocol Buffers (Protobuf) is a language-neutral, platform-neutral, and extensible way of serializing structured data.
It was developed by Google as a faster, smaller alternative to XML or JSON for communication between services or for data storage.

- Compact binary format

- Language-neutral

- Platform-neutral 

- Strongly typed

- Versioning

### 2.4. Types of gRPC Calls

#### 1. Unary RPC

The client sends a single request message to the server, and the server responds with a single response message.

**How it works:**
- Client calls a remote method with one request.
- Server processes the request and sends back one response.

**Benefits:**
- Simple and easy to implement.
- Suitable for standard request/response operations.
- Strongly typed and compile-time verified.

#### 2. Server Streaming

The client sends a single request message to the server, and the server responds with a stream of messages. The client reads from this stream until all messages have been received

**How it works:**
- Client sends a single request.
- Server sends multiple responses as a stream.
- Client reads each message asynchronously as they arrive.

**Benefits:**
- Efficient for sending large amounts of data incrementally.
- Reduces memory usage (no need to wait for all data at once).
- Useful for real-time updates or large datasets.


#### 3. Client Streaming

The client sends a stream of messages to the server, and after all messages are sent, the server responds with a single response message

**How it works:**
- Client streams multiple messages.
- Server processes messages as they arrive.
- Server sends one final response when client completes streaming.

**Benefits:**
- Good for uploading large datasets in chunks.
- Reduces network congestion and memory usage.
- Supports asynchronous and incremental processing.

#### 4. Bidirectional Streaming

This allows both the client and the server to send and receive streams of messages independently

**How it works:**
- Client and server establish a stream.
- Both sides can send messages independently and asynchronously.
- Useful for real-time applications where both sides need to communicate continuously.

**Benefits:**
- Real-time two-way communication.
- Low-latency, ideal for chat, gaming, telemetry, or live updates.
- Supports back-and-forth asynchronous messaging.

### 2.5. Advantages of gRPC

- Performance and Efficiency
    + **HTTP/2:** Enables multiplexing, header compression, and server push for faster data transfer.  
    + **Protocol Buffers:** Uses compact binary serialization, reducing bandwidth and improving speed.  
    + **Compression:** Supports message compression to further reduce network usage.  

- Strong Typing and Code Generation
    + **Code generation:** Automatically generates client/server code from `.proto` files, saving development time.  
    + **Strongly-typed contracts:** Ensures data consistency and catches errors at compile-time.  

- Communication and Flexibility
    + **Bidirectional streaming:** Supports real-time, two-way messaging for applications like chat or live updates.  
    + **Polyglot support:** Works with many programming languages, enabling interoperability in diverse environments.  
    + **Pluggable features:** Middleware (interceptors) allow integration of load balancing, authentication, tracing, and health checks.  

- Scalability
    + **High throughput:** Low latency, efficient communication for microservices.  
    + **Easily scalable:** Can handle high traffic volumes and many concurrent connections.  

### 2.6. Disadvantages of gRPC

- **Steeper Learning Curve:** Requires learning Protocol Buffers (Protobuf), unlike familiar JSON or XML in REST.  
- **Limited Browser Support:** Browsers don’t natively support gRPC over HTTP/2; workarounds like gRPC-Web are needed.  
- **Smaller Ecosystem:** Fewer tools, libraries, and community resources compared to REST.  
- **Debugging Challenges:** Binary data is harder to inspect and debug than human-readable formats.  
- **HTTP/2 Dependency:** Requires environments that fully support HTTP/2.  
- **Complex Error Handling:** Does not use standard HTTP status codes; custom error handling is needed.

---

## Part 3: Message Broker - Asynchronous Communication

### 3.1. What is Message Broker?

A message broker is a software system that enables communication between different applications or services by receiving, storing, and forwarding messages. It acts as an intermediary, allowing systems to exchange information without being directly connected or dependent on each other.

### 3.2. Basic Concepts

![AMQP](./image/AMQP.webp)

#### 1. Producer

Service that send message to exchange

#### 2. Consumer

Service that receive message, can run in parallel for scaling

#### 3. Queue

Store message until consumed, configurable (durable, exclusive, auto-delete, TTL, dead-letter)

#### 4. Topic

Routing mechanism based on pattern matching (used in topic exchanges)

#### 5. Exchange

Receives messages from producers and routes them to one or more queues according to rules

#### 6. Partition

A way to split data across multiple brokers or nodes for parallel processing and scalability (common in Kafka)

---

## Part 4: RabbitMQ

### 4.1. RabbitMQ Overview

Message broker, asynchronous messaging, base on AMQP but support MQTT, HTTP, STOMP,...

### 4.2. RabbitMQ Architecture

#### Exchange Types:

##### 1. Direct

Exact routing key match

##### 2. Fanout

Broad cast to all bound queues

##### 3. Topic

Routing based on pattern

##### 4. Headers

Routing based on message header

### 4.3. Messaging Patterns with RabbitMQ

#### 1. Work Queue Pattern

Distribute time-consuming tasks among multiple workers to balance load.

How it works:

- Producers send messages (tasks) to a queue.

- Multiple consumers (workers) listen to the queue.

- Each message is delivered to one consumer only.

- Helps scale processing and avoid overloading a single worker.

#### 2. Publish/Subscribe Pattern

Send the same message to multiple consumers.

How it works:

- Producer sends a message to an exchange (fanout type).

- Exchange broadcasts the message to all bound queues.

- Every consumer listening on its queue gets a copy.

#### 3. Routing Pattern

Send messages to specific queues based on a routing key.

How it works:

- Producer sends messages to a direct exchange with a routing key.

- Exchange sends message to queues that are bound with the matching key.

#### 4. RPC Pattern

Implement synchronous request/reply messaging using RabbitMQ.

How it works:

- Client sends a request message to a queue.

- Server (consumer) processes the message and sends back a reply to a queue specified by the client.

- The client waits for the response before continuing.

### 4.4. RabbitMQ Advantages

- Reliable Messaging

+ Supports message acknowledgment, ensuring messages aren’t lost.

+ Can persist messages to disk so they survive broker restarts.

- Flexible Routing

+ Provides multiple exchange types (direct, fanout, topic, headers) for complex routing.

+ Supports selective delivery, publish/subscribe, and request/reply (RPC).

- Scalability

+ Can distribute load among multiple consumers (Work Queues).

+ Supports clustering to scale horizontally.

- Supports Multiple Protocols

+ AMQP (primary), MQTT, STOMP, HTTP, and more.

+ Easy to integrate with different clients and platforms.

- Message Durability and Acknowledgment

+ Persistent messages + durable queues prevent data loss.

+ Acknowledgment ensures reliable processing.

- High Availability

+ Clustering and mirrored queues allow fault-tolerant setups.

- Mature Ecosystem

+ Well-supported, lots of client libraries.

+ Tools for monitoring, management UI, plugins for tracing, metrics, etc.

- Asynchronous Communication

+ Decouples producers and consumers.

+ Allows systems to handle variable loads without overloading services.

### 4.5. RabbitMQ Disadvantages

- Complexity

    + More configuration and setup than simpler brokers (e.g., Redis Pub/Sub).

    + Managing clustering, high availability, and failover requires experience.

- Performance Overhead

    + Slower than lightweight brokers (like Kafka for high-throughput streaming).

    + Message durability and acknowledgment add latency.

- Not Ideal for Large Data Streams

    + Better suited for tasks, commands, or events, not huge continuous streams.

    + Kafka or Pulsar may be better for big data pipelines.

- Message Ordering

    + Ordering is not guaranteed across multiple consumers.

    + Only ordered within a single queue and single consumer.

- Memory Usage

    + Holding large queues in memory can stress resources.

    + Needs careful tuning for persistent messages or high load.

- Scaling Challenges for Certain Workloads

    + Cluster scaling can be tricky when queues are large or many nodes are involved.

    + Mirrored queues add network overhead.

---

## Part 5: Apache Kafka

### 5.1. Kafka Overview

> _[Content to be added]_

### 5.2. Kafka Architecture

#### Main Components:

##### 1. Producer

> _[Content to be added]_

##### 2. Consumer

> _[Content to be added]_

##### 3. Topic

> _[Content to be added]_

##### 4. Partition

> _[Content to be added]_

##### 5. Broker

> _[Content to be added]_

##### 6. ZooKeeper/KRaft

> _[Content to be added]_

### 5.3. Kafka Topics and Partitions

> _[Content to be added]_

### 5.4. Consumer Groups

> _[Content to be added]_

### 5.5. Message Retention

> _[Content to be added]_

### 5.6. Kafka Advantages

> _[Content to be added]_

### 5.7. Kafka Disadvantages

> _[Content to be added]_

---

## Part 6: Comparing gRPC vs Message Broker

| Criteria | gRPC | Message Broker |
|----------|------|----------------|
| Communication model | _[Content to be added]_ | _[Content to be added]_ |
| Coupling | _[Content to be added]_ | _[Content to be added]_ |
| Response time | _[Content to be added]_ | _[Content to be added]_ |
| Latency | _[Content to be added]_ | _[Content to be added]_ |
| Complexity | _[Content to be added]_ | _[Content to be added]_ |
| Fault handling | _[Content to be added]_ | _[Content to be added]_ |
| Scalability | _[Content to be added]_ | _[Content to be added]_ |

---

## Part 7: Comparing RabbitMQ vs Kafka

| Criteria | RabbitMQ | Kafka |
|----------|----------|-------|
| Model | _[Content to be added]_ | _[Content to be added]_ |
| Delivery mechanism | _[Content to be added]_ | _[Content to be added]_ |
| Throughput | _[Content to be added]_ | _[Content to be added]_ |
| Latency | _[Content to be added]_ | _[Content to be added]_ |
| Retention | _[Content to be added]_ | _[Content to be added]_ |
| Ordering | _[Content to be added]_ | _[Content to be added]_ |
| Use case | _[Content to be added]_ | _[Content to be added]_ |
| Complexity | _[Content to be added]_ | _[Content to be added]_ |
| Replay capability | _[Content to be added]_ | _[Content to be added]_ |

---

## Part 8: When to Use Each Technology?

### 8.1. Use gRPC when:

gRPC is a high-performance, language-agnostic RPC framework. Use it when need fast, synchronous communication between services.

Typical scenarios:

- Microservices needing low-latency, point-to-point communication.

- Services that require strictly defined contracts (Protobuf schemas).

- High-performance applications where binary serialization (Protobuf) is important.

- Real-time APIs where request/response pattern is enough.

- Internal service-to-service communication in a trusted network.

Key Advantages:

- Strongly typed API with auto-generated code.

- Fast and lightweight due to Protobuf.

- Supports streaming (client, server, or bidirectional).

### 8.2. Use RabbitMQ when:

RabbitMQ is a message broker for reliable, decoupled, asynchronous communication. Use it when you need task distribution, decoupling, or complex routing.

Typical scenarios:

- Work queues: distribute background tasks among multiple workers.

- Publish/Subscribe: send updates or notifications to multiple consumers.

- Routing messages to specific consumers based on type or priority.

- Implementing RPC-style communication with reply queues.

- When message durability, acknowledgment, and reliability are required.

Key Advantages:

- Reliable message delivery with acknowledgments.

- Flexible routing with exchanges and bindings.

- Supports multiple protocols and clients.

### 8.3. Use Kafka when:

Kafka is a distributed streaming platform optimized for high-throughput, persistent log-based messaging. Use it when you need stream processing, event sourcing, or large-scale data pipelines.

Typical scenarios:

- Collecting and processing large streams of events or logs.

- Event sourcing and replayable event streams.

- Real-time analytics or metrics pipelines.

- Decoupling services where high throughput and persistence matter more than instant delivery.

- Systems that require horizontal scalability and fault tolerance.

Key Advantages:

- Extremely high throughput.

- Durable, replayable logs for auditing or reprocessing.

- Horizontally scalable across multiple nodes.

---

## Part 9: Hybrid Architecture

A hybrid architecture means using more than one communication technology together in a system to get the benefits of each. Modern systems often combine gRPC, RabbitMQ, and Kafka rather than relying on only one.

### 9.1. Combining Multiple Methods

- gRPC + RabbitMQ

    + Use gRPC for synchronous requests between microservices.

    + Use RabbitMQ for asynchronous tasks or notifications.

Example: A payment service validates accounts via gRPC, then sends a receipt email asynchronously via RabbitMQ.

- gRPC + Kafka

    + gRPC handles real-time requests, while Kafka logs all events for analytics or auditing.

Example: A trading platform processes transactions via gRPC, but publishes each trade to Kafka for downstream analysis.

- RabbitMQ + Kafka

    + RabbitMQ handles short-lived task queues.

    + Kafka stores a persistent stream of events for reporting, monitoring, or replaying.

- Key idea: You pick the right tool for the right job.

    + Synchronous vs asynchronous.

    + Reliability vs throughput.

    + Temporary tasks vs persistent event streams.

### 9.2. Design Principles

- Use the right tool for the right job

    + Don’t force gRPC to handle massive event streams.

    + Don’t use Kafka for simple request/reply APIs.

- Decouple services

    + Let asynchronous messaging (RabbitMQ/Kafka) loosen dependencies between services.

- Keep synchronous and asynchronous flows clear

    + Mark which services expect immediate responses (gRPC) and which can be processed in the background (RabbitMQ/Kafka).

- Ensure reliability where it matters

    + Persistent queues for critical tasks.

    + Retry mechanisms for failures.

- Monitor and observe each layer

    + Each technology may have its own metrics and monitoring needs.

- Avoid complexity overload

    + Don’t combine all three just because you can—use hybrid approaches only when benefits outweigh the complexity.

---

## Part 10: Demo & Practice

### 10.1. Demo 1: gRPC Service

> _[Demo code and instructions]_

### 10.2. Demo 2: RabbitMQ Integration

> _[Demo code and instructions]_

### 10.3. Demo 3: Kafka Event Streaming

> _[Demo code and instructions]_

---

## Practice Exercises

### Exercise 1: Implement gRPC Service

**Requirements:**
- Create a Product Service with gRPC API
- Implement CRUD operations
- Write client to test the operations

### Exercise 2: RabbitMQ Task Queue

**Requirements:**
- Create a notification system with RabbitMQ
- Multiple workers handle email/SMS
- Implement retry mechanism

### Exercise 3: Kafka Event Pipeline

**Requirements:**
- Build event-driven order processing system
- Producer: Order Service
- Consumers: Payment Service, Inventory Service, Notification Service
- Implement event replay

### Exercise 4: Hybrid Architecture

**Requirements:**
- Design an e-commerce system
- Use both gRPC and Message Broker
- Explain the reasoning for choosing each technology

---

## References

### gRPC
- [Official Documentation](https://grpc.io/docs/)
- [Protocol Buffers](https://protobuf.dev/)

### RabbitMQ
- [Official Documentation](https://www.rabbitmq.com/documentation.html)
- [Getting Started Tutorials](https://www.rabbitmq.com/getstarted.html)

### Kafka
- [Official Documentation](https://kafka.apache.org/documentation/)
- [Confluent Guides](https://docs.confluent.io/)

### Books
- **"Building Microservices"** - Sam Newman
- **"Designing Data-Intensive Applications"** - Martin Kleppmann

---

## Discussion Questions

1. **Why shouldn't we use synchronous calls for all communication?**

2. **In what cases can messages be lost? How to ensure at-least-once delivery?**

3. **Can Kafka completely replace RabbitMQ? Why or why not?**

4. **Is gRPC suitable for client-to-server communication?**

5. **How to handle failures in asynchronous communication?**
