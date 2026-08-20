package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.EnqueteModel;
import model.OpcaoRespostaModel;
import util.Conexao;

public class OpcaoRespostaDAO {

    public int inserir(OpcaoRespostaModel opcao) {
        String sql = "INSERT INTO opcao_resposta (descricao_opcao, enquete_idenquete) VALUES (?, ?)";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, opcao.getDescricaoOpcao());
            stmt.setInt(2, opcao.getEnquete().getIdEnquete());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<OpcaoRespostaModel> listarPorEnquete(int idEnquete) {
        List<OpcaoRespostaModel> lista = new ArrayList<>();
        String sql = "SELECT idopcao, descricao_opcao, enquete_idenquete FROM opcao_resposta WHERE enquete_idenquete = ? ORDER BY idopcao";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEnquete);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public OpcaoRespostaModel buscarPorId(int id) {
        String sql = "SELECT idopcao, descricao_opcao, enquete_idenquete FROM opcao_resposta WHERE idopcao = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean pertenceAEnquete(int idOpcao, int idEnquete) {
        String sql = "SELECT 1 FROM opcao_resposta WHERE idopcao = ? AND enquete_idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOpcao);
            stmt.setInt(2, idEnquete);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(OpcaoRespostaModel opcao) {
        String sql = "UPDATE opcao_resposta SET descricao_opcao = ? WHERE idopcao = ? AND enquete_idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, opcao.getDescricaoOpcao());
            stmt.setInt(2, opcao.getIdOpcao());
            stmt.setInt(3, opcao.getEnquete().getIdEnquete());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM opcao_resposta WHERE idopcao = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contarPorEnquete(int idEnquete) {
        String sql = "SELECT COUNT(*) FROM opcao_resposta WHERE enquete_idenquete = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEnquete);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean descricaoExiste(int idEnquete, String descricao, Integer ignorarId) {
        String sql = "SELECT 1 FROM opcao_resposta WHERE enquete_idenquete = ? AND LOWER(descricao_opcao) = LOWER(?)"
                + (ignorarId == null ? "" : " AND idopcao <> ?") + " LIMIT 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEnquete);
            stmt.setString(2, descricao);
            if (ignorarId != null) {
                stmt.setInt(3, ignorarId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static OpcaoRespostaModel montar(ResultSet rs) throws SQLException {
        EnqueteModel enquete = new EnqueteModel();
        enquete.setIdEnquete(rs.getInt("enquete_idenquete"));
        OpcaoRespostaModel opcao = new OpcaoRespostaModel();
        opcao.setIdOpcao(rs.getInt("idopcao"));
        opcao.setDescricaoOpcao(rs.getString("descricao_opcao"));
        opcao.setEnquete(enquete);
        return opcao;
    }
}
