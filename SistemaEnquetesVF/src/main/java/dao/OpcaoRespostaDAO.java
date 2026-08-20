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

        String sql =
            "INSERT INTO opcao_resposta " +
            "(descricao_opcao, enquete_idenquete) " +
            "VALUES (?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(
                1,
                opcao.getDescricaoOpcao()
            );

            stmt.setInt(
                2,
                opcao.getEnquete().getIdEnquete()
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

    public List<OpcaoRespostaModel> listarPorEnquete(
            int idEnquete) {

        List<OpcaoRespostaModel> lista = new ArrayList<>();

        String sql =
            "SELECT idopcao, descricao_opcao, " +
            "enquete_idenquete " +
            "FROM opcao_resposta " +
            "WHERE enquete_idenquete = ? " +
            "ORDER BY idopcao";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(sql)) {

            stmt.setInt(1, idEnquete);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    EnqueteModel enquete =
                        new EnqueteModel();

                    enquete.setIdEnquete(
                        rs.getInt("enquete_idenquete")
                    );

                    OpcaoRespostaModel opcao =
                        new OpcaoRespostaModel();

                    opcao.setIdOpcao(
                        rs.getInt("idopcao")
                    );

                    opcao.setDescricaoOpcao(
                        rs.getString("descricao_opcao")
                    );

                    opcao.setEnquete(enquete);

                    lista.add(opcao);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public OpcaoRespostaModel buscarPorId(int id) {

        String sql =
            "SELECT idopcao, descricao_opcao, " +
            "enquete_idenquete " +
            "FROM opcao_resposta " +
            "WHERE idopcao = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    EnqueteModel enquete =
                        new EnqueteModel();

                    enquete.setIdEnquete(
                        rs.getInt("enquete_idenquete")
                    );

                    OpcaoRespostaModel opcao =
                        new OpcaoRespostaModel();

                    opcao.setIdOpcao(
                        rs.getInt("idopcao")
                    );

                    opcao.setDescricaoOpcao(
                        rs.getString("descricao_opcao")
                    );

                    opcao.setEnquete(enquete);

                    return opcao;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizar(OpcaoRespostaModel opcao) {

        String sql =
            "UPDATE opcao_resposta SET " +
            "descricao_opcao = ?, enquete_idenquete = ? " +
            "WHERE idopcao = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                 conn.prepareStatement(sql)) {

            stmt.setString(
                1,
                opcao.getDescricaoOpcao()
            );

            stmt.setInt(
                2,
                opcao.getEnquete().getIdEnquete()
            );

            stmt.setInt(
                3,
                opcao.getIdOpcao()
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {

        String sql =
            "DELETE FROM opcao_resposta WHERE idopcao = ?";

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
}