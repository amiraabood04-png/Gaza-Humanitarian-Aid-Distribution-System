# Gaza-Humanitarian-Aid-Distribution-System
# GHADS - Gaza Humanitarian Aid Distribution System

## System Purpose
GHADS is a desktop application designed to organize humanitarian aid distribution for families in Gaza.

## Problem Solved
The system helps reduce duplicate aid distribution and keeps records of families, organizations, users, and aid distribution processes.

## Technologies Used
- Java
- JavaFX
- MySQL
- JDBC
- NetBeans
- Scene Builder

## Architecture Pattern
The project follows the MVC architecture pattern.

- Model: represents system data such as User, Family, Organization, and AidDistribution.
- View: represents the FXML user interfaces.
- Controller: handles user actions and connects the interface with the database.

The project also uses DAO classes to separate database operations from the controllers.

## Main Features
- Login system with Admin and Coordinator roles
- Manage organizations
- Manage users
- Manage families
- Record aid distributions
- Duplicate check for aid distribution
- Search and filtering
- Change password
- Profile management

