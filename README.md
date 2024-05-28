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

TBD:

- Caching
- Bulk Fetching
- CPU R/U Knee through resilience strategies
- Parallelization
- Reduce Logging / Switch from Logging to Metrics
- Downsampling
- Consolidate Databases
- RPC and Proto Buffer
- Generate Indices

## Analysis of the Demo Application 🧪

TBD

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