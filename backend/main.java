import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class main {
    public static void test(){
        String url = "jdbc:mysql://localhost:3306/library_booking";
        String user = "root";
        String password = "Lmz061112";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // 1. 加载驱动 & 连接数据库
            System.out.println("正在连接 MySQL...");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ 数据库连接成功！");
            System.out.println("  数据库产品: " + conn.getMetaData().getDatabaseProductVersion());

            // 2. 查询数据库中的表
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
            System.err.println("✗ 连接失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

    }

    public static void main(String[] args) {
        test();

    }
}
