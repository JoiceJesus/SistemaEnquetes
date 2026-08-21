package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexao {

    private static final String URL = System.getenv().getOrDefault(
            "SISTEMA_ENQUETES_DB_URL",
            "jdbc:mysql://localhost:3306/sistema_enquetes?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Bahia"
    );

    private static final String USUARIO = System.getenv().getOrDefault(
            "SISTEMA_ENQUETES_DB_USER", "root"
    );

    private static final String SENHA = System.getenv().getOrDefault(
            "SISTEMA_ENQUETES_DB_PASSWORD", "1234"
    );

    private Conexao() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL não encontrado. Verifique o mysql-connector-j no WEB-INF/lib.",
                e
            );
        }

        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
    
    public static void fechar(Connection conexao) {
        if (conexao != null) {
            try { conexao.close(); } catch (SQLException ignored) { }
        }
    }
}
