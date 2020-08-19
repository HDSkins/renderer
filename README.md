This project is a fork of the wonderful project https://github.com/unascribed-archive/Visage by Una Thompson (Copyright holder) which is licensed under the MIT license.

# Multiple renderers

For the usage of multiple renderers, you can start the server multiple times. All servers have to connect to one RabbitMQ Server and then clients 
can send render requests to the servers.

The clients can use the following dependency:
```xml
        <dependency>
            <groupId>de.hdskins</groupId>
            <artifactId>renderer-client</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
Now you'll only need to create a new instance of the SkinRenderClient and you can send requests.

# Usage without RabbitMQ

For the usage without a RabbitMQ Server, you'll just need the following dependency:
```xml
        <dependency>
            <groupId>de.hdskins</groupId>
            <artifactId>renderer-client</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
Now you can create a new instance of the RenderContext class and send a request with the queueRequest method.
