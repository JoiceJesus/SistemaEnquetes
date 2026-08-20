package model;

public class NivelAcessoModel {

    private int idNivelAcesso;
    private String tipo;
    private String permissoes;

    public NivelAcessoModel() {
    }

    public NivelAcessoModel(int idNivelAcesso, String tipo, String permissoes) {
        this.idNivelAcesso = idNivelAcesso;
        this.tipo = tipo;
        this.permissoes = permissoes;
    }

	public int getIdNivelAcesso() {
		return idNivelAcesso;
	}

	public void setIdNivelAcesso(int idNivelAcesso) {
		this.idNivelAcesso = idNivelAcesso;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(String permissoes) {
		this.permissoes = permissoes;
	}

    
}