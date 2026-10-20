#!/usr/bin/env bash
# Creates the webstore database: dump, users, then modifications.
# Depends on existence of sql/00_users.local.sql for user passwords, and .env for database name.
set -e
cd "$(dirname "$0")"

[ -f 00_users.local.sql ] || { echo "Missing sql/00_users.local.sql" >&2; exit 1; }

[ -f ../.env ] || { echo "Missing .env" >&2; exit 1; }
DB=$(grep '^WEBSTORE_DB=' ../.env | cut -d= -f2-)
[ -n "$DB" ] || { echo "Missing WEBSTORE_DB in .env" >&2; exit 1; }

mariadb -e "CREATE DATABASE $DB"

for script in \
  schema_populated_dump.sql \
  00_users.local.sql \
  01_indexes.sql \
  02_views.sql \
  03_trigger_no_negative_stock.sql \
  04_event_cancel_old_orders.sql \
  05_products_price_history.sql \
  06_views_current_products.sql \
  07_products_price_history_data.sql
do
  echo "$script"
  mariadb "$DB" < "$script"
done
