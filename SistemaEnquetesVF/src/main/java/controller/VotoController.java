package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.EnqueteDAO;
import dao.VotoDAO;
import model.EnqueteModel;
import model.UsuarioModel;
import model.VotoModel;
import model.OpcaoRespostaModel;

@WebServlet("/voto")
public class VotoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private VotoDAO votoDAO;
    private EnqueteDAO enqueteDAO;

    @Override
    public void init() {
        votoDAO = new VotoDAO();
        enqueteDAO = new EnqueteDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        UsuarioModel usuario = session == null ? null
                : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idEnquete = Integer.parseInt(request.getParameter("idEnquete"));
        String[] opcoes = request.getParameterValues("idOpcao");
        String ip = request.getRemoteAddr();

        EnqueteModel enquete = enqueteDAO.buscarPorId(idEnquete);
        if (enquete == null || !"EM_CURSO".equals(enquete.getStatus())
                || enquete.getDataExpiracao() == null
                || !enquete.getDataExpiracao().isAfter(LocalDateTime.now())) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=encerrada");
            return;
        }

        if (opcoes == null || opcoes.length == 0) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=semOpcao");
            return;
        }

        if (votoDAO.usuarioJaVotou(usuario.getIdUsuario(), idEnquete)) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=jaVotou");
            return;
        }

        if (enquete.getLimiteVotosIp() > 0 && votoDAO.contarParticipantesPorIp(idEnquete) >= enquete.getLimiteVotosIp()
                && !votoDAO.ipJaVotou(ip, idEnquete)) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=limiteIp");
            return;
        }

        if (enquete.getLimiteQuantidadeVotos() > 0
                && votoDAO.contarVotosEnquete(idEnquete) >= enquete.getLimiteQuantidadeVotos()) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=limiteTotal");
            return;
        }

        if ("UNICA".equals(enquete.getTipoVotacao()) && opcoes.length != 1) {
            response.sendRedirect(request.getContextPath() + "/enquete?erro=tipoVotacao");
            return;
        }

        for (String opcaoId : Arrays.asList(opcoes)) {
            try {
                OpcaoRespostaModel opcao = new OpcaoRespostaModel();
                opcao.setIdOpcao(Integer.parseInt(opcaoId));
                VotoModel voto = new VotoModel();
                voto.setDataHoraVoto(LocalDateTime.now());
                voto.setUsuario(usuario);
                voto.setEnquete(enquete);
                voto.setOpcaoResposta(opcao);
                voto.setIpVoto(ip);
                if (!votoDAO.inserir(voto)) {
                    response.sendRedirect(request.getContextPath() + "/enquete?erro=voto");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/enquete?erro=opcao");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/enquete?sucesso=voto");
    }
}
