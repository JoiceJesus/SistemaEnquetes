package controller;

import java.io.IOException;
import java.util.List;

import dao.CategoriaDAO;
import model.CategoriaModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");

        if (acao == null || acao.equals("listar")) {

            List<CategoriaModel> lista =
                dao.listarTodos();

            request.setAttribute("categorias", lista);

            request.getRequestDispatcher(
                "/categoria.jsp"
            ).forward(request, response);

        } else if (acao.equals("novo")) {

            request.getRequestDispatcher("/categoria-form.jsp").forward(request, response);

        } else if (acao.equals("editar")) {

            int id = Integer.parseInt(
                request.getParameter("id")
            );

            CategoriaModel categoria =
                dao.buscarPorId(id);

            request.setAttribute(
                "categoria",
                categoria
            );

            request.getRequestDispatcher(
                "/categoria-form.jsp"
            ).forward(request, response);

        } else if (acao.equals("excluir")) {

            int id = Integer.parseInt(
                request.getParameter("id")
            );

            dao.excluir(id);

            response.sendRedirect(
                request.getContextPath()
                + "/categoria?acao=listar"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");

        CategoriaModel categoria =
            new CategoriaModel();

        categoria.setNomeCategoria(
            request.getParameter("nomeCategoria")
        );

        if ("atualizar".equals(acao)) {

            categoria.setIdCategoria(
                Integer.parseInt(
                    request.getParameter("idCategoria")
                )
            );

            dao.atualizar(categoria);

        } else {

            dao.inserir(categoria);
        }

        response.sendRedirect(
            request.getContextPath()
            + "/categoria?acao=listar"
        );
    }
}