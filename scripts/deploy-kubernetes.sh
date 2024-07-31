#!/bin/bash

# Optionally accept a branch name as the first argument
BRANCH_NAME=${1:-latest}

# Process the branch name to sanitize and log it
echo "Using tag: $BRANCH_NAME"

# Remove current Kubernetes objects
kubectl delete -f ./kubernetes/
rm -f ./kubernetes/*.yaml

# Convert docker-compose.yml to Kubernetes YAML files
kompose convert -f ./docker/docker-compose.yml -o ./kubernetes/

# Remove -tcp suffix from loadbalanced services
find kubernetes/ -type f -name "*-tcp-service.yaml" -exec sed -i 's/\(name: \)\(.*\)-tcp/\1\2/g' {} \;

# Replace 'latest' with the specified branch name in the Kubernetes configuration files, if the branch name is not 'latest'
if [ "$BRANCH_NAME" != "latest" ]; then
  find kubernetes/ -type f -exec sed -i "s/:latest/:$BRANCH_NAME/g" {} \;
fi

# Show the Kubernetes configuration
kubectl config view

# Apply the Kubernetes configuration
kubectl apply -f ./kubernetes/

# Wait for user input before cleaning up
echo "Pull down on user input:"
read userInput

# Remove the applied Kubernetes objects
kubectl delete -f ./kubernetes/