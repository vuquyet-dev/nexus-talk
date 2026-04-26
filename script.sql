-- =============================
-- DATABASE
-- =============================
CREATE DATABASE IF NOT EXISTS nexustalk
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE nexustalk;

-- =============================
-- 1. USERS
-- =============================
CREATE TABLE users (
                       ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                       USERNAME VARCHAR(50) NOT NULL UNIQUE,
                       EMAIL VARCHAR(100) NOT NULL UNIQUE,
                       PASSWORD_HASH TEXT NOT NULL,
                       AVATAR_URL TEXT,
                       BIO TEXT,
                       ROLE ENUM('USER','MODERATOR','ADMIN','SUPER_ADMIN') DEFAULT 'USER',
                       STATUS ENUM('ACTIVE','INACTIVE','BANNED') DEFAULT 'ACTIVE',
                       FCM_TOKEN TEXT,
                       EMBEDDING JSON,
                       LAST_ACTIVE_AT DATETIME,
                       CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                       UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 2. COMMUNITIES
-- =============================
CREATE TABLE communities (
                             ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             NAME VARCHAR(100),
                             SLUG VARCHAR(100) UNIQUE,
                             DESCRIPTION TEXT,
                             VISIBILITY ENUM('PUBLIC','PRIVATE','RESTRICTED'),
                             AI_BOT_ENABLED BOOLEAN,
                             MEMBER_COUNT INT DEFAULT 0,
                             CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 3. POSTS
-- =============================
CREATE TABLE posts (
                       ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                       AUTHOR_ID BIGINT UNSIGNED,
                       COMMUNITY_ID BIGINT UNSIGNED,
                       TITLE TEXT,
                       CONTENT TEXT,
                       CONTENT_HTML LONGTEXT,
                       SUMMARY_AI TEXT,
                       POST_TYPE ENUM('TEXT','IMAGE','VIDEO','POLL'),
                       STATUS ENUM('DRAFT','PUBLISHED','REMOVED','FLAGGED'),
                       MEDIA_URLS JSON,
                       AI_MODERATION JSON,
                       EMBEDDING JSON,
                       LIKE_COUNT INT DEFAULT 0,
                       COMMENT_COUNT INT DEFAULT 0,
                       CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (AUTHOR_ID) REFERENCES users(ID) ON DELETE SET NULL,
                       FOREIGN KEY (COMMUNITY_ID) REFERENCES communities(ID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_posts_community ON posts(COMMUNITY_ID, CREATED_AT DESC);

-- =============================
-- 4. USER FOLLOWS
-- =============================
CREATE TABLE user_follows (
                              ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                              FOLLOWER_ID BIGINT UNSIGNED NOT NULL,
                              FOLLOWING_ID BIGINT UNSIGNED NOT NULL,
                              CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                              UNIQUE KEY uq_follow (FOLLOWER_ID, FOLLOWING_ID),
                              FOREIGN KEY (FOLLOWER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                              FOREIGN KEY (FOLLOWING_ID) REFERENCES users(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 5. USER BLOCKS
-- =============================
CREATE TABLE user_blocks (
                             ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             BLOCKER_ID BIGINT UNSIGNED NOT NULL,
                             BLOCKED_ID BIGINT UNSIGNED NOT NULL,
                             UNIQUE KEY uq_block (BLOCKER_ID, BLOCKED_ID),
                             FOREIGN KEY (BLOCKER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                             FOREIGN KEY (BLOCKED_ID) REFERENCES users(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                              USER_ID BIGINT UNSIGNED NOT NULL,
                                              TOKEN VARCHAR(255) NOT NULL UNIQUE,
                                              USER_AGENT_HASH VARCHAR(64),
                                              EXPIRES_AT DATETIME NOT NULL,
                                              REVOKED_AT DATETIME,
                                              CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                                              CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 6. COMMUNITY MEMBERS
-- =============================
CREATE TABLE community_members (
                                   ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                   USER_ID BIGINT UNSIGNED,
                                   COMMUNITY_ID BIGINT UNSIGNED,
                                   ROLE ENUM('MEMBER','MODERATOR','ADMIN'),
                                   JOINED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE KEY uq_member (USER_ID, COMMUNITY_ID),
                                   FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                                   FOREIGN KEY (COMMUNITY_ID) REFERENCES communities(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 7. COMMUNITY RULES
-- =============================
CREATE TABLE community_rules (
                                 ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 COMMUNITY_ID BIGINT UNSIGNED,
                                 ORDER_NUM INT,
                                 TITLE VARCHAR(255),
                                 DESCRIPTION TEXT,
                                 FOREIGN KEY (COMMUNITY_ID) REFERENCES communities(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 8. COMMENTS
-- =============================
CREATE TABLE comments (
                          ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          USER_ID BIGINT UNSIGNED,
                          POST_ID BIGINT UNSIGNED,
                          PARENT_COMMENT_ID BIGINT UNSIGNED,
                          CONTENT TEXT,
                          IS_AI_GENERATED BOOLEAN,
                          CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE SET NULL,
                          FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE,
                          FOREIGN KEY (PARENT_COMMENT_ID) REFERENCES comments(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_comments_post ON comments(POST_ID);

-- =============================
-- 9. POST REACTIONS
-- =============================
CREATE TABLE post_reactions (
                                ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                USER_ID BIGINT UNSIGNED,
                                POST_ID BIGINT UNSIGNED,
                                REACTION_TYPE ENUM('LIKE','LOVE','LAUGH','SAD','ANGRY'),
                                UNIQUE KEY uq_reaction (USER_ID, POST_ID),
                                FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                                FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 10. HASHTAGS
-- =============================
CREATE TABLE hashtags (
                          ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          NAME VARCHAR(100) UNIQUE,
                          POST_COUNT INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 11. POST HASHTAGS
-- =============================
CREATE TABLE post_hashtags (
                               ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               POST_ID BIGINT UNSIGNED,
                               HASHTAG_ID BIGINT UNSIGNED,
                               UNIQUE KEY uq_post_tag (POST_ID, HASHTAG_ID),
                               FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE,
                               FOREIGN KEY (HASHTAG_ID) REFERENCES hashtags(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 12. SAVED POSTS
-- =============================
CREATE TABLE saved_posts (
                             ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                             USER_ID BIGINT UNSIGNED,
                             POST_ID BIGINT UNSIGNED,
                             UNIQUE KEY uq_saved (USER_ID, POST_ID),
                             FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                             FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 13. REPORTS
-- =============================
CREATE TABLE reports (
                         ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                         REPORTER_ID BIGINT UNSIGNED,
                         POST_ID BIGINT UNSIGNED,
                         REASON ENUM('SPAM','HATE','NSFW','MISINFORMATION'),
                         STATUS ENUM('PENDING','RESOLVED','DISMISSED'),
                         FOREIGN KEY (REPORTER_ID) REFERENCES users(ID) ON DELETE SET NULL,
                         FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 14. CONVERSATIONS
-- =============================
CREATE TABLE conversations (
                               ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               TYPE ENUM('DIRECT','GROUP','COMMUNITY'),
                               LAST_MESSAGE_AT DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 15. CONVERSATION PARTICIPANTS
-- =============================
CREATE TABLE conversation_participants (
                                           ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                           CONVERSATION_ID BIGINT UNSIGNED,
                                           USER_ID BIGINT UNSIGNED,
                                           LAST_READ_AT DATETIME,
                                           UNIQUE KEY uq_conv_user (CONVERSATION_ID, USER_ID),
                                           FOREIGN KEY (CONVERSATION_ID) REFERENCES conversations(ID) ON DELETE CASCADE,
                                           FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 16. MESSAGES
-- =============================
CREATE TABLE messages (
                          ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          CONVERSATION_ID BIGINT UNSIGNED,
                          SENDER_ID BIGINT UNSIGNED,
                          MESSAGE_TYPE ENUM('TEXT','IMAGE','FILE','SYSTEM'),
                          CONTENT TEXT,
                          REPLY_TO_ID BIGINT UNSIGNED,
                          IS_DELETED BOOLEAN,
                          CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (CONVERSATION_ID) REFERENCES conversations(ID) ON DELETE CASCADE,
                          FOREIGN KEY (SENDER_ID) REFERENCES users(ID) ON DELETE SET NULL,
                          FOREIGN KEY (REPLY_TO_ID) REFERENCES messages(ID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_messages_conv ON messages(CONVERSATION_ID, CREATED_AT DESC);

-- =============================
-- 17. NOTIFICATIONS
-- =============================
CREATE TABLE notifications (
                               ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               RECIPIENT_ID BIGINT UNSIGNED,
                               TYPE ENUM('LIKE','COMMENT','FOLLOW','MENTION'),
                               IS_READ BOOLEAN,
                               PUSH_SENT BOOLEAN,
                               CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (RECIPIENT_ID) REFERENCES users(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notifications_user ON notifications(RECIPIENT_ID, CREATED_AT DESC);

-- =============================
-- 18. AI CHAT SESSIONS
-- =============================
CREATE TABLE ai_chat_sessions (
                                  ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                  USER_ID BIGINT UNSIGNED,
                                  COMMUNITY_ID BIGINT UNSIGNED,
                                  CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                                  FOREIGN KEY (COMMUNITY_ID) REFERENCES communities(ID) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 19. AI CHAT MESSAGES
-- =============================
CREATE TABLE ai_chat_messages (
                                  ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                  SESSION_ID BIGINT UNSIGNED,
                                  ROLE ENUM('USER','ASSISTANT','SYSTEM'),
                                  CONTENT TEXT,
                                  TOKENS_USED INT,
                                  CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (SESSION_ID) REFERENCES ai_chat_sessions(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_ai_session ON ai_chat_messages(SESSION_ID);

-- =============================
-- 20. USER FEED CACHE
-- =============================
CREATE TABLE user_feed_cache (
                                 ID BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 USER_ID BIGINT UNSIGNED,
                                 POST_ID BIGINT UNSIGNED,
                                 RELEVANCE_SCORE FLOAT,
                                 UNIQUE KEY uq_feed (USER_ID, POST_ID),
                                 FOREIGN KEY (USER_ID) REFERENCES users(ID) ON DELETE CASCADE,
                                 FOREIGN KEY (POST_ID) REFERENCES posts(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_feed_user ON user_feed_cache(USER_ID, RELEVANCE_SCORE DESC);