package model;

public class OpcaoRespostaModel {

    private int idOpcao;
    private String descricaoOpcao;
    private EnqueteModel enquete;

    public OpcaoRespostaModel() {
    }

	public OpcaoRespostaModel(int idOpcao, String descricaoOpcao, EnqueteModel enquete) {
		super();
		this.idOpcao = idOpcao;
		this.descricaoOpcao = descricaoOpcao;
		this.enquete = enquete;
	}

	public int getIdOpcao() {
		return idOpcao;
	}

	public void setIdOpcao(int idOpcao) {
		this.idOpcao = idOpcao;
	}

	public String getDescricaoOpcao() {
		return descricaoOpcao;
	}

	public void setDescricaoOpcao(String descricaoOpcao) {
		this.descricaoOpcao = descricaoOpcao;
	}

	public EnqueteModel getEnquete() {
		return enquete;
	}

	public void setEnquete(EnqueteModel enquete) {
		this.enquete = enquete;
	}
}