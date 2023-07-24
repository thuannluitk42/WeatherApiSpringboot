# WeatherApiSpringboot
<strong>Project goal</strong>:
Apply springboot knowledge learned from udemy to personal projects.

<strong>Technique:</strong>
 - Programming language: Java 17
 - Framework: Springboot
 - Database: PostgreSQL
 - IP2Location: is a non-intrusive IP location lookup technology that retrieves geolocation information with no explicit permission required from users.
 - IDE: SpringToolSuite4

<strong>Project structure:</strong>   multi-module structure.

Root project (weather-api-project-1) contains 2 modules WeatherApiCommon-1 and WeatherApiService-1.

<strong>WeatherApiCommon-1</strong>: contains information about project objects such as HourlyWeather, HourlyWeatherId,Location,RealtimeWeather.

<strong>WeatherApiService-1</strong>: will contain handlers for Exception, Controller (is responsible for processing incoming REST API requests, preparing a model, and returning the view to be rendered as a response), 
Repository (the class provides the mechanism for storage, retrieval, search, update and delete operation on objects), Service( allows developers to add business functionalities).

Below is an image showing the project structure:

	<modules>
		<module>WeatherApiCommon-1</module>
		<module>WeatherApiService-1</module>
	</modules>
 
![image](https://github.com/thuannluitk42/WeatherApiSpringboot/assets/66455360/10b267d8-7910-4210-a8f0-57285408a923)

WeatherApiSpringboot/pom.xml: 
The pom file of the root directory contains the configuration of the parent directory.

The <strong>spring-boot-starter-parent</strong> project is a special starter project that provides default configurations for our application and a complete dependency tree to quickly build our Spring Boot project. 
It also provides default configurations for Maven plugins, such as maven-failsafe-plugin, maven-jar-plugin, maven-surefire-plugin, and maven-war-plugin.

The <strong>Spring Boot Maven Plugin</strong> provides Spring Boot support in Apache Maven. It allows you to package executable jar or war archives, run Spring Boot applications,
generate build information and start your Spring Boot application prior to running integration tests.

Now we will go into the details of the project:

Module WeatherApiCommon-1: 

File WeatherApiSpringboot/WeatherApiCommon-1/pom.xml:

![image](https://github.com/thuannluitk42/WeatherApiSpringboot/assets/66455360/0c8a7bdc-6531-41e8-a6b8-936cac7274ee)

The spring-boot-starter-data-jpa is connect Spring application with relational database. It's provides three CrudRepository (create, read, update, and delete), 
PagingAndSortingRepository (sort and retrieve the data in a paginated way), JpaRepository (JPA specific repository, adds the JPA-specific methods, like flush() to trigger a flush on the persistence context.)

jackson-databind: General data-binding functionality for Jackson: works on core streaming API.

spring-boot-starter-validation: Used to provide validation APIs in the form of annotations. (@NotNull, @NotEmpty, @Email,@Valid, @Validated, ....).

The spring-boot-starter-test is the primary dependency that contains the majority of elements required for our tests.
