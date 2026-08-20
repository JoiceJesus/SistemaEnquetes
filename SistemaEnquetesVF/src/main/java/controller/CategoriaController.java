package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoriaDAO;
import model.CategoriaModel;
import model.UsuarioModel;

@WebServlet("/categoria")
public class CategoriaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CategoriaDAO dao;

    @Override
    public void init() {
        dao = new CategoriaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ehAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");
        if (acao == null || "listar".equals(acao)) {
            request.setAttribute("categorias", dao.listarTodos());
            request.getRequestDispatcher("/WEB-INF/views/categoria.jsp").forward(request, response);
            return;
        }

        if ("novo".equals(acao)) {
            request.getRequestDispatcher("/WEB-INF/views/categoria-form.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            Integer id = parseInt(request.getParameter("id"));
            CategoriaModel categoria = id == null ? null : dao.buscarPorId(id);
            if (categoria == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("categoria", categoria);
            request.getRequestDispatcher("/WEB-INF/views/categoria-form.jsp").forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ehAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");
        if ("excluir".equals(acao)) {
            Integer id = parseInt(request.getParameter("idCategoria"));
            if (id == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            boolean ok = dao.excluir(id);
            response.sendRedirect(request.getContextPath() + "/categoria?acao=listar&" + (ok ? "msg=excluida" : "erro=emUso"));
            return;
        }

        String nome = request.getParameter("nomeCategoria");
        nome = nome == null ? "" : nome.trim();
        Integer id = parseInt(request.getParameter("idCategoria"));
        if (nome.length() < 2 || dao.nomeExiste(nome, id)) {
            String destino = id == null ? "/categoria?acao=novo&erro=dados" : "/categoria?acao=editar&id=" + id + "&erro=dados";
            response.sendRedirect(request.getContextPath() + destino);
            return;
        }

        CategoriaModel categoria = new CategoriaModel();
        categoria.setNomeCategoria(nome);
        boolean ok;
        if ("atualizar".equals(acao) && id != null) {
            categoria.setIdCategoria(id);
            ok = dao.atualizar(categoria);
        } else {
            ok = dao.inserir(categoria);
        }
        response.sendRedirect(request.getContextPath() + "/categoria?acao=listar&" + (ok ? "msg=salva" : "erro=salvar"));
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
