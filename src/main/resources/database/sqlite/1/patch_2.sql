CREATE TABLE home_new
(
    player_id TEXT,
    id TEXT,
    world TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL,
    CONSTRAINT location_pk
        PRIMARY KEY (player_id, id)
);

INSERT INTO home_new(player_id, id, world, x, y, z, yaw, pitch)
SELECT player_id, id, world, CAST(x AS REAL), CAST(y AS REAL), CAST(z AS REAL), yaw, pitch FROM home;

DROP TABLE home;

ALTER TABLE home_new RENAME TO home;

CREATE TABLE location_new
(
    id TEXT PRIMARY KEY,
    world TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL
);

INSERT INTO location_new(id, world, x, y, z, yaw, pitch)
SELECT id, world, CAST(x AS REAL), CAST(y AS REAL), CAST(z AS REAL), yaw, pitch FROM location;

DROP TABLE location;

ALTER TABLE location_new RENAME TO location;

CREATE TABLE staff_last_location_new
(
    player_id TEXT PRIMARY KEY,
    world TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL
);

INSERT INTO staff_last_location_new(player_id, world, x, y, z, yaw, pitch)
SELECT player_id, world, CAST(x AS REAL), CAST(y AS REAL), CAST(z AS REAL), yaw, pitch FROM staff_last_location;

DROP TABLE staff_last_location;

ALTER TABLE staff_last_location_new RENAME TO staff_last_location;
