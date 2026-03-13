SHELL := /bin/bash

.PHONY: infra-up infra-down build run-gateway run-telemetry run-orchestrator run-identity

infra-up:
	docker compose up -d zookeeper kafka redis postgres keycloak

infra-down:
	docker compose down

build:
	mvn -q -DskipTests clean package

run-gateway:
	mvn -q -pl gateway-service spring-boot:run

run-telemetry:
	mvn -q -pl telemetry-service spring-boot:run

run-orchestrator:
	mvn -q -pl orchestrator-service spring-boot:run

run-identity:
	mvn -q -pl identity-service spring-boot:run
