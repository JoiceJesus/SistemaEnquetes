package model;

import java.time.LocalDateTime;

public class EnqueteModel {

    private int idEnquete;
    private String titulo;
    private String descricao;
    private String tipoVotacao;
    private int limiteVotosIp;
    private int limiteQuantidadeVotos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;
    private String status;

    private UsuarioModel usuario;
    private CategoriaModel categoria;

    public EnqueteModel() {
    }

public EnqueteModel(int idEnquete, String titulo, String descricao, String tipoVotacao, int limiteVotosIp,
			int limiteQuantidadeVotos, LocalDateTime dataCriacao, LocalDateTime dataExpiracao, String status,
			UsuarioModel usuario, CategoriaModel categoria) {
		super();
		this.idEnquete = idEnquete;
		this.titulo = titulo;
		this.descricao = descricao;
		this.tipoVotacao = tipoVotacao;
		this.limiteVotosIp = limiteVotosIp;
		this.limiteQuantidadeVotos = limiteQuantidadeVotos;
		this.dataCriacao = dataCriacao;
		this.dataExpiracao = dataExpiracao;
		this.status = status;
		this.usuario = usuario;
		this.categoria = categoria;
	}

	public int getIdEnquete() {
		return idEnquete;
	}

	public void setIdEnquete(int idEnquete) {
		this.idEnquete = idEnquete;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipoVotacao() {
		return tipoVotacao;
	}

	public void setTipoVotacao(String tipoVotacao) {
		this.tipoVotacao = tipoVotacao;
	}

	public int getLimiteVotosIp() {
		return limiteVotosIp;
	}

	public void setLimiteVotosIp(int limiteVotosIp) {
		this.limiteVotosIp = limiteVotosIp;
	}

	public int getLimiteQuantidadeVotos() {
		return limiteQuantidadeVotos;
	}

	public void setLimiteQuantidadeVotos(int limiteQuantidadeVotos) {
		this.limiteQuantidadeVotos = limiteQuantidadeVotos;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(LocalDateTime dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UsuarioModel getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioModel usuario) {
		this.usuario = usuario;
	}

	public CategoriaModel getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaModel categoria) {
		this.categoria = categoria;
	}


    
}
