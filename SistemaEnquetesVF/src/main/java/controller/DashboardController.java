package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoriaDAO;
import dao.EnqueteDAO;
import dao.OpcaoRespostaDAO;
import dao.UsuarioDAO;
import dao.VotoDAO;
import model.EnqueteModel;
import model.OpcaoRespostaModel;
import model.UsuarioModel;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EnqueteDAO enqueteDAO;
    private CategoriaDAO categoriaDAO;
    private OpcaoRespostaDAO opcaoDAO;
    private VotoDAO votoDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        enqueteDAO = new EnqueteDAO();
        categoriaDAO = new CategoriaDAO();
        opcaoDAO = new OpcaoRespostaDAO();
        votoDAO = new VotoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsuarioModel usuarioSessao = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuarioSessao == null) {
            response.sendRedirect(request.getContextPath() + "/inicio?login=necessario");
            return;
        }

        UsuarioModel usuario = usuarioDAO.buscarPorId(usuarioSessao.getIdUsuario());
        if (usuario == null || usuario.getStatus() != 1) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/inicio?erro=usuarioInativo");
            return;
        }
        session.setAttribute("usuarioLogado", usuario);

        enqueteDAO.atualizarEnquetesExpiradas();
        List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
        Map<Integer, List<OpcaoRespostaModel>> opcoes = new HashMap<>();
        Map<Integer, Integer> totalPorEnquete = new HashMap<>();
        Map<Integer, Integer> participantesPorEnquete = new HashMap<>();
        Map<Integer, Integer> votosPorOpcao = new HashMap<>();

        for (EnqueteModel e : enquetes) {
            List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
            opcoes.put(e.getIdEnquete(), lista);
            totalPorEnquete.put(e.getIdEnquete(), votoDAO.contarVotosEnquete(e.getIdEnquete()));
            participantesPorEnquete.put(e.getIdEnquete(), votoDAO.contarParticipantesEnquete(e.getIdEnquete()));
            for (OpcaoRespostaModel o : lista) {
                votosPorOpcao.put(o.getIdOpcao(), votoDAO.contarVotosPorOpcao(o.getIdOpcao()));
            }
        }

        Set<Integer> enquetesVotadas = new HashSet<>(votoDAO.listarIdsEnquetesVotadasPorUsuario(usuario.getIdUsuario()));
        request.setAttribute("enquetes", enquetes);
        request.setAttribute("categorias", categoriaDAO.listarTodos());
        request.setAttribute("opcoesPorEnquete", opcoes);
        request.setAttribute("totalPorEnquete", totalPorEnquete);
        request.setAttribute("participantesPorEnquete", participantesPorEnquete);
        request.setAttribute("votosPorOpcao", votosPorOpcao);
        request.setAttribute("enquetesVotadas", enquetesVotadas);
        request.setAttribute("totalVotos", votoDAO.contarTodosVotos());
        request.setAttribute("enquetesRespondidas", votoDAO.contarEnquetesVotadasUsuario(usuario.getIdUsuario()));
        request.setAttribute("votosUsuario", votoDAO.contarVotosUsuario(usuario.getIdUsuario()));
        request.setAttribute("opcoesVotadas", new HashSet<>(votoDAO.listarIdsOpcoesVotadasPorUsuario(usuario.getIdUsuario())));
        request.setAttribute("enquetesAtivas", enqueteDAO.contarPorStatus("EM_CURSO"));
        request.setAttribute("totalEnquetes", enqueteDAO.contarTodos());
        request.setAttribute("categoriaMaisVotada", votoDAO.buscarCategoriaMaisVotada());
        request.setAttribute("totalUsuariosAtivos", usuarioDAO.contarAtivos());

        boolean admin = usuario.getNivelAcesso() != null && usuario.getNivelAcesso().getIdNivelAcesso() == 2;
        request.getRequestDispatcher(admin ? "/WEB-INF/views/usuario_adm.jsp" : "/WEB-INF/views/usuario_comum.jsp")
                .forward(request, response);
    }
}
