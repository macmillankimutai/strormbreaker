Wildlife Tracker

Description
A web based application that is responsible for tracking animals in tha game park.

Setup/Installation Requirements
For setup you need:
CREATE DATABASE wildlife_tracker;
CREATE TABLE animals (id serial PRIMARY KEY, name varchar, species varchar, health varchar, age varchar, endangered boolean);
CREATE TABLE sightings (id serial PRIMARY KEY, location varchar, rangerName varchar, date_sighted timestamp);
CREATE TABLE animals_sightings (id serial PRIMARY KEY, animal_id int, sighting_id int);
gradle will download and install junit and spark
the 'gradle run' command will deploy the site to port 4567 by default
Technologies used
Java 1.8.0_101
Gradle 3.0
JUnit 4.+
Spark 2.3
Velocity Template Engine 1.7
Postgresql 9.6
Contributer
Macmillan kimutai
