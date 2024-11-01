# Wolt Junior Software Engineer Programme 2024

## Overview

This is a Spring Boot application built using Kotlin and Maven. It provides an API service to calculate delivery order prices based on various criteria such as venue location, user location, and cart value.

## Prerequisites

Before running the application, ensure that you have the following installed on your system:

- Java 17 or later (ensure `JAVA_HOME` is set correctly)
- Maven 3.6 or later
- Internet connection for downloading dependencies

## Installation and Running Instructions

### Step 1: Unzip the Project

First, unzip the project file to your desired directory:

```bash
unzip wolt-junior-software-engineer.zip
cd wolt-junior-software-engineer
```

### Step 2: Build the Project

Run the following Maven command to build the project and download all necessary dependencies:

```bash
mvn clean install
```

### Step 3: Run the Application

After building the project, you can run the Spring Boot application using the following command:

```bash
mvn spring-boot:run
```
By default, the application will run on port `8000` as defined in the application.properties file.

### Step 4: Test the Application

Once the application is running, you can test the service using curl or any API testing tool like Postman. Here’s an example using `curl`

```bash
curl "http://localhost:8000/api/v1/delivery-order-price?venue_slug=home-assignment-venue-helsinki&cart_value=1000&user_lat=60.17094&user_lon=24.93087"
```
This should return a response with the calculated delivery order price based on the input parameters and some additional parameters.

example of output:
```
{
"total_price": 1190,
"small_order_surcharge": 0,
"cart_value": 1000,
"delivery": {
"fee": 190,
"distance": 177
}
}
```

### Step 5: Shutdown 

To stop the application, press Ctrl + C in the terminal where the application is running.


### Additional Notes

- Ensure the port 8000 is available on your machine or change the port in the `application.properties` file located in the `src/main/resources` folder before running the application.
- If using a different port, update the curl commands accordingly.
- This project is built using **Spring Boot**, **Kotlin**, and **Maven**.
- For more advanced configurations or further customization, refer to the application properties located in `src/main/resources/application.properties`.
- Ensure that your Java version is compatible with the project requirements. The project is built with Java 17 or newer.

