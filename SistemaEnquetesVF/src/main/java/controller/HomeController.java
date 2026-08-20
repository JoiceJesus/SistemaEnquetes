package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.EnqueteDAO;
import dao.OpcaoRespostaDAO;
import dao.UsuarioDAO;
import dao.VotoDAO;
import model.EnqueteModel;
import model.OpcaoRespostaModel;

@WebServlet("/inicio")
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EnqueteDAO enqueteDAO;
    private OpcaoRespostaDAO opcaoDAO;
    private VotoDAO votoDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        enqueteDAO = new EnqueteDAO();
        opcaoDAO = new OpcaoRespostaDAO();
        votoDAO = new VotoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        enqueteDAO.atualizarEnquetesExpiradas();

        List<EnqueteModel> destaques = enqueteDAO.listarDestaques(3);
        Map<Integer, List<OpcaoRespostaModel>> opcoes = new HashMap<>();
        Map<Integer, Integer> totais = new HashMap<>();

        for (EnqueteModel enquete : destaques) {
            opcoes.put(enquete.getIdEnquete(), opcaoDAO.listarPorEnquete(enquete.getIdEnquete()));
            totais.put(enquete.getIdEnquete(), votoDAO.contarVotosEnquete(enquete.getIdEnquete()));
        }

        request.setAttribute("destaques", destaques);
        request.setAttribute("opcoesPorEnquete", opcoes);
        request.setAttribute("totalPorEnquete", totais);
        request.setAttribute("totalEnquetes", enqueteDAO.contarTodos());
        request.setAttribute("enquetesAtivas", enqueteDAO.contarPorStatus("EM_CURSO"));
        request.setAttribute("totalVotos", votoDAO.contarTodosVotos());
        request.setAttribute("usuariosAtivos", usuarioDAO.contarAtivos());

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
