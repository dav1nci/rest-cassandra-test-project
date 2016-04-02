CREATE KEYSPACE IF NOT EXISTS example WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;
CREATE TABLE IF NOT EXISTS greeting (
    user text,
    id timeuuid,
    greet text,
    creation_date timestamp,
    PRIMARY KEY (user, id)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE TABLE IF NOT EXISTS device (
    id timeuuid,
    is_empty int,
    PRIMARY KEY (id)
) WITH CLUSTERING ORDER BY (id DESC);