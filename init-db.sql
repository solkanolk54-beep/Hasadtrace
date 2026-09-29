-- FarmTrace Database Initialization Script
-- Executed automatically on the first boot of the PostgreSQL container

-- Enable UUID generation support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable PostGIS geospatial support for coordinates & polygons (GIS Farm Mapping)
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE farmtrace_db TO farmtrace_user;
