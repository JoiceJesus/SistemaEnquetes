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
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CategoriaModel> listarTodos() {
        List<CategoriaModel> lista = new ArrayList<>();
        String sql = "SELECT idcategoria, nome_categoria FROM categoria ORDER BY nome_categoria";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public CategoriaModel buscarPorId(int id) {
        String sql = "SELECT idcategoria, nome_categoria FROM categoria WHERE idcategoria = ?";
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

    public boolean atualizar(CategoriaModel categoria) {
        String sql = "UPDATE categoria SET nome_categoria = ? WHERE idcategoria = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean nomeExiste(String nome, Integer ignorarId) {
        String sql = "SELECT 1 FROM categoria WHERE LOWER(nome_categoria) = LOWER(?)" + (ignorarId == null ? "" : " AND idcategoria <> ?") + " LIMIT 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            if (ignorarId != null) {
                stmt.setInt(2, ignorarId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static CategoriaModel montar(ResultSet rs) throws SQLException {
        CategoriaModel categoria = new CategoriaModel();
        categoria.setIdCategoria(rs.getInt("idcategoria"));
        categoria.setNomeCategoria(rs.getString("nome_categoria"));
        return categoria;
    }
}
