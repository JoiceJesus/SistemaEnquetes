package model;

import java.time.LocalDateTime;

public class UsuarioModel {

    private int idUsuario;
    private String nome;
    private String email;
    private String senha;
    private LocalDateTime dataCadastro;
    private int status;
    private NivelAcessoModel nivelAcesso;

    public UsuarioModel() {
    }

    public UsuarioModel(int idUsuario, String nome, String email, String senha,
                        LocalDateTime dataCadastro, int status, NivelAcessoModel nivelAcesso) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.status = status;
        this.nivelAcesso = nivelAcesso;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public NivelAcessoModel getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(NivelAcessoModel nivelAcesso) { this.nivelAcesso = nivelAcesso; }
}
