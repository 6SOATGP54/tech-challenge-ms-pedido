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
kubectl delete -f kubernetes/rabbit.secret.yaml
kubectl delete -f kubernetes/mongo.secret.yaml

# Deployments
kubectl delete -f kubernetes/app.deployment.yaml
kubectl delete -f kubernetes/mongo.deployment.yaml

# Services
kubectl delete -f kubernetes/mongodb-service.yaml
kubectl delete -f kubernetes/postgres.service.yaml
kubectl delete -f kubernetes/app-food-service.yaml

# PVC
kubectl delete -f kubernetes/mongodb-pvc.yaml
