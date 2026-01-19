#!/bin/bash

# Start MSSQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to be ready
echo "Waiting for SQL Server to start..."
sleep 2

# Run the init script
echo "Creating authdb database..."
/opt/mssql-tools/bin/sqlcmd -S 127.0.0.1 -U sa -P testPassword123 -i /var/opt/mssql/scripts/init.sql

# Keep the container running
wait
