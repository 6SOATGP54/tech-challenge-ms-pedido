#!/bin/bash

if command -v kubectl &> /dev/null
then
	echo "Starting cluster..."
else
	echo "Error. Please install Kubectl"
	echo "visit: https://kubernetes.io/docs/reference/kubectl/kubectl/"
	exit 1
fi

# Secrets
kubectl apply -f kubernetes/rabbit.secret.yaml
kubectl apply -f kubernetes/mongo.secret.yaml

# Deployments
kubectl apply -f kubernetes/app.deployment.yaml
kubectl apply -f kubernetes/mongo.deployment.yaml

# Services
kubectl apply -f kubernetes/mongodb-service.yaml

# PVC
kubectl apply -f kubernetes/mongodb-pvc.yaml
