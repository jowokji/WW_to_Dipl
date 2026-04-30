#!/usr/bin/env bash
set -e

: "${APP_DB_USER:?APP_DB_USER is required}"
: "${APP_DB_PASSWORD:?APP_DB_PASSWORD is required}"

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  -v app_db_user="$APP_DB_USER" \
  -v app_db_password="$APP_DB_PASSWORD" <<'EOSQL'
SELECT 'CREATE ROLE app_read NOLOGIN'
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_read');
\gexec

SELECT 'CREATE ROLE app_write NOLOGIN'
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_write');
\gexec

SELECT 'CREATE ROLE app_admin NOLOGIN'
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_admin');
\gexec

GRANT app_read TO app_write;
GRANT app_write TO app_admin;

SELECT 'GRANT CONNECT ON DATABASE ' || quote_ident(current_database()) || ' TO app_read, app_write, app_admin';
\gexec

GRANT USAGE ON SCHEMA public TO app_read, app_write, app_admin;
GRANT CREATE ON SCHEMA public TO app_write, app_admin;

SELECT 'CREATE ROLE ' || quote_ident(:'app_db_user') ||
       ' LOGIN PASSWORD ' || quote_literal(:'app_db_password') ||
       ' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION'
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = :'app_db_user'
);
\gexec

SELECT 'ALTER ROLE ' || quote_ident(:'app_db_user') ||
       ' PASSWORD ' || quote_literal(:'app_db_password');
\gexec

GRANT app_write TO :"app_db_user";
EOSQL
