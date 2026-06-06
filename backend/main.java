import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.stream.Collectors;

@SuppressWarnings("restriction")
public class main {

    // ---- 数据库配置 ----
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/library_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Lmz061112";

    // ---- 服务器端口 ----
    private static final int PORT = 8080;

    // ---- 数据库连接 ----
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // ---- 简单 JSON 解析（不依赖第三方库） ----
    private static String jsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start = json.indexOf(":", start) + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = json.indexOf("\"", start);
        if (end == -1) end = json.indexOf("}", start);
        if (end == -1) return "";
        return json.substring(start, end);
    }

    // ---- 注册用户到数据库 ----
    private static String doRegister(String studentId, String password) {
        String sql = "INSERT INTO user (user_id, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, password);
            ps.executeUpdate();
            return "{\"success\":true,\"message\":\"注册成功\"}";
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                return "{\"success\":false,\"message\":\"该学号已被注册\"}";
            }
            return "{\"success\":false,\"message\":\"数据库错误: " + e.getMessage() + "\"}";
        }
    }

    // ---- HTTP 请求处理器 ----
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // ① 只处理 POST
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"success\":false,\"message\":\"仅支持POST\"}");
                return;
            }

            // ② 读取请求体 JSON
            String body = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

            // ③ 解析学号和密码
            String studentId = jsonValue(body, "studentId");
            String password  = jsonValue(body, "password");

            if (studentId.isEmpty() || password.isEmpty()) {
                sendResponse(exchange, 400, "{\"success\":false,\"message\":\"学号和密码不能为空\"}");
                return;
            }

            // ④ 执行注册
            String result = doRegister(studentId, password);
            sendResponse(exchange, 200, result);
        }
    }

    // ---- 发送 JSON 响应 + CORS ----
    private static void sendResponse(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ---- 启动服务器 ----
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/register", new RegisterHandler());
        server.setExecutor(null); // 默认线程池
        server.start();
        System.out.println("后端服务器已启动 → http://localhost:" + PORT);
        System.out.println("注册接口: POST http://localhost:" + PORT + "/api/register");
    }
}
