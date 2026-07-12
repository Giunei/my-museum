-- Align completed/status/finished_at that drifted (wishlist vs library bugs).
-- Items with a finish date or completed=true must be COMPLETED.
UPDATE user_media
SET status = 'COMPLETED',
    completed = TRUE
WHERE finished_at IS NOT NULL
   OR completed = TRUE;

-- Steam imports / rows without status become explicit PENDING wishlist/library entries.
UPDATE user_media
SET status = 'PENDING',
    completed = FALSE
WHERE status IS NULL;

-- Non-completed statuses should not keep a finish date.
UPDATE user_media
SET finished_at = NULL,
    completed = FALSE
WHERE status IN ('PENDING', 'IN_PROGRESS', 'ABANDONED')
  AND finished_at IS NOT NULL;

-- Keep user_game.status in sync with linked user_media.
UPDATE user_game ug
SET status = um.status
FROM user_media um
WHERE ug.user_media_id = um.id
  AND (ug.status IS NULL OR ug.status IS DISTINCT FROM um.status);
