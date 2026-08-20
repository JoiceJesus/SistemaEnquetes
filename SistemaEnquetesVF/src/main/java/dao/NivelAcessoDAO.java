package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.NivelAcessoModel;
import util.Conexao;

public class NivelAcessoDAO {

    public boolean inserir(NivelAcessoModel nivel) {

        String sql = "INSERT INTO nivel_acesso (tipo, permissoes) " +
                     "VALUES (?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, nivel.getTipo());
            stmt.setString(2, nivel.getPermissoes());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<NivelAcessoModel> listarTodos() {

        List<NivelAcessoModel> lista = new ArrayList<>();

        String sql = "SELECT idnivel_acesso, tipo, permissoes " +
                     "FROM nivel_acesso ORDER BY idnivel_acesso";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                NivelAcessoModel nivel = new NivelAcessoModel();

                nivel.setIdNivelAcesso(
                    rs.getInt("idnivel_acesso")
                );

                nivel.setTipo(rs.getString("tipo"));
                nivel.setPermissoes(rs.getString("permissoes"));

                lista.add(nivel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public NivelAcessoModel buscarPorId(int id) {

        String sql = "SELECT idnivel_acesso, tipo, permissoes " +
                     "FROM nivel_acesso WHERE idnivel_acesso = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    NivelAcessoModel nivel = new NivelAcessoModel();

                    nivel.setIdNivelAcesso(
                        rs.getInt("idnivel_acesso")
                    );

                    nivel.setTipo(rs.getString("tipo"));
                    nivel.setPermissoes(rs.getString("permissoes"));

                    return nivel;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizar(NivelAcessoModel nivel) {

        String sql = "UPDATE nivel_acesso SET tipo = ?, " +
                     "permissoes = ? WHERE idnivel_acesso = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nivel.getTipo());
            stmt.setString(2, nivel.getPermissoes());
            stmt.setInt(3, nivel.getIdNivelAcesso());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {

        String sql = "DELETE FROM nivel_acesso " +
                     "WHERE idnivel_acesso = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public NivelAcessoModel buscarPorTipo(String tipo) {

        String sql = """
                SELECT
                    idnivel_acesso,
                    tipo,
                    permissoes
                FROM nivel_acesso
                WHERE tipo = ?
                """;

        try (
            Connection conn = Conexao.getConnection();
            PreparedStatement stmt =
                conn.prepareStatement(sql)
        ) {

            stmt.setString(1, tipo);

            ResultSet rs =
                stmt.executeQuery();

            if (rs.next()) {

                NivelAcessoModel nivel =
                    new NivelAcessoModel();

                nivel.setIdNivelAcesso(
                    rs.getInt("idnivel_acesso")
                );

                nivel.setTipo(
                    rs.getString("tipo")
                );

                nivel.setPermissoes(
                    rs.getString("permissoes")
                );

                return nivel;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}