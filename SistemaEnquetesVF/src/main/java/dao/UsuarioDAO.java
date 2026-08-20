package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.NivelAcessoModel;
import model.UsuarioModel;
import util.Conexao;

public class UsuarioDAO {

    public boolean inserir(UsuarioModel usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha, status, nivel_acesso_idnivel_acesso) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getStatus());
            stmt.setInt(5, usuario.getNivelAcesso().getIdNivelAcesso());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UsuarioModel autenticar(String email, String senha) {
        String sql = "SELECT u.*, n.tipo, n.permissoes FROM usuario u "
                + "INNER JOIN nivel_acesso n ON n.idnivel_acesso = u.nivel_acesso_idnivel_acesso "
                + "WHERE LOWER(u.email) = LOWER(?) AND u.senha = ? AND u.status = 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UsuarioModel buscarPorId(int id) {
        String sql = "SELECT u.*, n.tipo, n.permissoes FROM usuario u "
                + "INNER JOIN nivel_acesso n ON n.idnivel_acesso = u.nivel_acesso_idnivel_acesso "
                + "WHERE u.idusuario = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UsuarioModel> listarTodos() {
        List<UsuarioModel> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, n.tipo, n.permissoes FROM usuario u "
                + "INNER JOIN nivel_acesso n ON n.idnivel_acesso = u.nivel_acesso_idnivel_acesso "
                + "ORDER BY u.nome, u.idusuario";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean atualizar(UsuarioModel usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, status = ?, nivel_acesso_idnivel_acesso = ? WHERE idusuario = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getStatus());
            stmt.setInt(5, usuario.getNivelAcesso().getIdNivelAcesso());
            stmt.setInt(6, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStatus(int idUsuario, int status) {
        String sql = "UPDATE usuario SET status = ? WHERE idusuario = ?";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExiste(String email, Integer ignorarId) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = LOWER(?)" + (ignorarId == null ? "" : " AND idusuario <> ?") + " LIMIT 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
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

    public int contarAtivos() {
        String sql = "SELECT COUNT(*) FROM usuario WHERE status = 1";
        try (Connection conn = Conexao.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private UsuarioModel montarUsuario(ResultSet rs) throws SQLException {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setIdUsuario(rs.getInt("idusuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        Timestamp cadastro = rs.getTimestamp("data_cadastro");
        if (cadastro != null) {
            usuario.setDataCadastro(cadastro.toLocalDateTime());
        }
        usuario.setStatus(rs.getInt("status"));
        NivelAcessoModel nivel = new NivelAcessoModel();
        nivel.setIdNivelAcesso(rs.getInt("nivel_acesso_idnivel_acesso"));
        nivel.setTipo(rs.getString("tipo"));
        nivel.setPermissoes(rs.getString("permissoes"));
        usuario.setNivelAcesso(nivel);
        return usuario;
    }
}
