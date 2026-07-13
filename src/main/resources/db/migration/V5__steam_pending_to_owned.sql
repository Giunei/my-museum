-- Jogos Steam marcados como PENDING (wishlist) passam a OWNED (biblioteca)
UPDATE user_media um
SET status = 'OWNED'
FROM user_game ug
WHERE ug.media_id = um.id
  AND ug.steam_app_id IS NOT NULL
  AND um.status = 'PENDING';

UPDATE user_game ug
SET status = 'OWNED'
WHERE ug.steam_app_id IS NOT NULL
  AND ug.status = 'PENDING';
