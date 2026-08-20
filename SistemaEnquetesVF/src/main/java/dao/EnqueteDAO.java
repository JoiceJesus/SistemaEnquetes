package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.CategoriaModel;
import model.EnqueteModel;
import model.UsuarioModel;
import util.Conexao;

public class EnqueteDAO {

    private static final String SELECT_BASE = "SELECT e.*, u.idusuario, u.nome, u.email, c.idcategoria, c.nome_categoria "
            + "FROM enquete e INNER JOIN usuario u ON e.usuario_idusuario = u.idusuario "
            + "INNER JOIN categoria c ON e.categoria_idcategoria = c.idcategoria ";

    public int inserir(EnqueteModel enquete) {
        String sql = "INSERT INTO enquete (titulo, descricao, tipo_votacao, limite_votos_ip, limite_quantidade_votos, "
                + "data_expiracao, status, usuario_idusuario, categoria_idcategoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(stmt, enquete, false);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public EnqueteModel buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE e.idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarEnquete(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<EnqueteModel> listarTodos() {
        String sql = SELECT_BASE + "ORDER BY CASE WHEN e.status = 'EM_CURSO' THEN 0 ELSE 1 END, e.data_criacao DESC";
        return listar(sql, null);
    }

    public List<EnqueteModel> listarDestaques(int limite) {
        List<EnqueteModel> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.idusuario, u.nome, u.email, c.idcategoria, c.nome_categoria "
                + "FROM enquete e INNER JOIN usuario u ON e.usuario_idusuario = u.idusuario "
                + "INNER JOIN categoria c ON e.categoria_idcategoria = c.idcategoria "
                + "LEFT JOIN (SELECT enquete_idenquete, COUNT(*) total_votos FROM voto GROUP BY enquete_idenquete) v "
                + "ON v.enquete_idenquete = e.idenquete "
                + "WHERE e.status = 'EM_CURSO' AND e.data_expiracao > NOW() "
                + "ORDER BY COALESCE(v.total_votos, 0) DESC, e.data_criacao DESC LIMIT ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(1, limite));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarEnquete(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizar(EnqueteModel enquete) {
        String sql = "UPDATE enquete SET titulo = ?, descricao = ?, tipo_votacao = ?, limite_votos_ip = ?, "
                + "limite_quantidade_votos = ?, data_expiracao = ?, status = ?, usuario_idusuario = ?, categoria_idcategoria = ? "
                + "WHERE idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherParametros(stmt, enquete, true);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStatus(int idEnquete, String status) {
        String sql = "UPDATE enquete SET status = ? WHERE idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, idEnquete);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM enquete WHERE idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void atualizarEnquetesExpiradas() {
        String sql = "UPDATE enquete SET status = 'ENCERRADA' WHERE status = 'EM_CURSO' AND data_expiracao <= NOW()";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int contarPorStatus(String status) {
        String sql = "SELECT COUNT(*) FROM enquete WHERE status = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM enquete";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<EnqueteModel> listar(String sql, Integer parametro) {
        List<EnqueteModel> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parametro != null) {
                stmt.setInt(1, parametro);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarEnquete(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private static void preencherParametros(PreparedStatement stmt, EnqueteModel enquete, boolean incluirId) throws SQLException {
        stmt.setString(1, enquete.getTitulo());
        stmt.setString(2, enquete.getDescricao());
        stmt.setString(3, enquete.getTipoVotacao());
        stmt.setInt(4, enquete.getLimiteVotosIp());
        stmt.setInt(5, enquete.getLimiteQuantidadeVotos());
        stmt.setTimestamp(6, enquete.getDataExpiracao() == null ? null : Timestamp.valueOf(enquete.getDataExpiracao()));
        stmt.setString(7, enquete.getStatus());
        stmt.setInt(8, enquete.getUsuario().getIdUsuario());
        stmt.setInt(9, enquete.getCategoria().getIdCategoria());
        if (incluirId) {
            stmt.setInt(10, enquete.getIdEnquete());
        }
    }

    private static EnqueteModel montarEnquete(ResultSet rs) throws SQLException {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setIdUsuario(rs.getInt("idusuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));

        CategoriaModel categoria = new CategoriaModel();
        categoria.setIdCategoria(rs.getInt("idcategoria"));
        categoria.setNomeCategoria(rs.getString("nome_categoria"));

        EnqueteModel enquete = new EnqueteModel();
        enquete.setIdEnquete(rs.getInt("idenquete"));
        enquete.setTitulo(rs.getString("titulo"));
        enquete.setDescricao(rs.getString("descricao"));
        enquete.setTipoVotacao(rs.getString("tipo_votacao"));
        enquete.setLimiteVotosIp(rs.getInt("limite_votos_ip"));
        enquete.setLimiteQuantidadeVotos(rs.getInt("limite_quantidade_votos"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        if (criacao != null) {
            enquete.setDataCriacao(criacao.toLocalDateTime());
        }
        Timestamp expiracao = rs.getTimestamp("data_expiracao");
        if (expiracao != null) {
            enquete.setDataExpiracao(expiracao.toLocalDateTime());
        }
        enquete.setStatus(rs.getString("status"));
        enquete.setUsuario(usuario);
        enquete.setCategoria(categoria);
        return enquete;
    }
}
