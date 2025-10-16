CREATE TABLE IF NOT EXISTS home_slots(
    player_uuid TEXT PRIMARY KEY,
    slot_id INT NOT NULL DEFAULT 0
);