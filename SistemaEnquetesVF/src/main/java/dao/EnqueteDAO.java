package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import model.CategoriaModel;
import model.EnqueteModel;
import model.UsuarioModel;
import util.Conexao;

public class EnqueteDAO {

    public int inserir(EnqueteModel enquete) {

        String sql =
            "INSERT INTO enquete " +
            "(titulo, descricao, tipo_votacao, limite_votos_ip, " +
            "limite_quantidade_votos, data_expiracao, status, " +
            "usuario_idusuario, categoria_idcategoria) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, enquete.getTitulo());
            stmt.setString(2, enquete.getDescricao());
            stmt.setString(3, enquete.getTipoVotacao());
            stmt.setInt(4, enquete.getLimiteVotosIp());
            stmt.setInt(5, enquete.getLimiteQuantidadeVotos());

            if (enquete.getDataExpiracao() != null) {
                stmt.setTimestamp(
                    6,
                    Timestamp.valueOf(
                        enquete.getDataExpiracao()
                    )
                );
            } else {
                stmt.setTimestamp(6, null);
            }

            stmt.setString(7, enquete.getStatus());

            stmt.setInt(
                8,
                enquete.getUsuario().getIdUsuario()
            );

            stmt.setInt(
                9,
                enquete.getCategoria().getIdCategoria()
            );

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Cria a enquete e todas as opções na mesma transação.
     * Se qualquer INSERT falhar, nada é gravado.
     */
    public int inserirComOpcoes(EnqueteModel enquete, Collection<String> opcoes) {

        if (enquete == null
                || enquete.getUsuario() == null
                || enquete.getCategoria() == null
                || opcoes == null
                || opcoes.size() < 2) {
            return -1;
        }

        String sqlEnquete =
            "INSERT INTO enquete " +
            "(titulo, descricao, tipo_votacao, limite_votos_ip, " +
            "limite_quantidade_votos, data_expiracao, status, " +
            "usuario_idusuario, categoria_idcategoria) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlOpcao =
            "INSERT INTO opcao_resposta " +
            "(descricao_opcao, enquete_idenquete) " +
            "VALUES (?, ?)";

        Connection conn = null;

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            int idEnquete;

            try (PreparedStatement stmt = conn.prepareStatement(
                    sqlEnquete,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, enquete.getTitulo());
                stmt.setString(2, enquete.getDescricao());
                stmt.setString(3, enquete.getTipoVotacao());
                stmt.setInt(4, enquete.getLimiteVotosIp());
                stmt.setInt(5, enquete.getLimiteQuantidadeVotos());

                if (enquete.getDataExpiracao() != null) {
                    stmt.setTimestamp(
                        6,
                        Timestamp.valueOf(enquete.getDataExpiracao())
                    );
                } else {
                    stmt.setNull(6, java.sql.Types.TIMESTAMP);
                }

                stmt.setString(7, enquete.getStatus());
                stmt.setInt(8, enquete.getUsuario().getIdUsuario());
                stmt.setInt(9, enquete.getCategoria().getIdCategoria());

                if (stmt.executeUpdate() != 1) {
                    conn.rollback();
                    return -1;
                }

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return -1;
                    }
                    idEnquete = rs.getInt(1);
                }
            }

