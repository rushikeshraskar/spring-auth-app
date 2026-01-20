package com.auth.app.config;

import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    // Database creation is now handled by Hibernate with ddl-auto=create-drop
    // MSSQL Docker container must have authdb created by init.sql script
}

