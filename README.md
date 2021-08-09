# Motivation

As usual, I wanted to try new technologies and have up-to-date code examples written by me that might help me during a job interview.

Most of my career I have worked with spark.
As new technologies I decided to try something from the standard backend stack - rest api and microservices.

## Tracing

The main aim.
Let's say we have a product based on microservices. Our customer can click on a button - the button will call microservice A that service in its turn will call microservices B and C, B can interact with a database, and so on.
If in the result our customer got an error or waited for too long - how can we find out where is the problem?

Of course, we can check logs but it will be painful:
- we need to check logs of different services
- we need to collect events from them into a single chain responsible for exactly our customer's request (if we work under load it can be almost impossible because of too many requests)

Things like [Jaeger](https://www.jaegertracing.io/) can help us to simplify this process.

To interact with jaeger let's use [trace4cats](https://github.com/trace4cats/trace4cats).

## Swagger generation from code

The secondary aim.
To share api details with other teams we often use swagger. If we write both rest api and swagger ourselves it leads to problems:
- code duplication (api implementation and its description)
- a place for a mistake. You can change implementation and forget to change swagger or vice versa

Can we somehow improve it? [Tapir](https://github.com/softwaremill/tapir) says we can. Let's try


# Architecture
//<image>

## Company-info app

## Company-prices app

## Entrypoint app


# Implementation details

# Results

## Tracing

## Swagger generation

# Want to try it locally?

If you want to try it locally you need:

1. installed docker

2. start applications via
```bash
sbt companyInfo/run
sbt companyPrices/run
sbt entryPoint/run
```

3. start jaeger (full details [here](https://www.jaegertracing.io/docs/1.22/getting-started/#all-in-one))
```bash
docker run -d --name jaeger \
  -p 6831:6831/udp \
  -p 16686:16686 \
  jaegertracing/all-in-one:1.22
```

4. open in a web browser [jager](http://localhost:16686/search) and [entry point swagger](http://localhost:8081/docs).
   Use default auth Bearer token="123"  

# Other notes

This project is a POC with a very limited number of aims described in the [motivation](#motivation).

It doesn't need long-term support. That's why:
- there are no tests and configuration via the standard typesafe config
- several todo's were not fixed
- two similar libraries in the classpath (`cats-retry` vs `org.http4s.client.middleware.Retry`)

Most probably there are some other non-optimal decisions that could be done better but again these non-optimal decisions
are outside the main project aims.
