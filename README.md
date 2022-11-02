# Nate Hausfater
## hausfat1@uic.edu
CS441 Homework 2. Restful API

Youtube videos: https://www.youtube.com/watch?v=l_LnD0yQduc&list=PLpCHxrtVwRiCi2e79_yb8zKs7ZyFPKi-9&index=1

first video: https://youtu.be/l_LnD0yQduc
GRPC video: https://youtu.be/48ylMDRqMxI
RESTful video: https://youtu.be/A-BPLFpuMWU



My project is composed of four programs: 'homework2restart' -> which is the gRPC client, 'Homework2Server' -> which is the gRPC server, 'Homework2RESTful' -> which the restful client, and 'RESTFULSERVER' -> which is the RESTful lambda.

Each homework uses a Protobuf in order to define the objects that will be passed back and forth. Each can be run as an individual project in IntelliJ. RESTFULSERVER must be hosted by an AWS lambda with an API Gateway forwarding JSON strings to it.
