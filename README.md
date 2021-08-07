# Motivation

## Tracing

## Swagger generation from code



# Architecture
//<image>

## Company-info app

## Company-prices app

## Entrypoint app


# Implementation details


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
