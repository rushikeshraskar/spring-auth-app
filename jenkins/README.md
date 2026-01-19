# Jenkins Docker Setup

This folder contains a separate Docker Compose configuration for Jenkins that persists independently from the main application services.

## Usage

### Start Jenkins
```bash
cd jenkins
docker-compose up -d
```

### Stop Jenkins (without deleting data)
```bash
cd jenkins
docker-compose stop
```

### Stop and remove containers (Jenkins data persists in volume)
```bash
cd jenkins
docker-compose down
```

### Stop and remove everything including Jenkins data
```bash
cd jenkins
docker-compose down -v
```

## Benefits

- **Independent Lifecycle**: Jenkins can be managed separately from app and database services
- **Persistent Data**: Running `docker-compose down -v` from the main directory won't affect Jenkins
- **Easy Cleanup**: Only delete main services without losing Jenkins configuration and build history
- **Isolation**: Jenkins volumes are not affected by main application stack updates

## Note

The main `docker-compose.yml` in the root directory no longer includes Jenkins. Manage app services and databases separately:

```bash
# From root directory - manages MSSQL and Spring Auth App only
docker-compose up -d
docker-compose down -v  # Safe: won't delete Jenkins

# From jenkins directory - manages Jenkins only
cd jenkins
docker-compose up -d
cd ..
```
test change 2