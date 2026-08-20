package model;

import java.time.LocalDateTime;

public class VotoModel {
    private long idVoto;
    private LocalDateTime dataHoraVoto;
    private String ipVoto;
    private UsuarioModel usuario;
    private EnqueteModel enquete;
    private OpcaoRespostaModel opcaoResposta;

    public VotoModel() {}

    public long getIdVoto() { return idVoto; }
    public void setIdVoto(long idVoto) { this.idVoto = idVoto; }
    public LocalDateTime getDataHoraVoto() { return dataHoraVoto; }
    public void setDataHoraVoto(LocalDateTime dataHoraVoto) { this.dataHoraVoto = dataHoraVoto; }
    public String getIpVoto() { return ipVoto; }
    public void setIpVoto(String ipVoto) { this.ipVoto = ipVoto; }
    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
    public EnqueteModel getEnquete() { return enquete; }
    public void setEnquete(EnqueteModel enquete) { this.enquete = enquete; }
    public OpcaoRespostaModel getOpcaoResposta() { return opcaoResposta; }
    public void setOpcaoResposta(OpcaoRespostaModel opcaoResposta) { this.opcaoResposta = opcaoResposta; }
}
