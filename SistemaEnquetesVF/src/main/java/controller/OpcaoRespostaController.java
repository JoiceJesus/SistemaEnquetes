package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.EnqueteDAO;
import dao.OpcaoRespostaDAO;
import model.EnqueteModel;
import model.OpcaoRespostaModel;
import model.UsuarioModel;

@WebServlet("/opcao")
public class OpcaoRespostaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private OpcaoRespostaDAO opcaoDAO;
    private EnqueteDAO enqueteDAO;

    @Override
    public void init() {
        opcaoDAO = new OpcaoRespostaDAO();
        enqueteDAO = new EnqueteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ehAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer idEnquete = parseInt(request.getParameter("idEnquete"));
        EnqueteModel enquete = idEnquete == null ? null : enqueteDAO.buscarPorId(idEnquete);
        if (enquete == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("enquete", enquete);
        request.setAttribute("opcoes", opcaoDAO.listarPorEnquete(idEnquete));
        request.getRequestDispatcher("/WEB-INF/views/opcao.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ehAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer idEnquete = parseInt(request.getParameter("idEnquete"));
        EnqueteModel enquete = idEnquete == null ? null : enqueteDAO.buscarPorId(idEnquete);
        if (enquete == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String acao = request.getParameter("acao");
        if ("excluir".equals(acao)) {
            Integer idOpcao = parseInt(request.getParameter("idOpcao"));
            if (idOpcao == null || !opcaoDAO.pertenceAEnquete(idOpcao, idEnquete)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (opcaoDAO.contarPorEnquete(idEnquete) <= 2) {
                response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&erro=minimo");
                return;
            }
            opcaoDAO.excluir(idOpcao);
            response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&msg=excluida");
            return;
        }

        String descricao = request.getParameter("descricaoOpcao");
        descricao = descricao == null ? "" : descricao.trim();
        if (descricao.length() < 1) {
            response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&erro=dados");
            return;
        }

        if ("atualizar".equals(acao)) {
            Integer idOpcao = parseInt(request.getParameter("idOpcao"));
            if (idOpcao == null || !opcaoDAO.pertenceAEnquete(idOpcao, idEnquete)
                    || opcaoDAO.descricaoExiste(idEnquete, descricao, idOpcao)) {
                response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&erro=duplicada");
                return;
            }
            OpcaoRespostaModel opcao = new OpcaoRespostaModel();
            opcao.setIdOpcao(idOpcao);
            opcao.setDescricaoOpcao(descricao);
            opcao.setEnquete(enquete);
            opcaoDAO.atualizar(opcao);
            response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&msg=atualizada");
            return;
        }

        if (opcaoDAO.descricaoExiste(idEnquete, descricao, null)) {
            response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&erro=duplicada");
            return;
        }
        OpcaoRespostaModel opcao = new OpcaoRespostaModel();
        opcao.setDescricaoOpcao(descricao);
        opcao.setEnquete(enquete);
        opcaoDAO.inserir(opcao);
        response.sendRedirect(request.getContextPath() + "/opcao?idEnquete=" + idEnquete + "&msg=adicionada");
    }

    private static boolean ehAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        UsuarioModel usuario = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        return usuario != null && usuario.getNivelAcesso() != null
                && usuario.getNivelAcesso().getIdNivelAcesso() == 2;
    }

    private static Integer parseInt(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }
}
