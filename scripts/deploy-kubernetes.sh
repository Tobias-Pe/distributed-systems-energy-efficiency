#!/bin/bash

kubectl delete -f ./kubernetes/
rm -f ./kubernetes/*.yaml

kompose convert -f ./docker/docker-compose.yml -o ./kubernetes/
# remove -tcp suffix from loadbalanced services
find kubernetes/ -type f -name "*-tcp-service.yaml" -exec sed -i 's/\(name: \)\(.*\)-tcp/\1\2/g' {} \;

kubectl config view

kubectl apply -f ./kubernetes/

echo "Pull down on user input:"
read userInput

kubectl delete -f ./kubernetes/
