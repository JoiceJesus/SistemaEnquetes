package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoriaDAO;
import dao.EnqueteDAO;
import dao.OpcaoRespostaDAO;
import model.CategoriaModel;
import model.EnqueteModel;
import model.OpcaoRespostaModel;
import model.UsuarioModel;

@WebServlet("/enquete")
public class EnqueteController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private EnqueteDAO enqueteDAO;
    private CategoriaDAO categoriaDAO;
    private OpcaoRespostaDAO opcaoDAO;

    @Override
    public void init() {
        enqueteDAO = new EnqueteDAO();
        categoriaDAO = new CategoriaDAO();
        opcaoDAO = new OpcaoRespostaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null
                : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        enqueteDAO.atualizarEnquetesExpiradas();
        String acao = request.getParameter("acao");

        if (acao == null || "listar".equals(acao)) {
            List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
            Map<Integer, List<OpcaoRespostaModel>> opcoes = new HashMap<>();
            for (EnqueteModel enquete : enquetes) {
                opcoes.put(enquete.getIdEnquete(), opcaoDAO.listarPorEnquete(enquete.getIdEnquete()));
            }
            request.setAttribute("enquetes", enquetes);
            request.setAttribute("opcoesPorEnquete", opcoes);
            request.getRequestDispatcher("/enquete.jsp").forward(request, response);
            return;
        }

        if ("novo".equals(acao)) {
            exigirAdmin(logado, response);
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            exigirAdmin(logado, response);
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("enquete", enqueteDAO.buscarPorId(id));
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }

        if ("excluir".equals(acao)) {
            exigirAdmin(logado, response);
            int id = Integer.parseInt(request.getParameter("id"));
            enqueteDAO.excluir(id);
            response.sendRedirect(request.getContextPath() + "/enquete?acao=listar");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null
                : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");
        EnqueteModel enquete = new EnqueteModel();
        boolean atualizar = "atualizar".equals(acao);
        if (atualizar) {
            int id = Integer.parseInt(request.getParameter("idEnquete"));
            EnqueteModel existente = enqueteDAO.buscarPorId(id);
            if (existente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            enquete.setIdEnquete(id);
            enquete.setStatus(existente.getStatus());
        } else {
            enquete.setStatus("EM_CURSO");
        }
        enquete.setTitulo(request.getParameter("titulo"));
        enquete.setDescricao(request.getParameter("descricao"));
        enquete.setTipoVotacao(request.getParameter("tipoVotacao"));
        enquete.setLimiteVotosIp(parseInt(request.getParameter("limiteVotosIp"), 0));
        enquete.setLimiteQuantidadeVotos(parseInt(request.getParameter("limiteQuantidadeVotos"), 0));
        String expiracao = request.getParameter("dataExpiracao");
        if (expiracao == null || expiracao.isBlank()) {
            request.setAttribute("erro", "Informe a data e hora de expiração.");
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }
        enquete.setDataExpiracao(LocalDateTime.parse(expiracao));

        if (enquete.getDataExpiracao().isBefore(LocalDateTime.now())) {
            request.setAttribute("erro", "A expiração deve ser futura.");
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setIdUsuario(logado.getIdUsuario());
        enquete.setUsuario(usuario);

        CategoriaModel categoria = new CategoriaModel();
        categoria.setIdCategoria(Integer.parseInt(request.getParameter("idCategoria")));
        enquete.setCategoria(categoria);

        if (atualizar) {
            enqueteDAO.atualizar(enquete);
        } else {
            int idEnquete = enqueteDAO.inserir(enquete);
            String[] opcoes = request.getParameterValues("opcao");
            if (idEnquete > 0 && opcoes != null) {
                for (String texto : opcoes) {
                    if (texto != null && !texto.isBlank()) {
                        OpcaoRespostaModel opcao = new OpcaoRespostaModel();
                        opcao.setDescricaoOpcao(texto.trim());
                        EnqueteModel e = new EnqueteModel();
                        e.setIdEnquete(idEnquete);
                        opcao.setEnquete(e);
                        opcaoDAO.inserir(opcao);
                    }
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/enquete?acao=listar");
    }

    private static void exigirAdmin(UsuarioModel usuario, HttpServletResponse response) throws IOException {
        if (usuario.getNivelAcesso() == null || usuario.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception e) { return fallback; }
    }
}
