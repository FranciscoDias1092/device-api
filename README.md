# device-api

This application provides a REST API that allows users to create, update, partially update, get, and delete devices.

## Run containers:
- To build and start the containers: docker-compose up --build
- To start without rebuilding: docker-compose up

## API documentation
Once the application is running, the documentation can be accessed at:
http://localhost:8080/swagger-ui/index.html.

## Features:

### Create a new device:
- Users can create a device by providing the device's name, brand, state (AVAILABLE, IN_USE, INACTIVE), and creation time.
- The name brand pair must be unique.
- If successfully created the device is returned.
- If a device with the same name brand pair already exists in the database, the user is notified.

### Update a device:
- Users can update a device by providing the device's name, brand, state (AVAILABLE, IN_USE, INACTIVE), and creation time.
- Operation is idempotent. If the device is IN USE, it can still be updated.
- If successfully updated the device is returned.
- If any of the properties are missing, the user is notified.

### Partially update a device:
- Users can update a device by providing the device's name, brand, and state (AVAILABLE, IN_USE, INACTIVE).
- If successfully updated the device is returned.
- If the device is IN USE it is not updated unless the new state is different from IN_USE and an error message (CONFLICT) is returned.

### Get a device:
- Users can fetch a device by its id.
- If the device exists, it's returned.
- If the device doesn't exist, an error message (NOT_FOUND) is returned.

### Get devices:
- Users can fetch all devices or a list of devices filtered by Brand and/or State.
- If no devices exist that match the criteria, an error message (NOT_FOUND) is returned.

### Delete a device:
- Users can delete a devices by its id.
- If the device is IN USE, an error message is returned.
- If the device doesn't exist, an error message (NOT_FOUND) is returned.

## Call services

### Create device:

#### Endpoint: POST /api/v1/devices

#### Body example:
{
  "name": "SomeName",
  "brand": "Brand 2",
  "state": "available",
  "creationTime": "2022-01-23"
}

#### Output example:
{
    "id": 3,
    "name": "SomeName",
    "brand": "Brand 2",
    "state": "AVAILABLE",
    "creationTime": "2025-03-31"
}

### Update a device:

#### Endpoints: PUT /api/v1/devices/{id}

#### Body example:
{
  "name": "Device 4",
  "brand": "Brand 3",
  "state": "available",
  "creationTime": "2022-01-23"
}

#### Output example:
{
    "id": 3,
    "name": "Device 4",
    "brand": "Brand 3",
    "state": "AVAILABLE",
    "creationTime": "2025-03-31"
}

### Partially update a device:

#### Endpoints: PATCH /api/v1/devices/{id}

#### Body example:
{
  "brand": "Brand 5",
  "state": "available"
}

#### Output example:
{
    "id": 3,
    "name": "Device 4",
    "brand": "Brand 5",
    "state": "AVAILABLE",
    "creationTime": "2025-03-31"
}

 
### Get a device:

#### Endpoints: GET /api/v1/devices/{id}

#### Output example:
{
    "id": 3,
    "name": "SomeName",
    "brand": "Brand 2",
    "state": "AVAILABLE",
    "creationTime": "2025-03-31"
}

### Get devices:

#### Endpoints: GET /api/v1/devices?brand=Brand 3

#### Output example:
[
    {
        "id": 1,
        "name": "Device 4",
        "brand": "Brand 3",
        "state": "IN_USE",
        "creationTime": "2025-03-31"
    },
    {
        "id": 2,
        "name": "Device 2",
        "brand": "Brand 3",
        "state": "IN_USE",
        "creationTime": "2025-03-31"
    }
]

### Get devices:

#### Endpoints: DELETE /api/v1/devices/{id}

#### Output example:
No return (NO_CONTENT)
