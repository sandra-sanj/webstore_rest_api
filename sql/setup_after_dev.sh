#!/usr/bin/env bash
# Runs the scripts that need the tables the dev profile creates.
set -e
cd "$(dirname "$0")"

[ -f ../.env ] || { echo "Missing .env" >&2; exit 1; }
DB=$(grep '^WEBSTORE_DB=' ../.env | cut -d= -f2-)
[ -n "$DB" ] || { echo "Missing WEBSTORE_DB in .env" >&2; exit 1; }

for script in \
  08_permissions.sql \
  09_productsuppliers_data.sql
do
  echo "$script"
  mariadb "$DB" < "$script"
done
