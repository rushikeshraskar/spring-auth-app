-- Create authdb database if it doesn't exist
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'authdb')
BEGIN
    CREATE DATABASE authdb;
END
GO

-- Use the authdb database
USE authdb;
GO
