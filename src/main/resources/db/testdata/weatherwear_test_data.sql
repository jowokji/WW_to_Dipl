-- Reproducible demo/test data for local verification.
-- User passwords are BCrypt hashes. No plain-text passwords are stored here.

BEGIN;

DELETE FROM user_feedback
WHERE user_id IN (
    SELECT id FROM users WHERE email IN (
        'demo.user@example.com',
        'admin.user@example.com',
        'edge.case@example.com'
    )
);

DELETE FROM chat_messages
WHERE session_id IN (
    SELECT cs.id
    FROM chat_sessions cs
    JOIN users u ON u.id = cs.user_id
    WHERE u.email IN (
        'demo.user@example.com',
        'admin.user@example.com',
        'edge.case@example.com'
    )
);

DELETE FROM chat_sessions
WHERE user_id IN (
    SELECT id FROM users WHERE email IN (
        'demo.user@example.com',
        'admin.user@example.com',
        'edge.case@example.com'
    )
);

DELETE FROM recommendation_history
WHERE user_id IN (
    SELECT id FROM users WHERE email IN (
        'demo.user@example.com',
        'admin.user@example.com',
        'edge.case@example.com'
    )
);

DELETE FROM user_preferences
WHERE user_id IN (
    SELECT id FROM users WHERE email IN (
        'demo.user@example.com',
        'admin.user@example.com',
        'edge.case@example.com'
    )
);

DELETE FROM users
WHERE email IN (
    'demo.user@example.com',
    'admin.user@example.com',
    'edge.case@example.com'
);

DELETE FROM weather_cache
WHERE city IN ('Vilnius', 'London', 'Oslo', 'Death Valley');

INSERT INTO users (email, password, role, created_at)
VALUES
    (
        'demo.user@example.com',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5B6dqg/YShu5h9g1xTDqJgIYj3q.q',
        'USER',
        NOW() - INTERVAL '5 days'
    ),
    (
        'admin.user@example.com',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5B6dqg/YShu5h9g1xTDqJgIYj3q.q',
        'ADMIN',
        NOW() - INTERVAL '4 days'
    ),
    (
        'edge.case@example.com',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi5B6dqg/YShu5h9g1xTDqJgIYj3q.q',
        'USER',
        NOW() - INTERVAL '1 day'
    );

INSERT INTO user_preferences (
    user_id,
    style_preference,
    cold_sensitivity,
    heat_sensitivity,
    wind_sensitivity,
    rain_sensitivity,
    max_layers,
    prefers_headwear,
    prefers_waterproof,
    activity_level,
    preferred_colors,
    avoid_items,
    created_at
)
SELECT
    id,
    'CASUAL',
    'HIGH',
    'MEDIUM',
    'HIGH',
    'HIGH',
    4,
    TRUE,
    TRUE,
    'MEDIUM',
    'navy, gray, white',
    'sandals',
    NOW() - INTERVAL '5 days'
FROM users
WHERE email = 'demo.user@example.com';

INSERT INTO user_preferences (
    user_id,
    style_preference,
    cold_sensitivity,
    heat_sensitivity,
    wind_sensitivity,
    rain_sensitivity,
    max_layers,
    prefers_headwear,
    prefers_waterproof,
    activity_level,
    preferred_colors,
    avoid_items,
    created_at
)
SELECT
    id,
    'BUSINESS',
    'MEDIUM',
    'LOW',
    'MEDIUM',
    'LOW',
    3,
    FALSE,
    TRUE,
    'LOW',
    'black, blue',
    'sport shoes',
    NOW() - INTERVAL '4 days'
FROM users
WHERE email = 'admin.user@example.com';

INSERT INTO user_preferences (
    user_id,
    style_preference,
    cold_sensitivity,
    heat_sensitivity,
    wind_sensitivity,
    rain_sensitivity,
    max_layers,
    prefers_headwear,
    prefers_waterproof,
    activity_level,
    preferred_colors,
    avoid_items,
    created_at
)
SELECT
    id,
    'SPORTY',
    'LOW',
    'HIGH',
    'LOW',
    'MEDIUM',
    1,
    FALSE,
    FALSE,
    'HIGH',
    NULL,
    NULL,
    NOW() - INTERVAL '1 day'
FROM users
WHERE email = 'edge.case@example.com';

INSERT INTO weather_cache (
    city,
    latitude,
    longitude,
    temperature,
    feels_like,
    humidity,
    wind_speed,
    condition,
    precipitation,
    cached_at,
    expires_at
)
VALUES
    ('Vilnius', 54.6872, 25.2797, 8.5, 5.9, 76, 6.4, 'RAIN', 1.8, NOW(), NOW() + INTERVAL '1 hour'),
    ('London', 51.5072, -0.1276, 15.2, 14.6, 65, 3.1, 'CLOUDS', 0.0, NOW(), NOW() + INTERVAL '1 hour'),
    ('Oslo', 59.9139, 10.7522, -6.0, -11.3, 82, 5.7, 'SNOW', 2.5, NOW(), NOW() + INTERVAL '1 hour'),
    ('Death Valley', 36.5323, -116.9325, 49.0, 51.0, 9, 2.2, 'CLEAR', 0.0, NOW(), NOW() + INTERVAL '1 hour');

INSERT INTO recommendation_history (
    user_id,
    city,
    weather_summary,
    recommendation_text,
    created_at
)
SELECT
    id,
    'Vilnius',
    'Temp: 8.5, Feels: 5.9, Condition: RAIN',
    'Wear a waterproof jacket, warm mid-layer, long trousers, and closed shoes.',
    NOW() - INTERVAL '2 days'
FROM users
WHERE email = 'demo.user@example.com';

INSERT INTO recommendation_history (
    user_id,
    city,
    weather_summary,
    recommendation_text,
    created_at
)
SELECT
    id,
    'Oslo',
    'Temp: -6.0, Feels: -11.3, Condition: SNOW',
    'Use thermal layers, winter coat, gloves, hat, scarf, and insulated boots.',
    NOW() - INTERVAL '1 day'
FROM users
WHERE email = 'demo.user@example.com';

INSERT INTO recommendation_history (
    user_id,
    city,
    weather_summary,
    recommendation_text,
    created_at
)
SELECT
    id,
    'Death Valley',
    'Temp: 49.0, Feels: 51.0, Condition: CLEAR',
    'Wear lightweight breathable clothing, sun protection, and carry water.',
    NOW() - INTERVAL '12 hours'
FROM users
WHERE email = 'edge.case@example.com';

INSERT INTO user_feedback (
    recommendation_history_id,
    user_id,
    feedback_type,
    rating,
    comment,
    created_at
)
SELECT
    rh.id,
    u.id,
    'RATING',
    5,
    'Accurate recommendation for rainy weather.',
    NOW() - INTERVAL '1 day'
FROM recommendation_history rh
JOIN users u ON u.id = rh.user_id
WHERE u.email = 'demo.user@example.com'
  AND rh.city = 'Vilnius';

WITH inserted_session AS (
    INSERT INTO chat_sessions (user_id, title, created_at, updated_at)
    SELECT id, 'Morning commute outfit', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'
    FROM users
    WHERE email = 'demo.user@example.com'
    RETURNING id
)
INSERT INTO chat_messages (session_id, role, content, created_at)
SELECT id, 'USER', 'What should I wear for rainy weather in Vilnius?', NOW() - INTERVAL '2 hours'
FROM inserted_session
UNION ALL
SELECT id, 'ASSISTANT', 'Choose a waterproof jacket, warm layer, and closed shoes.', NOW() - INTERVAL '119 minutes'
FROM inserted_session;

COMMIT;
