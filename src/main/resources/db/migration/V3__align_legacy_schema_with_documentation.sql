-- Align databases that were originally created by Hibernate ddl-auto=update
-- with the documented Flyway schema.

DROP VIEW IF EXISTS v_user_recommendation_summary;
DROP VIEW IF EXISTS v_anonymized_users;

UPDATE chat_sessions
SET title = 'Style chat'
WHERE title IS NULL OR BTRIM(title) = '';

ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(30),
    ALTER COLUMN role SET DEFAULT 'USER',
    ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE user_preferences
    ALTER COLUMN style_preference TYPE VARCHAR(30),
    ALTER COLUMN style_preference SET DEFAULT 'CASUAL',
    ALTER COLUMN cold_sensitivity TYPE VARCHAR(20),
    ALTER COLUMN cold_sensitivity SET DEFAULT 'MEDIUM',
    ALTER COLUMN heat_sensitivity TYPE VARCHAR(20),
    ALTER COLUMN heat_sensitivity SET DEFAULT 'MEDIUM',
    ALTER COLUMN wind_sensitivity TYPE VARCHAR(20),
    ALTER COLUMN wind_sensitivity SET DEFAULT 'MEDIUM',
    ALTER COLUMN rain_sensitivity TYPE VARCHAR(20),
    ALTER COLUMN rain_sensitivity SET DEFAULT 'MEDIUM',
    ALTER COLUMN max_layers SET DEFAULT 3,
    ALTER COLUMN prefers_headwear SET DEFAULT FALSE,
    ALTER COLUMN prefers_waterproof SET DEFAULT FALSE,
    ALTER COLUMN activity_level TYPE VARCHAR(20),
    ALTER COLUMN activity_level SET DEFAULT 'MEDIUM',
    ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE weather_cache
    ALTER COLUMN city TYPE VARCHAR(120),
    ALTER COLUMN condition TYPE VARCHAR(50),
    ALTER COLUMN cached_at SET DEFAULT NOW();

ALTER TABLE recommendation_history
    ALTER COLUMN city TYPE VARCHAR(120),
    ALTER COLUMN weather_summary TYPE VARCHAR(1000),
    ALTER COLUMN recommendation_text TYPE TEXT,
    ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE user_feedback
    ALTER COLUMN feedback_type TYPE VARCHAR(30),
    ALTER COLUMN feedback_type SET DEFAULT 'RATING',
    ALTER COLUMN comment TYPE TEXT,
    ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE chat_sessions
    ALTER COLUMN title TYPE VARCHAR(255),
    ALTER COLUMN title SET DEFAULT 'Style chat',
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN created_at SET DEFAULT NOW();

ALTER TABLE chat_messages
    ALTER COLUMN role TYPE VARCHAR(30),
    ALTER COLUMN content TYPE TEXT,
    ALTER COLUMN created_at SET DEFAULT NOW();

CREATE OR REPLACE FUNCTION weatherwear_replace_fk_with_cascade(
    target_table TEXT,
    target_column TEXT,
    referenced_table TEXT,
    referenced_column TEXT,
    new_constraint_name TEXT
)
RETURNS VOID AS $$
DECLARE
    existing_constraint TEXT;
BEGIN
    FOR existing_constraint IN
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.constraint_schema = kcu.constraint_schema
        WHERE tc.constraint_schema = 'public'
          AND tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_name = target_table
          AND kcu.column_name = target_column
    LOOP
        EXECUTE FORMAT(
            'ALTER TABLE %I DROP CONSTRAINT %I',
            target_table,
            existing_constraint
        );
    END LOOP;

    EXECUTE FORMAT(
        'ALTER TABLE %I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES %I(%I) ON DELETE CASCADE',
        target_table,
        new_constraint_name,
        target_column,
        referenced_table,
        referenced_column
    );
END;
$$ LANGUAGE plpgsql;

SELECT weatherwear_replace_fk_with_cascade(
    'user_preferences',
    'user_id',
    'users',
    'id',
    'fk_user_preferences_user'
);

SELECT weatherwear_replace_fk_with_cascade(
    'recommendation_history',
    'user_id',
    'users',
    'id',
    'fk_recommendation_history_user'
);

SELECT weatherwear_replace_fk_with_cascade(
    'user_feedback',
    'recommendation_history_id',
    'recommendation_history',
    'id',
    'fk_user_feedback_recommendation_history'
);

SELECT weatherwear_replace_fk_with_cascade(
    'user_feedback',
    'user_id',
    'users',
    'id',
    'fk_user_feedback_user'
);

SELECT weatherwear_replace_fk_with_cascade(
    'chat_sessions',
    'user_id',
    'users',
    'id',
    'fk_chat_sessions_user'
);

SELECT weatherwear_replace_fk_with_cascade(
    'chat_messages',
    'session_id',
    'chat_sessions',
    'id',
    'fk_chat_messages_session'
);

DROP FUNCTION weatherwear_replace_fk_with_cascade(
    TEXT,
    TEXT,
    TEXT,
    TEXT,
    TEXT
);

CREATE OR REPLACE VIEW v_anonymized_users AS
SELECT
    id,
    weatherwear_mask_email(email) AS masked_email,
    role,
    created_at,
    updated_at
FROM users;

CREATE OR REPLACE VIEW v_user_recommendation_summary AS
SELECT
    u.id AS user_id,
    weatherwear_mask_email(u.email) AS masked_email,
    COUNT(DISTINCT rh.id) AS recommendation_count,
    COUNT(uf.id) AS feedback_count,
    ROUND(AVG(uf.rating) FILTER (WHERE uf.rating IS NOT NULL), 2) AS average_rating,
    MAX(rh.created_at) AS last_recommendation_at
FROM users u
LEFT JOIN recommendation_history rh ON rh.user_id = u.id
LEFT JOIN user_feedback uf ON uf.recommendation_history_id = rh.id
GROUP BY u.id, u.email;
