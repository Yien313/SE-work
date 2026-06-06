import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class main {

    // ========== 数据库连接信息 ==========
    private static final String URL      = "jdbc:mysql://localhost:3306/library_booking";
    private static final String USER     = "root";
    private static final String PASSWORD = "Lmz061112";

    /**
     * 测试数据库连接，并列出所有表
     */
    public static void testConnection() {
        Connection conn = null;
        Statement stmt  = null;
        ResultSet rs    = null;

        try {
            System.out.println("正在连接 MySQL...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功！");
            System.out.println("数据库产品版本: " + conn.getMetaData().getDatabaseProductVersion());

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW TABLES");

            System.out.println("\nlibrary_booking 中的表：");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  - " + rs.getString(1));
            }
            if (count == 0) {
                System.out.println("  (暂无表)");
            }

        } catch (Exception e) {
            System.err.println("连接失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null)   rs.close();   } catch (Exception e) {}
            try { if (stmt != null) stmt.close();  } catch (Exception e) {}
            try { if (conn != null) conn.close();  } catch (Exception e) {}
        }
    }

    /**
     * 用户表测试：建表 → 插入 → 查询
     */
    public static void testUserTable() {
        Connection conn        = null;
        Statement  stmt        = null;
        PreparedStatement pStmt = null;
        ResultSet   rs         = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.createStatement();

            // ===== 1. 先删旧表，再建表（与 init_library_booking.sql 一致） =====
            System.out.println("\n===== 1. 重建 user 表 =====");
            stmt.executeUpdate("DROP TABLE IF EXISTS user");
            String createSQL = """
                CREATE TABLE IF NOT EXISTS user (
                    id          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
                    user_id     VARCHAR(32)      NOT NULL                 COMMENT '学号/工号',
                    password    VARCHAR(255)     NOT NULL                 COMMENT '密码(加密存储)',
                    created_at  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                    updated_at  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    PRIMARY KEY (id),
                    UNIQUE INDEX uk_user_id (user_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
                """;
            stmt.executeUpdate(createSQL);
            System.out.println("user 表已就绪。");

            // ===== 2. 插入测试数据 =====
            System.out.println("\n===== 2. 插入测试用户 =====");
            String insertSQL = """
                INSERT INTO user (user_id, password)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE user_id = VALUES(user_id)
                """;
            pStmt = conn.prepareStatement(insertSQL);

            // 插入两条测试数据（实际场景中密码应为加密值）
            pStmt.setString(1, "2024001");
            pStmt.setString(2, "hashed_password_001");  // 实际应用中应存哈希
            pStmt.executeUpdate();
            System.out.println("  已插入: 2024001");

            pStmt.setString(1, "T2024002");
            pStmt.setString(2, "hashed_password_002");
            pStmt.executeUpdate();
            System.out.println("  已插入: T2024002");

            // ===== 3. 查询所有用户 =====
            System.out.println("\n===== 3. 查询所有用户 =====");
            rs = stmt.executeQuery("SELECT id, user_id, created_at FROM user");
            System.out.printf("%-6s %-14s %-20s\n", "id", "学号/工号", "注册时间");
            System.out.println("------ -------------- --------------------");
            while (rs.next()) {
                System.out.printf("%-6d %-14s %-20s\n",
                    rs.getLong("id"),
                    rs.getString("user_id"),
                    rs.getString("created_at")
                );
            }

        } catch (Exception e) {
            System.err.println("用户表测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null)    rs.close();    } catch (Exception e) {}
            try { if (pStmt != null) pStmt.close(); } catch (Exception e) {}
            try { if (stmt != null)  stmt.close();  } catch (Exception e) {}
            try { if (conn != null)  conn.close();  } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        testConnection();
        testUserTable();
    }
}
