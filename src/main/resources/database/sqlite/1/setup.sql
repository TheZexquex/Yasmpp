CREATE TABLE IF NOT EXISTS home
(
    player_id TEXT,
    id TEXT,
    world TEXT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL,
    CONSTRAINT location_pk
        PRIMARY KEY (player_id, id)
);

CREATE TABLE IF NOT EXISTS location
(
    id TEXT PRIMARY KEY,
    world TEXT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS staff_last_location
(
    player_id TEXT PRIMARY KEY,
    world TEXT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS staff_player
(
    player_id TEXT PRIMARY KEY,
    staffmode_enabled INTEGER NOT NULL,
    survival_inv TEXT
);

CREATE TABLE IF NOT EXISTS game
(
    is_spawn_set INTEGER NOT NULL DEFAULT 0,
    game_state TEXT NOT NULL DEFAULT 'SETUP'
);