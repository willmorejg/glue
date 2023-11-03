# Project Title

## Table of Contents

- [Project Title](#project-title)
  - [Table of Contents](#table-of-contents)
  - [About ](#about-)
  - [Getting Started ](#getting-started-)
    - [Prerequisites](#prerequisites)
    - [Installing](#installing)
  - [Usage ](#usage-)

## About <a name = "about"></a>

Experimentation using Docker, Spring Boot, Apache Camel, ActiveMQ (Artemis), Kafka, and PostgreSQL.

## Getting Started <a name = "getting_started"></a>

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See [deployment](#deployment) for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them.

```
Java 11
```

### Installing

TBD

## Usage <a name = "usage"></a>

I've run the following command to get up and running:
```
./gradlew clean build bootBuildImage -x test && docker-compose build && docker-compose up -d --force-recreate
```