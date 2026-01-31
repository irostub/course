package com.example.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DBConnectionTest {
    @Test
    void testDatabaseConnection() throws Exception {
        // 데이터베이스 연결 테스트
        try (Connection conn = DatabaseConfig.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testCreateTableAndInsertData() throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // 테이블 생성
            String createTableSQL = """
                CREATE TABLE users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL
                )
                """;
            stmt.execute(createTableSQL);

            // 데이터 삽입
            String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, "jin");
                pstmt.setString(2, "jin@example.com");

                int rowsAffected = pstmt.executeUpdate();
                assertEquals(1, rowsAffected);
            }

            // 데이터 조회
            String selectSQL = "SELECT * FROM users WHERE name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                pstmt.setString(1, "jin");

                try (ResultSet rs = pstmt.executeQuery()) {
                    assertTrue(rs.next());
                    assertEquals("jin", rs.getString("name"));
                    assertEquals("jin@example.com", rs.getString("email"));
                }
            }
        }
    }
}
