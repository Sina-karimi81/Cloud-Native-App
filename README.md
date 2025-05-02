# Cloud Native Application  

## Overview  
This project is a **cloud-native image generation application** built using **Java Spring Boot**, **Docker**, and **RabbitMQ**. The application is a combination of three microservices and it connects to thrid-party services such as image-to-text and text-to-image and an object store. the client first uploads an image which we store in an object store. Then a scheduler in another services pickups the uploaded image, passes it to a hugging face API and generates a text as a response. we store that text and pass to another hugging face API to generate a similar image from it. then the image is stored and a link to it is sent to the client via an email service.

## Features  
- **Image Storage**: Store your images in an object store.  
- **Message brokers**: Uses RabbitMQ to send events to other services.  

## API Endpoints  
| Method | Endpoint | Description |  
|--------|------------------------|-----------------------------|  
| `POST` | `/submit` | Register an image |  
| `GET` | `/status` | Check the status of the request |  

## Technologies Used  
- **Java 17**, **Spring Boot**  
- **Amazon S3 SDK**  
- **RabbitMQ**  
- **Databases**  

## Deployment  

### Prerequisites  
- Docker

### Running with Docker Compose  
1. Clone the repository:  
   ```sh
   git clone https://github.com/sina-karimi81/cloud-native-app.git
   cd cloud-native-app
   ```  
2. Start the services using Docker Compose:  
   ```sh
   docker-compose up -d
   ```  
