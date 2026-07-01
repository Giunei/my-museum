CREATE TABLE lol_account
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              BIGINT       NOT NULL UNIQUE,
    puuid                VARCHAR(100) NOT NULL,
    game_name            VARCHAR(100) NOT NULL,
    tag_line             VARCHAR(20)  NOT NULL,
    platform             VARCHAR(10)  NOT NULL,
    summoner_level       INTEGER,
    profile_icon_id      INTEGER,
    solo_tier            VARCHAR(20),
    solo_rank            VARCHAR(5),
    solo_league_points   INTEGER,
    solo_wins            INTEGER,
    solo_losses          INTEGER,
    flex_tier            VARCHAR(20),
    flex_rank            VARCHAR(5),
    flex_league_points   INTEGER,
    flex_wins            INTEGER,
    flex_losses          INTEGER,
    last_rank_refresh_at TIMESTAMP,
    created_at           TIMESTAMP,
    updated_at           TIMESTAMP,
    CONSTRAINT fk_lol_account_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_lol_account_user_id ON lol_account (user_id);
CREATE INDEX idx_lol_account_puuid ON lol_account (puuid);