            try (PreparedStatement stmtOpcao = conn.prepareStatement(sqlOpcao)) {
                for (String descricao : opcoes) {
                    stmtOpcao.setString(1, descricao);
                    stmtOpcao.setInt(2, idEnquete);
                    stmtOpcao.addBatch();
                }

                int[] resultados = stmtOpcao.executeBatch();
                for (int resultado : resultados) {
                    if (resultado == Statement.EXECUTE_FAILED) {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            conn.commit();
            return idEnquete;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    rollbackError.addSuppressed(e);
                    rollbackError.printStackTrace();
                }
            }

            System.err.println("Erro ao criar enquete e opções: " + e.getMessage());
            e.printStackTrace();
            return -1;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }

                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public EnqueteModel buscarPorId(int id) {

        String sql =
            "SELECT e.*, " +
            "u.idusuario, u.nome, u.email, u.senha, " +
            "u.data_cadastro, u.status AS usuario_status, " +
            "c.idcategoria, c.nome_categoria " +
            "FROM enquete e " +
            "INNER JOIN usuario u " +
            "ON e.usuario_idusuario = u.idusuario " +
            "INNER JOIN categoria c " +
            "ON e.categoria_idcategoria = c.idcategoria " +
            "WHERE e.idenquete = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return montarEnquete(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<EnqueteModel> listarTodos() {

        List<EnqueteModel> lista = new ArrayList<>();

        String sql =
            "SELECT e.*, " +
            "u.idusuario, u.nome, u.email, " +
            "c.idcategoria, c.nome_categoria " +
            "FROM enquete e " +
            "INNER JOIN usuario u " +
            "ON e.usuario_idusuario = u.idusuario " +
            "INNER JOIN categoria c " +
            "ON e.categoria_idcategoria = c.idcategoria " +
            "ORDER BY e.data_criacao DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarEnquete(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean atualizar(EnqueteModel enquete) {

        String sql =
            "UPDATE enquete SET " +
            "titulo = ?, descricao = ?, tipo_votacao = ?, " +
            "limite_votos_ip = ?, limite_quantidade_votos = ?, " +
            "data_expiracao = ?, status = ?, " +
            "usuario_idusuario = ?, categoria_idcategoria = ? " +
            "WHERE idenquete = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(sql)) {

            stmt.setString(1, enquete.getTitulo());
            stmt.setString(2, enquete.getDescricao());
            stmt.setString(3, enquete.getTipoVotacao());
            stmt.setInt(4, enquete.getLimiteVotosIp());
            stmt.setInt(5, enquete.getLimiteQuantidadeVotos());

            if (enquete.getDataExpiracao() != null) {
                stmt.setTimestamp(
                    6,
                    Timestamp.valueOf(
                        enquete.getDataExpiracao()
                    )
                );
            } else {
                stmt.setTimestamp(6, null);
            }

            stmt.setString(7, enquete.getStatus());

            stmt.setInt(
                8,
                enquete.getUsuario().getIdUsuario()
            );

            stmt.setInt(
                9,
                enquete.getCategoria().getIdCategoria()
            );

            stmt.setInt(10, enquete.getIdEnquete());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {

        String sql = "DELETE FROM enquete WHERE idenquete = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void atualizarEnquetesExpiradas() {
        String sql = "UPDATE enquete SET status = 'ENCERRADA' "
                + "WHERE status = 'EM_CURSO' AND data_expiracao <= NOW()";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int contarPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM enquete WHERE status = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    public int contarPorUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM enquete WHERE usuario_idusuario = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { e.printStackTrace(); return 0; }
    }

    private EnqueteModel montarEnquete(ResultSet rs)
            throws SQLException {

        UsuarioModel usuario = new UsuarioModel();

        usuario.setIdUsuario(rs.getInt("idusuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));

        CategoriaModel categoria = new CategoriaModel();

        categoria.setIdCategoria(
            rs.getInt("idcategoria")
        );

        categoria.setNomeCategoria(
            rs.getString("nome_categoria")
        );

        EnqueteModel enquete = new EnqueteModel();

        enquete.setIdEnquete(
            rs.getInt("idenquete")
        );

        enquete.setTitulo(rs.getString("titulo"));
        enquete.setDescricao(rs.getString("descricao"));
        enquete.setTipoVotacao(rs.getString("tipo_votacao"));
        enquete.setLimiteVotosIp(
            rs.getInt("limite_votos_ip")
        );
        enquete.setLimiteQuantidadeVotos(
            rs.getInt("limite_quantidade_votos")
        );

        Timestamp criacao = rs.getTimestamp("data_criacao");
        if (criacao != null) {
            enquete.setDataCriacao(
                criacao.toLocalDateTime()
            );
        }

        Timestamp expiracao =
            rs.getTimestamp("data_expiracao");

        if (expiracao != null) {
            enquete.setDataExpiracao(
                expiracao.toLocalDateTime()
            );
        }

        enquete.setStatus(rs.getString("status"));
        enquete.setUsuario(usuario);
        enquete.setCategoria(categoria);

        return enquete;
    }
}