#!/bin/sh

# You could probably do this fancier and have an array of extensions
# to create, but this is mostly an illustration of what can be done

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<EOF
    CREATE EXTENSION IF NOT EXISTS hstore;
    CREATE EXTENSION IF NOT EXISTS btree_gin;
    CREATE EXTENSION IF NOT EXISTS btree_gist;
    CREATE EXTENSION IF NOT EXISTS file_fdw;
    CREATE EXTENSION IF NOT EXISTS orafce;
    CREATE EXTENSION IF NOT EXISTS postgres_fdw;
    CREATE EXTENSION IF NOT EXISTS postgis;
    CREATE EXTENSION IF NOT EXISTS address_standardizer;
    CREATE EXTENSION IF NOT EXISTS ogr_fdw;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    SELECT * FROM PG_EXTENSION;
EOF

mv -fv /tmp/postgresql.conf /var/lib/postgresql/data
