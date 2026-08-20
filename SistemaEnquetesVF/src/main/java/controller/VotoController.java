package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.EnqueteDAO;
import dao.OpcaoRespostaDAO;
import dao.VotoDAO;
import model.EnqueteModel;
import model.OpcaoRespostaModel;
import model.UsuarioModel;
import model.VotoModel;
import util.DataHoraUtil;

@WebServlet("/voto")
public class VotoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private VotoDAO votoDAO;
    private EnqueteDAO enqueteDAO;
    private OpcaoRespostaDAO opcaoDAO;

    @Override
    public void init() {
        votoDAO = new VotoDAO();
        enqueteDAO = new EnqueteDAO();
        opcaoDAO = new OpcaoRespostaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsuarioModel usuario = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/inicio?login=necessario");
            return;
        }

        Integer idEnquete = parseInt(request.getParameter("idEnquete"));
        if (idEnquete == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=votoInvalido");
            return;
        }

        EnqueteModel enquete = enqueteDAO.buscarPorId(idEnquete);
        if (enquete == null || !"EM_CURSO".equals(enquete.getStatus())
                || enquete.getDataExpiracao() == null
                || !enquete.getDataExpiracao().isAfter(DataHoraUtil.agora())) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=encerrada");
            return;
        }

        Set<Integer> idsOpcoes = new LinkedHashSet<>();
        String[] valores = request.getParameterValues("idOpcao");
        if (valores != null) {
            for (String valor : valores) {
                Integer id = parseInt(valor);
                if (id != null) {
                    idsOpcoes.add(id);
                }
            }
        }
        if (idsOpcoes.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=semOpcao");
            return;
        }
        if ("UNICA".equals(enquete.getTipoVotacao()) && idsOpcoes.size() != 1) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=tipoVotacao");
            return;
        }

        for (Integer idOpcao : idsOpcoes) {
            if (!opcaoDAO.pertenceAEnquete(idOpcao, idEnquete)) {
                response.sendRedirect(request.getContextPath() + "/dashboard?erro=opcao");
                return;
            }
        }

        if (votoDAO.usuarioJaVotou(usuario.getIdUsuario(), idEnquete)) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=jaVotou");
            return;
        }

        String ip = obterIp(request);
        if (enquete.getLimiteVotosIp() > 0
                && votoDAO.contarUsuariosPorIp(ip, idEnquete) >= enquete.getLimiteVotosIp()) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=limiteIp");
            return;
        }

        int totalAtual = votoDAO.contarVotosEnquete(idEnquete);
        if (enquete.getLimiteQuantidadeVotos() > 0
                && totalAtual + idsOpcoes.size() > enquete.getLimiteQuantidadeVotos()) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=limiteTotal");
            return;
        }

        List<VotoModel> votos = new ArrayList<>();
        for (Integer idOpcao : idsOpcoes) {
            OpcaoRespostaModel opcao = new OpcaoRespostaModel();
            opcao.setIdOpcao(idOpcao);
            VotoModel voto = new VotoModel();
            voto.setDataHoraVoto(DataHoraUtil.agora());
            voto.setIpVoto(ip);
            voto.setUsuario(usuario);
            voto.setEnquete(enquete);
            voto.setOpcaoResposta(opcao);
            votos.add(voto);
        }

        if (!votoDAO.inserirLote(votos)) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=voto");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/dashboard?msg=votoRegistrado");
    }

    private static String obterIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static Integer parseInt(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }
}
