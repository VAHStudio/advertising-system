package com.touhuwai.db.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库迁移验证测试
 * 验证 V9 和 V10 迁移脚本正确执行
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConversationMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testV9_aiConversationEnhanced() {
        // 验证 ai_conversation 表存在 mode 字段
        Integer modeColumnExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation' " +
            "AND COLUMN_NAME = 'mode'",
            Integer.class
        );
        assertEquals(1, modeColumnExists, "mode 字段应该存在");

        // 验证 message_count 字段存在
        Integer countColumnExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation' " +
            "AND COLUMN_NAME = 'message_count'",
            Integer.class
        );
        assertEquals(1, countColumnExists, "message_count 字段应该存在");

        // 验证 last_message_preview 字段存在
        Integer previewColumnExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation' " +
            "AND COLUMN_NAME = 'last_message_preview'",
            Integer.class
        );
        assertEquals(1, previewColumnExists, "last_message_preview 字段应该存在");

        // 验证复合索引存在
        Integer userModeIndexExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.STATISTICS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation' " +
            "AND INDEX_NAME = 'idx_user_mode_time'",
            Integer.class
        );
        assertEquals(1, userModeIndexExists, "idx_user_mode_time 索引应该存在");
    }

    @Test
    void testV10_aiConversationMessageTable() {
        // 验证 ai_conversation_message 表存在
        Integer tableExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.TABLES " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation_message'",
            Integer.class
        );
        assertEquals(1, tableExists, "ai_conversation_message 表应该存在");

        // 验证必要字段存在
        String[] expectedColumns = {"id", "conversation_id", "role", "content", 
                                     "thinking", "tool_calls", "metadata", "created_at"};
        
        for (String column : expectedColumns) {
            Integer columnExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'ai_conversation_message' " +
                "AND COLUMN_NAME = ?",
                Integer.class,
                column
            );
            assertEquals(1, columnExists, column + " 字段应该存在");
        }

        // 验证索引存在
        Integer convTimeIndexExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.STATISTICS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'ai_conversation_message' " +
            "AND INDEX_NAME = 'idx_conversation_time'",
            Integer.class
        );
        assertEquals(1, convTimeIndexExists, "idx_conversation_time 索引应该存在");
    }

    @Test
    void testModeFieldDefaultValue() {
        // 插入一条测试数据，验证 mode 字段默认值
        jdbcTemplate.update(
            "INSERT INTO ai_conversation (user_id, conversation_id, title) " +
            "VALUES ('test_user', 'test_conv_001', '测试会话')"
        );

        String mode = jdbcTemplate.queryForObject(
            "SELECT mode FROM ai_conversation WHERE conversation_id = ?",
            String.class,
            "test_conv_001"
        );

        assertEquals("DIFY", mode, "mode 字段默认值应该是 DIFY");

        // 清理测试数据
        jdbcTemplate.update(
            "DELETE FROM ai_conversation WHERE conversation_id = ?",
            "test_conv_001"
        );
    }

    @Test
    void testCustomModeMigration() {
        // 插入一条 CUSTOM 模式的测试数据
        jdbcTemplate.update(
            "INSERT INTO ai_conversation (user_id, conversation_id, mode, title) " +
            "VALUES ('test_user', 'custom-test-uuid', 'CUSTOM', '测试CUSTOM会话')"
        );

        String mode = jdbcTemplate.queryForObject(
            "SELECT mode FROM ai_conversation WHERE conversation_id = ?",
            String.class,
            "custom-test-uuid"
        );

        assertEquals("CUSTOM", mode, "custom- 前缀的会话应该被识别为 CUSTOM 模式");

        // 清理测试数据
        jdbcTemplate.update(
            "DELETE FROM ai_conversation WHERE conversation_id = ?",
            "custom-test-uuid"
        );
    }
}
