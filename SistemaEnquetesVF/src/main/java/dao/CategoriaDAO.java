package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CategoriaModel;
import util.Conexao;

public class CategoriaDAO {

    public boolean inserir(CategoriaModel categoria) {

        String sql = "INSERT INTO categoria (nome_categoria) VALUES (?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNomeCategoria());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CategoriaModel> listarTodos() {

        List<CategoriaModel> lista = new ArrayList<>();

        String sql = "SELECT idcategoria, nome_categoria " +
                     "FROM categoria ORDER BY nome_categoria";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                CategoriaModel categoria = new CategoriaModel();

                categoria.setIdCategoria(
                    rs.getInt("idcategoria")
                );

                categoria.setNomeCategoria(
                    rs.getString("nome_categoria")
                );

                lista.add(categoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public CategoriaModel buscarPorId(int id) {

        String sql = "SELECT idcategoria, nome_categoria " +
                     "FROM categoria WHERE idcategoria = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    CategoriaModel categoria = new CategoriaModel();

                    categoria.setIdCategoria(
                        rs.getInt("idcategoria")
                    );

                    categoria.setNomeCategoria(
                        rs.getString("nome_categoria")
                    );

                    return categoria;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean atualizar(CategoriaModel categoria) {

        String sql = "UPDATE categoria SET nome_categoria = ? " +
                     "WHERE idcategoria = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setInt(2, categoria.getIdCategoria());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {

        String sql = "DELETE FROM categoria WHERE idcategoria = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}