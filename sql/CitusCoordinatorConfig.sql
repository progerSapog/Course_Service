CREATE DATABASE course_service;

-- Переключиться на созданную базу!!!

CREATE EXTENSION citus;

SELECT citus_set_coordinator_host('192.168.122.1', 5432);

SELECT citus_add_node('192.168.122.133', 5432);
SELECT citus_add_node('192.168.122.221', 5432);
SELECT citus_add_node('192.168.122.150', 5432);

SELECT citus_set_default_rebalance_strategy('by_disk_size');