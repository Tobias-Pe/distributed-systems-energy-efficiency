# Optimization of Energy Efficiency in Distributed Systems

[![MIT License](https://img.shields.io/github/license/Tobias-Pe/distributed-systems-energy-efficiency)](https://github.com/Tobias-Pe/microservices-error-handling/blob/main/LICENSE)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.dev)

This distributed application and its infrastructure are set up to experiment with and measure different energy optimization techniques. The domain for the application is a social media platform, which includes a variety of computation types to demonstrate tasks performed by distributed systems in real scenarios.

_This project is part of a Master's thesis in Computer Science🎓_

## Overview Architecture 🏗️

![Architecture](assets/Overview.jpg)

Swagger API documentation for each service exposing REST endpoints can be found at:
- `http://localhost:PORT/swagger-ui/index.html`
- Through the gateway at `http://localhost:8080/xyservice/swagger-ui/index.html`

## Tech Stack 📚

### Services

- **Language:** Java with SpringBoot
- **Messaging:** RabbitMQ
- **Relational Database:** Postgres
- **Cache:** Redis
- **Service Discovery:** Eureka
- **Gateway:** spring-cloud-starter-gateway
- **Load Testing:** Test scenarios written in Python using [Locust](https://locust.io/) and [Poetry](https://python-poetry.org/) as Dependency Manager

### Monitoring

- **Dashboard:** Grafana
- **Power Measurement:**
  - [Kepler (Whitebox)](https://sustainable-computing.io/)
  - [SNMP Exporter (Blackbox)](https://github.com/prometheus/snmp_exporter)
- **Service Metrics:** Prometheus
- **Ops Metrics:** [kube-prometheus](https://github.com/prometheus-operator/kube-prometheus)
- **Dashboard:** Grafana
- **Tracing:** Zipkin

### Ops

- **Containerization:** Docker & Docker-Compose
- **Translation to Kubernetes Configs:** [Kompose](https://kompose.io/)
- **Kubernetes Flavor:** MicroK8s

## Energy Efficiency Practices 🔌⚡

- Caching
- Bulk Fetching
- Concurrent Programming
- Reduce Network Package Size
- Lazy Fetching
- Asynchronous Communication (RPC)
- Set Configuration Parameters Optimally
- Lower Fidelity
- Scaling of Modular Architecture

## Analysis of the Demo Application 🧪

### Base App

![Energy Efficiency of the Base App](loadtesting/data/00-baseapp/2024-07-15/16-18/output/energy_efficiency_by_stage.png)

### Experiment 1: Implement Caching

- **Objective:** Reduce energy consumption of the most energy-intensive services by implementing a caching mechanism for frequently requested data.
- **Approach:** Added Redis caching to minimize frequent requests from the `feedservice` to the `postservice` and `statisticservice`.
- **Results:**
  - **Performance:** Significant improvement in response times and stability across various user loads, with the application handling higher request rates without failures.
  - **Energy Efficiency:** Increased energy efficiency, especially for services that benefited from reduced redundant work. The base app's energy efficiency was potentially inflated due to unnecessary work, making the optimized version's efficiency gains more meaningful.
  - **Resource Usage:** Decreased container scaling due to lower workload, improving energy efficiency further.

![Energy Efficiency of the Experiment 1](loadtesting/data/01-caching/2024-07-31/16-18/output/energy_efficiency_by_stage.png)

### Experiment 2: Increase Resource Utilization

- **Objective:** Improve resource utilization to increase throughput, reduce response times, and enhance energy efficiency by optimizing various aspects of the application.
- **Approach:** Implemented several optimization techniques including:
  - **Concurrent Programming & Bulk Fetching:** Enhanced RabbitMQ listeners with a minimum of 2 and a maximum of 4 threads, enabled bulk fetching of up to 10 messages, and introduced child transactions with retry logic.
  - **Parallelization:** Enabled parallel processing in the `notificationservice` and `feedservice` to handle data more efficiently and reduce processing time.
  - **Database & Transactional Improvements:** Adjusted database operations and transactional handling to reduce failures and optimize performance.
  - **Virtual Threads:** Introduced Java virtual threads for lightweight concurrency, improving resource utilization and reducing memory footprint.
  - **Timeout Adjustments:** Reduced connection timeouts for the `gateway` and `feedservice` to minimize busy waiting and retrying.
  - **HPA Configuration Tuning:** Refined Horizontal Pod Autoscaler (HPA) settings to better match container resource needs and encourage efficient scaling.
  - **Feedservice Optimization:** Removed excessive data (comments) from posts to decrease network load and improve response times.

- **Results:**
  - **Performance:** Significant increases in throughput and improved response times, with stable performance up to 400 users. The application demonstrated better scalability and managed higher loads efficiently.
  - **Energy Efficiency:** Observed a consistent rise in energy consumption due to higher hardware utilization, but overall energy efficiency improved compared to Experiment 1. Energy efficiency increased up to stage 400, with notable improvements in handling higher user loads.
  - **Resource Usage:** Enhanced container scaling behavior with more dynamic adjustments based on the load. Reduced failures and more efficient resource utilization led to better overall system performance.

![Energy Efficiency of the Experiment 2](loadtesting/data/02-resource-usage/2024-08-05/11-13/output/energy_efficiency_by_stage.png)

### Experiment 3: Communication Efficiency

- **Objective:** Improve communication efficiency and reduce response times by optimizing data transfer and interservice communication, addressing observed issues from Experiment 2.
- **Approach:**
  - **Maximum Response Time Limit:** Implemented a maximum response time limit of five seconds on all synchronous endpoints using Spring MVC’s `Callable` wrapper to handle long-running tasks asynchronously and prevent thread pool exhaustion.
  - **Response Size Optimization:** Reduced the page size for the `feedservice` from 50 to 40 posts and streamlined the `notificationservice` and `userservice` communication to minimize data transfer. Introduced a new endpoint for user existence checks to return only a boolean value.
  - **AMQP RPC for Interservice Communication:** Switched to RabbitMQ’s AMQP RPC for asynchronous communication between domain services to enhance resilience and scalability.
  - **Optimistic Locking Adjustment:** Replaced optimistic locking with direct `UPDATE` queries in the database for handling concurrent changes, reducing version conflicts and improving performance.

- **Results:**
  - **Performance:** Improved stability and throughput with no significant drops in requests per second during scale-down phases. Response times were slightly lower at stages 100, 200, and 300 but higher at stages 400 and 500 compared to Experiment 2. The system's scalability improved with fewer collapses in request handling.
  - **Energy Efficiency:** Slight increase in energy consumption at stages 400 and 500, with decreased efficiency at stages 100, 400, and 500. Stages 200 and 300 showed improved energy efficiency compared to Experiment 2. Increased energy consumption was noted in `statisticservice-db` and `rabbitmq`, likely due to RPC communication and possibly inefficient database operations.
  - **Resource Usage:** Notable improvements in system stability and throughput. The `feedservice` and `notificationservice` showed the most significant gains in efficiency, while the `gateway` and `statisticservice` experienced reductions in energy efficiency.

![Energy Efficiency of the Experiment 3](loadtesting/data/03-communication-efficiency/2024-08-10/17-19/output/energy_efficiency_by_stage.png)

## Run Locally 🏃

### Application and Infrastructure

**Requirements:**

- Java 21 with Maven
- [Docker](https://docs.docker.com/get-docker/) to start infrastructure containers

#### Option 1: JetBrains RunConfigs

Use the RunConfigs in JetBrains:
Services Tab --> Run all``
Or start the services through the defined run configs in the top left dropdown. Various run configurations are available for Docker, JUnit, SpringBoot, and Shell scripts.

#### Option 2: Docker

Start everything using docker-compose and the images published in the GitHub registry:

```bash
docker compose -f docker/docker-compose.yml up
```

(Optional) For building the services into local docker containers:

```bash
./scripts/spring-build-and-tag-images.sh
```

Option 3: Maven

Start the services using the Maven SpringBoot plugin (ensure the working directory for each service is the project root):

```bash
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl servicediscovery
mvn spring-boot:run -pl userservice
...
```

That's it! All Docker files will be pulled, started, and waited for automatically, thanks to Docker Compose Support in Spring Boot.

Load Tests

Requirements:

- Python 3.10
- Poetry

```bash
poetry install
poetry shell
locust --host http://localhost:8080 --processes 4
```

Deployment 🚀

Requirements:

- A Kubernetes cluster
- kube-prometheus on the cluster to measure resource usage
- Kepler on the cluster to measure energy consumption
- (Optionally) Kompose if you want to change configurations in Docker Compose and regenerate the kubeconfigs

Apply the deployment to your cluster:

```bash
kubectl apply -f ./kubernetes/
```

Regenerate & apply all kubeconfig files:

```bash
./scripts/deploy-kubernetes.sh
```

## Known Issues 🦺

- If the Prometheus service from prometheus-operator/kube-prometheus can't be scraped by the Grafana instance, adjust the network policies.
- Locust running from a single host can run out of connections if the user count raises too high.

## Author ✍️

[@Tobias Peslalz](https://github.com/Tobias-Pe)

![linkedin](https://www.linkedin.com/in/tobias-peslalz)