#!/bin/bash

kubectl port-forward deployment/gateway 8080 --pod-running-timeout=5m &
kubectl port-forward deployment/servicediscovery 8761 --pod-running-timeout=5m &
kubectl port-forward deployment/grafana 3000 --pod-running-timeout=5m &
kubectl port-forward deployment/zipkin 9411 --pod-running-timeout=5m &
kubectl port-forward deployment/prometheus 9090 --pod-running-timeout=5m &
kubectl port-forward deployment/mysql 3306 --pod-running-timeout=5m &
kubectl port-forward deployment/rabbitmq 15672 --pod-running-timeout=5m &
kubectl port-forward deployment/redis 6379 --pod-running-timeout=5m &
wait
