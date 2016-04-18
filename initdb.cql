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
);

CREATE TABLE IF NOT EXISTS doctor (
    email text,
    password text,
    id timeuuid,
    surname text,
    name text,
    registration_date timestamp,
    birth_day timestamp,
    speciality text,
    PRIMARY KEY (email, id, surname, name)
) WITH CLUSTERING ORDER BY (id DESC);

CREATE TABLE IF NOT EXISTS patient (
    email text,
    password text,
    surname text,
    name text,
    doctor_id timeuuid,
    device_id timeuuid,
    registration_date timestamp,
    analyses list<text>,
    diagnosis map<text, double>,
    PRIMARY KEY (email, password,
    surname, name)
) WITH CLUSTERING ORDER BY (password DESC);

    CREATE TABLE IF NOT EXISTS general_blood_analysis (
    patient_email text,
    day text,
    event_time timestamp,
    analyses map<text, double>,
    PRIMARY KEY ((patient_email, day), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);

CREATE TABLE IF NOT EXISTS patients_by_doctor (
    doctor_id timeuuid,
    surname text,
    name text,
    email text,
    PRIMARY KEY (doctor_id, surname, name)
) WITH CLUSTERING ORDER BY (surname DESC);


{
    "email" : "qqweqwe",
    "password" : "aaaaaa",
    "doctorId" : "9fcbdce0-f991-11e5-919c-f7db3580a3b7",
    "surname" : "Qsde",
    "name" : "Dsda",
    "deviceId" : "1ac43350-f994-11e5-b0c8-f7db3580a3b7"
}