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
import dao.VotoDAO;
import model.CategoriaModel;
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

    @Override
    public void init() {
        enqueteDAO = new EnqueteDAO();
        categoriaDAO = new CategoriaDAO();
        opcaoDAO = new OpcaoRespostaDAO();
        votoDAO = new VotoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UsuarioModel usuario = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?login=necessario");
            return;
        }

        enqueteDAO.atualizarEnquetesExpiradas();
        List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
        List<CategoriaModel> categorias = categoriaDAO.listarTodos();
        Map<Integer, List<OpcaoRespostaModel>> opcoes = new HashMap<>();
        Map<Integer, Integer> totalPorEnquete = new HashMap<>();
        Map<Integer, Integer> votosPorOpcao = new HashMap<>();

        for (EnqueteModel e : enquetes) {
            List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
            opcoes.put(e.getIdEnquete(), lista);
            totalPorEnquete.put(e.getIdEnquete(), votoDAO.contarVotosEnquete(e.getIdEnquete()));
            for (OpcaoRespostaModel o : lista) {
                votosPorOpcao.put(o.getIdOpcao(), votoDAO.contarVotosPorOpcao(o.getIdOpcao()));
            }
        }

        Set<Integer> enquetesVotadas = new HashSet<>(votoDAO.listarIdsEnquetesVotadasPorUsuario(usuario.getIdUsuario()));
        request.setAttribute("enquetes", enquetes);
        request.setAttribute("categorias", categorias);
        request.setAttribute("opcoesPorEnquete", opcoes);
        request.setAttribute("totalPorEnquete", totalPorEnquete);
        request.setAttribute("votosPorOpcao", votosPorOpcao);
        request.setAttribute("enquetesVotadas", enquetesVotadas);
        request.setAttribute("totalVotos", votoDAO.contarTodosVotos());
        request.setAttribute("votosUsuario", votoDAO.contarVotosUsuario(usuario.getIdUsuario()));
        request.setAttribute("enquetesAtivas", enqueteDAO.contarPorStatus("EM_CURSO"));
        request.setAttribute("categoriaMaisVotada", votoDAO.buscarCategoriaMaisVotada());
        request.setAttribute("enquetesCriadas", enqueteDAO.contarPorUsuario(usuario.getIdUsuario()));

        boolean admin = usuario.getNivelAcesso() != null && usuario.getNivelAcesso().getIdNivelAcesso() == 2;
        request.getRequestDispatcher(admin ? "/usuario_adm.jsp" : "/usuario_comum.jsp").forward(request, response);
    }
}
