-- WeatherWear  MVP database schema
-- Version aligned with current Java entities + lightweight preference/feedback extension

DROP TABLE IF EXISTS user_feedback CASCADE;
DROP TABLE IF EXISTS chat_messages CASCADE;
DROP TABLE IF EXISTS chat_sessions CASCADE;
DROP TABLE IF EXISTS recommendation_history CASCADE;
DROP TABLE IF EXISTS weather_cache CASCADE;
DROP TABLE IF EXISTS user_preferences CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL DEFAULT 'USER',
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMP
);

CREATE TABLE user_preferences (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,

                                  style_preference VARCHAR(30) NOT NULL DEFAULT 'CASUAL',
                                  cold_sensitivity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                                  heat_sensitivity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',

                                  wind_sensitivity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                                  rain_sensitivity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
                                  max_layers SMALLINT NOT NULL DEFAULT 3 CHECK (max_layers BETWEEN 1 AND 5),
                                  prefers_headwear BOOLEAN NOT NULL DEFAULT FALSE,
                                  prefers_waterproof BOOLEAN NOT NULL DEFAULT FALSE,
                                  activity_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',

                                  preferred_colors VARCHAR(255),
                                  avoid_items VARCHAR(255),

                                  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                  updated_at TIMESTAMP
);

CREATE TABLE weather_cache (
                               id BIGSERIAL PRIMARY KEY,
                               city VARCHAR(120),
                               latitude DOUBLE PRECISION,
                               longitude DOUBLE PRECISION,
                               temperature DOUBLE PRECISION,
                               feels_like DOUBLE PRECISION,
                               humidity INT CHECK (humidity BETWEEN 0 AND 100),
                               wind_speed DOUBLE PRECISION,
                               condition VARCHAR(50),
                               precipitation DOUBLE PRECISION,
                               cached_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               expires_at TIMESTAMP NOT NULL
);

CREATE TABLE recommendation_history (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                        city VARCHAR(120),
                                        weather_summary VARCHAR(1000),
                                        recommendation_text TEXT,
                                        created_at TIMESTAMP NOT NULL DEFAULT NOW()
);


CREATE TABLE user_feedback (
                               id BIGSERIAL PRIMARY KEY,
                               recommendation_history_id BIGINT NOT NULL REFERENCES recommendation_history(id) ON DELETE CASCADE,
                               user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               feedback_type VARCHAR(30) NOT NULL DEFAULT 'RATING',
                               rating SMALLINT CHECK (rating BETWEEN 1 AND 5),
                               comment TEXT,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_sessions (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               title VARCHAR(255) NOT NULL DEFAULT 'Style chat',
                               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP
);

CREATE TABLE chat_messages (
                               id BIGSERIAL PRIMARY KEY,
                               session_id BIGINT NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
                               role VARCHAR(30) NOT NULL,
                               content TEXT NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);
CREATE INDEX idx_weather_cache_city ON weather_cache(city);
CREATE INDEX idx_weather_cache_coordinates ON weather_cache(latitude, longitude);
CREATE INDEX idx_weather_cache_expires_at ON weather_cache(expires_at);
CREATE INDEX idx_recommendation_history_user_id ON recommendation_history(user_id);
CREATE INDEX idx_user_feedback_user_id ON user_feedback(user_id);
CREATE INDEX idx_user_feedback_recommendation_history_id ON user_feedback(recommendation_history_id);
CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);
