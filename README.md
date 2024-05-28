# Optimization of Energy Efficiency in Distributed Systems

[![MIT License](https://img.shields.io/github/license/Tobias-Pe/distributed-systems-energy-efficiency)](https://github.com/Tobias-Pe/microservices-error-handling/blob/main/LICENSE)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.dev)

This distributed application and its infrastructure are an experiment setup for trying out and measuring different
energy optimization techniques.
The domain for the application will be a social media platform containing a wide array of computation types to showcase
tasks done by distributed systems in real scenarios.

_This project is part of a Masterthesis in Computer Science🎓_

## Overview Architecture 🏗️

![Architecture](assets/Overview.jpg)

You can find swagger api documentation for each service exposing rest-endpoints
under `http://localhost:PORT/swagger-ui/index.html` or through the
gateway `http://localhost:8080/xyservice/swagger-ui/index.html`

## Tech Stack 📚

### Services

* **Language:** Java with SpringBoot
* **Messaging:** RabbitMQ
* **Relational Database:** Postgres
* **Cache:** Redis
* **Servicediscovery:** Eureka
* **Gateway:** spring-cloud-starter-gateway


* **Loadtesting:** Testscenarios written in Python using [Locust](https://locust.io/) and [Poetry](https://python-poetry.org/) as Dependency Manager

### Monitoring

* **Dashboard:** Grafana
* **Power Measurement:
  ** [Kepler (Whitebox)](https://sustainable-computing.io/) & [SNMP Exporter (Blackbox)](https://github.com/prometheus/snmp_exporter)
* **Service Metrics:** Prometheus
* **Ops Metrics:** [kube-prometheus](https://github.com/prometheus-operator/kube-prometheus)
* **Dashboard:** Grafana
* **Tracing:** Zipkin

### Ops

* **Containerization:** Docker & Docker-Compose
* **Translation to Kubernetes Configs:** [Kompose](https://kompose.io/)
* **Kubernetes Flavor:** MicroK8s

## Energy efficiency practices 🔌⚡

TBD:

- Caching
- Bulk Fetching
- CPU R/U Knee durch resilience startegies
- Parallelization
- Logging reduzieren/Von Logging zu Metrics wechseln
- Downsample
- Datenbanken zusammenfassen
- RPC und Proto Buffer
- Index generieren

## Analysis of the Demo-Application 🧪

TBD

## Run Locally 🏃

#### Application and Infrastructure

Requirements:

- Java 21 with Maven
- [Docker](https://docs.docker.com/get-docker/) to start infrastructure containers

### Option 1: **JetBrains RunConfigs**

``Services Tab --> Run all`` || ``Start the services through the defined run configs in the top left dropdown``

There are many run configurations present for Docker, JUnit, SpringBoot and Shell scripts you can use!

### Option 2: **Docker**

Start everything up using docker compose and the images published in the GitHub registry:

```bash
docker compose -f docker/docker-compose.yml up
```

(_Optional_) For building the services into local docker containers:

```bash
./scripts/spring-build-and-tag-images.sh
```

### Option 3: **Maven**

Start the services using the maven SpringBoot plugin (make sure that the working directory for each service is the project root):

```bash
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl servicediscovery
mvn spring-boot:run -pl userservice
...
```

That's it! 

All docker files will be automatically pulled, started and waited for thanks to [Docker Compose Support in Spring Boot](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1)

#### Loadtests

Requirements:

- Python 3.10
- [Poetry](https://python-poetry.org/docs/#installing-with-the-official-installer)

```bash
poetry install
poetry shell
locust --host http://localhost:8080 --processes 4
```

## Deployment 🚀

Requirements:

- A Kubernetes cluster
- [kube-prometheus](https://github.com/prometheus-operator/kube-prometheus) on the cluster to measure resource usage
- [Kepler](https://sustainable-computing.io/installation/strategy) on the cluster to measure energy consumption
- (_Optionally_) https://kompose.io/installation/ If you want to change configurations in docker compose and regenerate
  the kubeconfigs

Apply the deployment to your cluster: 

```bash
kubectl apply -f ./kubernetes/
```

Regenerate & apply all kubeconfig files:

```bash
./scripts/deploy-kubernetes.sh
```

## Known Issues 🦺

- If the prometheus service from prometheus-operator/kube-prometheus can't be scraped by the grafana instance --> Adjust
  the networkpolicies
- Locust running from a single host can run out of connections if user count rasises to high

## Author ✍️

[@Tobias Peslalz](https://github.com/Tobias-Pe)

[![linkedin](https://img.shields.io/badge/LinkedIn-0077B5?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/tobias-peslalz)