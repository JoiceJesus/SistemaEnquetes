package controller;

import java.io.IOException;
import java.util.List;

import dao.NivelAcessoDAO;
import dao.UsuarioDAO;
import model.NivelAcessoModel;
import model.UsuarioModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/usuario")
public class UsuarioController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int NIVEL_COMUM = 1;
    private static final int NIVEL_ADMIN = 2;

    private UsuarioDAO usuarioDAO;
    private NivelAcessoDAO nivelDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
        nivelDAO = new NivelAcessoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null
                : (UsuarioModel) session.getAttribute("usuarioLogado");

        String acao = request.getParameter("acao");
        if ("logout".equals(acao)) {
            if (session != null) session.invalidate();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if (logado == null || logado.getNivelAcesso() == null || logado.getNivelAcesso().getIdNivelAcesso() != NIVEL_ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (acao == null || "listar".equals(acao)) {
            request.setAttribute("usuarios", usuarioDAO.listarTodos());
            request.setAttribute("niveis", nivelDAO.listarTodos());
            request.getRequestDispatcher("/usuario.jsp").forward(request, response);
        } else if ("editar".equals(acao)) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("usuario", usuarioDAO.buscarPorId(id));
            request.setAttribute("niveis", nivelDAO.listarTodos());
            request.getRequestDispatcher("/usuario.jsp").forward(request, response);
        } else if ("excluir".equals(acao)) {
            int id = Integer.parseInt(request.getParameter("id"));
            usuarioDAO.excluir(id);
            response.sendRedirect(request.getContextPath() + "/usuario?acao=listar");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        if ("login".equals(acao)) {
            String email = request.getParameter("email");
            String senha = request.getParameter("senha");
            UsuarioModel usuario = usuarioDAO.autenticar(email, senha);

            if (usuario == null) {
                request.setAttribute("erro", "E-mail ou senha inválidos.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            request.getSession(true).setAttribute("usuarioLogado", usuario);
            int nivel = usuario.getNivelAcesso().getIdNivelAcesso();
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        if ("cadastro".equals(acao)) {
            UsuarioModel usuario = new UsuarioModel();
            usuario.setNome(request.getParameter("nome"));
            usuario.setEmail(request.getParameter("email"));
            usuario.setSenha(request.getParameter("senha"));
            usuario.setStatus(1);

            String tipoPerfil = request.getParameter("tipo");
            int idNivel = ("admin".equalsIgnoreCase(tipoPerfil) || "administrador".equalsIgnoreCase(tipoPerfil)) ? NIVEL_ADMIN : NIVEL_COMUM;

            NivelAcessoModel nivel = new NivelAcessoModel();
            nivel.setIdNivelAcesso(idNivel);
            usuario.setNivelAcesso(nivel);

            if (usuarioDAO.inserir(usuario)) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?cadastro=sucesso");
            } else {
                request.setAttribute("erro", "Não foi possível cadastrar o usuário.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
            return;
        }

        if ("atualizar".equals(acao)) {
            HttpSession session = request.getSession(false);
            UsuarioModel logado = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
            if (logado == null || logado.getNivelAcesso() == null || logado.getNivelAcesso().getIdNivelAcesso() != NIVEL_ADMIN) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            UsuarioModel existente = usuarioDAO.buscarPorId(Integer.parseInt(request.getParameter("idUsuario")));
            if (existente == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            existente.setNome(request.getParameter("nome"));
            existente.setEmail(request.getParameter("email"));
            String senha = request.getParameter("senha");
            if (senha != null && !senha.isBlank()) existente.setSenha(senha);
            existente.setStatus(Integer.parseInt(request.getParameter("status")));
            NivelAcessoModel nivel = new NivelAcessoModel();
            nivel.setIdNivelAcesso(Integer.parseInt(request.getParameter("idNivel")));
            existente.setNivelAcesso(nivel);
            usuarioDAO.atualizar(existente);
            if (existente.getIdUsuario() == logado.getIdUsuario()) {
                UsuarioModel atualizado = usuarioDAO.buscarPorId(existente.getIdUsuario());
                session.setAttribute("usuarioLogado", atualizado);
            }
            response.sendRedirect(request.getContextPath() + "/usuario?acao=listar");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}
