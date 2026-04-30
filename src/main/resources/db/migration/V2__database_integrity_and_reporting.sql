CREATE OR REPLACE FUNCTION weatherwear_set_updated_at()
RETURNS trigger AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_set_updated_at ON users;
CREATE TRIGGER trg_users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION weatherwear_set_updated_at();

DROP TRIGGER IF EXISTS trg_user_preferences_set_updated_at ON user_preferences;
CREATE TRIGGER trg_user_preferences_set_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW
    EXECUTE FUNCTION weatherwear_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_sessions_set_updated_at ON chat_sessions;
CREATE TRIGGER trg_chat_sessions_set_updated_at
    BEFORE UPDATE ON chat_sessions
    FOR EACH ROW
    EXECUTE FUNCTION weatherwear_set_updated_at();

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_role'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_role
            CHECK (role IN ('USER', 'ADMIN'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_users_email_format'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_users_email_format
            CHECK (email LIKE '%_@_%._%');
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_user_preferences_style'
    ) THEN
        ALTER TABLE user_preferences
            ADD CONSTRAINT chk_user_preferences_style
            CHECK (style_preference IN (
                'CASUAL',
                'BUSINESS',
                'SPORTY',
                'STREETWEAR',
                'ELEGANT',
                'MINIMALIST'
            ));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_user_preferences_sensitivity_values'
    ) THEN
        ALTER TABLE user_preferences
            ADD CONSTRAINT chk_user_preferences_sensitivity_values
            CHECK (
                cold_sensitivity IN ('LOW', 'MEDIUM', 'HIGH')
                AND heat_sensitivity IN ('LOW', 'MEDIUM', 'HIGH')
                AND wind_sensitivity IN ('LOW', 'MEDIUM', 'HIGH')
                AND rain_sensitivity IN ('LOW', 'MEDIUM', 'HIGH')
            );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_user_preferences_activity'
    ) THEN
        ALTER TABLE user_preferences
            ADD CONSTRAINT chk_user_preferences_activity
            CHECK (activity_level IN ('LOW', 'MEDIUM', 'HIGH'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_weather_cache_coordinates'
    ) THEN
        ALTER TABLE weather_cache
            ADD CONSTRAINT chk_weather_cache_coordinates
            CHECK (
                (latitude IS NULL OR latitude BETWEEN -90 AND 90)
                AND (longitude IS NULL OR longitude BETWEEN -180 AND 180)
            );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_weather_cache_numeric_ranges'
    ) THEN
        ALTER TABLE weather_cache
            ADD CONSTRAINT chk_weather_cache_numeric_ranges
            CHECK (
                (temperature IS NULL OR temperature BETWEEN -90 AND 60)
                AND (feels_like IS NULL OR feels_like BETWEEN -100 AND 70)
                AND (wind_speed IS NULL OR wind_speed >= 0)
                AND (precipitation IS NULL OR precipitation >= 0)
            );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_weather_cache_condition'
    ) THEN
        ALTER TABLE weather_cache
            ADD CONSTRAINT chk_weather_cache_condition
            CHECK (
                condition IS NULL
                OR condition IN (
                    'CLEAR',
                    'CLOUDS',
                    'RAIN',
                    'DRIZZLE',
                    'THUNDERSTORM',
                    'SNOW',
                    'MIST',
                    'FOG',
                    'WIND',
                    'UNKNOWN'
                )
            );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_user_feedback_type'
    ) THEN
        ALTER TABLE user_feedback
            ADD CONSTRAINT chk_user_feedback_type
            CHECK (feedback_type IN ('RATING', 'LIKE', 'DISLIKE', 'COMMENT'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_user_feedback_has_content'
    ) THEN
        ALTER TABLE user_feedback
            ADD CONSTRAINT chk_user_feedback_has_content
            CHECK (
                rating IS NOT NULL
                OR (comment IS NOT NULL AND BTRIM(comment) <> '')
            );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_chat_messages_role'
    ) THEN
        ALTER TABLE chat_messages
            ADD CONSTRAINT chk_chat_messages_role
            CHECK (role IN ('USER', 'ASSISTANT', 'SYSTEM'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_chat_messages_content_not_blank'
    ) THEN
        ALTER TABLE chat_messages
            ADD CONSTRAINT chk_chat_messages_content_not_blank
            CHECK (BTRIM(content) <> '');
    END IF;
END $$;

CREATE OR REPLACE FUNCTION weatherwear_mask_email(input_email TEXT)
RETURNS TEXT AS $$
BEGIN
    IF input_email IS NULL OR POSITION('@' IN input_email) <= 1 THEN
        RETURN '***';
    END IF;

    RETURN LEFT(input_email, 1) || '***' || SUBSTRING(input_email FROM POSITION('@' IN input_email));
END;
$$ LANGUAGE plpgsql IMMUTABLE;

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
