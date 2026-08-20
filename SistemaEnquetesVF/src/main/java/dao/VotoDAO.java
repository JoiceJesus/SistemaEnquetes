package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.VotoModel;
import util.Conexao;

public class VotoDAO {

    public boolean inserirLote(List<VotoModel> votos) {
        if (votos == null || votos.isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO voto (data_hora_voto, ip_voto, usuario_idusuario, enquete_idenquete, opcao_resposta_idopcao) "
                + "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (VotoModel voto : votos) {
                    stmt.setTimestamp(1, voto.getDataHoraVoto() == null ? null : Timestamp.valueOf(voto.getDataHoraVoto()));
                    stmt.setString(2, voto.getIpVoto() == null ? "" : voto.getIpVoto());
                    stmt.setInt(3, voto.getUsuario().getIdUsuario());
                    stmt.setInt(4, voto.getEnquete().getIdEnquete());
                    stmt.setInt(5, voto.getOpcaoResposta().getIdOpcao());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
                Conexao.fechar(conn);
            }
        }
    }

    public boolean usuarioJaVotou(int idUsuario, int idEnquete) {
        String sql = "SELECT 1 FROM voto WHERE usuario_idusuario = ? AND enquete_idenquete = ? LIMIT 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idEnquete);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contarVotosEnquete(int idEnquete) {
        return contar("SELECT COUNT(*) FROM voto WHERE enquete_idenquete = ?", idEnquete);
    }

    public int contarParticipantesEnquete(int idEnquete) {
        return contar("SELECT COUNT(DISTINCT usuario_idusuario) FROM voto WHERE enquete_idenquete = ?", idEnquete);
    }

    public int contarTodosVotos() {
        String sql = "SELECT COUNT(*) FROM voto";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int contarEnquetesVotadasUsuario(int idUsuario) {
        return contar("SELECT COUNT(DISTINCT enquete_idenquete) FROM voto WHERE usuario_idusuario = ?", idUsuario);
    }
    public int contarVotosUsuario(int idUsuario) {
        return contar("SELECT COUNT(*) FROM voto WHERE usuario_idusuario = ?", idUsuario);
    }

    public List<Integer> listarIdsOpcoesVotadasPorUsuario(int idUsuario) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT opcao_resposta_idopcao FROM voto WHERE usuario_idusuario = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }


    public List<Integer> listarIdsEnquetesVotadasPorUsuario(int idUsuario) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT enquete_idenquete FROM voto WHERE usuario_idusuario = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public int contarUsuariosPorIp(String ip, int idEnquete) {
        String sql = "SELECT COUNT(DISTINCT usuario_idusuario) FROM voto WHERE ip_voto = ? AND enquete_idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setInt(2, idEnquete);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String buscarCategoriaMaisVotada() {
        String sql = "SELECT c.nome_categoria, COUNT(v.idvoto) total FROM categoria c "
                + "JOIN enquete e ON e.categoria_idcategoria = c.idcategoria "
                + "JOIN voto v ON v.enquete_idenquete = e.idenquete "
                + "GROUP BY c.idcategoria, c.nome_categoria ORDER BY total DESC, c.nome_categoria LIMIT 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getString(1) : "Sem votos";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Sem votos";
        }
    }

    public int contarVotosPorOpcao(int idOpcao) {
        return contar("SELECT COUNT(*) FROM voto WHERE opcao_resposta_idopcao = ?", idOpcao);
    }

    private int contar(String sql, int parametro) {
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
