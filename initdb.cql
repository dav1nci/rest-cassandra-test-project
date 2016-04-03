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
    PRIMARY KEY (id, is_empty)
) WITH CLUSTERING ORDER BY (is_empty DESC);

CREATE TABLE IF NOT EXISTS doctor (
    email text,
    password text,
    id timeuuid,
    surname text,
    name text,
    registration_date timestamp,
    PRIMARY KEY (email, id, surname, name)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE TABLE IF NOT EXISTS patient (
    email text,
    password text,
    doctor_id timeuuid,
    surname text,
    name text,
    device_id timeuuid,
    registration_date timestamp,
    diagnosis map<text, double>,
    analysis map<text, double>,
    PRIMARY KEY (email, password,
    doctor_id, surname, name)
) WITH CLUSTERING ORDER BY (password DESC);


{
    "email" : "qqweqwe",
    "password" : "aaaaaa",
    "doctorId" : "9fcbdce0-f991-11e5-919c-f7db3580a3b7",
    "surname" : "Qsde",
    "name" : "Dsda",
    "deviceId" : "1ac43350-f994-11e5-b0c8-f7db3580a3b7"
}