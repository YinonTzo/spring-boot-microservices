# spring-boot-microservices

This project is aimed at learning Docker and Kubernetes by implementing a microservices architecture. <br/>

# Microservices Overview
1) <b>Cloud Gateway -</b> Serves as the entry point for the project.
2) <b>Order Service -</b> Responsible for creating an order, calling the payment service and product service for payment and decreasing product. 
3) <b>Payment Service -</b> Handles payment transactions.
4) <b>product Gateway -</b> Manages product-related operations.
</br>
 
Each microservice has its own database and communicates with each other via HTTP calls using the WebClient object. 
To prevent redundant calls, a circuit breaker has been implemented.

# Running the Project
You can run this project using either docker compose or kubernetes. </br>

<b3><b>Docker Compose:</b> Use the following commands: </b3>
<ul>
<li>Start: <code>docker compose up</code> </li>
</ul>

<b3><b>For kubernetes:</b> The project uses **kubectl** and **minikube**. </b3></br>
run:
1) Start Minikube from the command line: <code>minikube start</code>.
2) Access the Kubernetes dashboard: <code>minikube dashboard</code>.
3) Navigate to the project directory and apply the Kubernetes configurations: <code>kubectl apply -f .\k8s\ </code>.
4) Expose the Cloud Gateway service by running the command: <code>minikube service cloud-gateway-svc</code>. It will give you a port. </br>
<b> Let's assume that the command above gives us the port 9090. </b>

With the project up and running, let's add some products.
The process is the same for Docker and Kubernetes, except for the port number.</br>

From Postman, send a **POST** request to the product service:
<ul>
<li>The uri: <code>http://localhost:9090/product</code> </li>
<li>and the body request (as json):</li>
<code>
    {</br>
        "productName" : "Product 1",</br>
        "price" : 100,</br>
        "quantity" : 10</br>
    }
</code>
</ul>

You should receive a response with status 201 (Created) and the product details.</br>

Now, let's create an order. Send a **POST** request to the order service: <br>
<ul>
<li>The uri: <code>http://localhost:9090/order/placeOrder</code> </li>
<li>and the body request (as json):</li>
<code>
   { </br>
        "productId": 1, </br>
        "quantity": 7, </br>
        "PaymentMode": "CASH" </br>
    }
</code>
</ul>

You should receive a response with status 200 (OK) and the order details.

To view the order details, send a **GET** request to the following URL: 
<code>
    http://localhost:9090/order/3
</code>
</br>
You should receive a response containing the order, product, and payment details.

# Build and push

To build and push the services, follow these steps:
1) run <code>mvn clean install</code> to build the project.
2) Use the command <code>docker build -t  yinontz/cloud-gateway:0.0.1 .</code>, replacing <code>yinontz</code>
   with your Docker Hub username and <code>cloud-gateway</code> with the desired service name.
3) Push the container to Docker Hub using <code>docker push -t yinontz/cloud-gateway:0.0.1</code>,
   This will make it available for Kubernetes to pull.
4) Finally, you can run the project using Docker Compose or Kubernetes, as mentioned earlier.

**Note**: </br>
In the Cloud Gateway and Order Service, the tag convention follows this pattern: **0.0.X** is for Docker Compose,
and **1.0.X** is for Kubernetes.</br>
Before building and pushing the containers, ensure that the injected URLs are correctly configured. </br>
Check the application.yaml files in the Cloud Gateway and Order Service, where the injected URLs are marked
for both Kubernetes and Docker.</br>
For Kubernetes, the URLs should be in the format
<code>
    http://some-service-svc
</code>,  while for Docker,
they should be <code>http://${some-service:localhost}:8083/some-service</code>.