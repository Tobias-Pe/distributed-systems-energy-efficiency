#!/bin/bash

kubectl delete -f ./kubernetes/
rm -f ./kubernetes/*.yaml

kompose convert -f ./docker/docker-compose.yml -o ./kubernetes/
kubectl config view

kubectl apply -f ./kubernetes/

echo "Pull down on user input:"
read userInput

kubectl delete -f ./kubernetes/
