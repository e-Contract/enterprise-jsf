#
# Enterprise JSF project.
#
# Copyright 2022-2023 e-Contract.be BV. All rights reserved.
# e-Contract.be BV proprietary/confidential. Use is subject to license terms.
#

DOCKER_USERNAME ?= e-contract.be
APPLICATION_NAME ?= enterprise-jsf-wildfly

JEE8_VERSION_TAG=ee8
JEE10_VERSION_TAG=ee10

.PHONY:run-ee8
run-ee8: docker-start-ee8 app-deploy-ee8

.PHONY:run-ee10
run-ee10: docker-start-ee10 app-deploy-ee10

.PHONY:run-spring
run-spring: app-start-spring

.PHONY: doc-with-docker
doc-with-docker:
	@docker build -t e-contract.be/enterprise-jsf-doc -f docker/Dockerfile.build-doc .
	@docker run --rm -v ${PWD}:/build/ e-contract.be/enterprise-jsf-doc
	@echo " > > > > > > Documentation can be found here: doc/enterprise-jsf.pdf"

.PHONY: docker-start-ee8
docker-start-ee8: docker-stop docker-build-ee8
	docker run --rm --name ${DOCKER_USERNAME}_${APPLICATION_NAME} -p 8080:8080 -p 9990:9990 -p 9999:9999 -d ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE8_VERSION_TAG}

.PHONY: docker-start-ee10
docker-start-ee10: docker-stop docker-build-ee10
	docker run --rm --name ${DOCKER_USERNAME}_${APPLICATION_NAME} -p 8080:8080 -p 9990:9990 -p 9999:9999 -d ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE10_VERSION_TAG}

.PHONY: docker-build-ee8
docker-build-ee8:
	docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE8_VERSION_TAG} -f docker/Dockerfile.ee8 docker

.PHONY: docker-build-ee10
docker-build-ee10:
	docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE10_VERSION_TAG} -f docker/Dockerfile.ee10 docker

.PHONY: app-build
app-build:
	mvn clean install

.PHONY: app-deploy
app-deploy-ee8: app-build
	cd ejsf-demo && mvn wildfly:deploy -Dwildfly.password=e-contract.be -Dwildfly.username=admin

.PHONY: app-deploy-ee10
app-deploy-ee10: app-build
	cd ejsf-demo-ee10 && mvn wildfly:deploy -Dwildfly.password=e-contract.be -Dwildfly.username=admin

# We stop the Docker containers here because they are also listening on port 8080, as will the spring-boot server
.PHONY: app-start-spring
app-start-spring: app-build docker-stop
	cd spring && mvn clean spring-boot:run

.PHONY: docker-stop
docker-stop:
	docker kill ${DOCKER_USERNAME}_${APPLICATION_NAME} 2> /dev/null || echo Already killed

.PHONY: app-clean
app-clean:
	mvn clean

.PHONY: mrproper
mrproper: docker-stop
	@echo "Going to remove the image..."
	docker image rm ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE8_VERSION_TAG} 2> /dev/null || echo "Might be already removed..."
	docker image rm ${DOCKER_USERNAME}/${APPLICATION_NAME}:${JEE10_VERSION_TAG} 2> /dev/null || echo "Might be already removed..."

.PHONY: clean
clean: docker-stop app-clean
