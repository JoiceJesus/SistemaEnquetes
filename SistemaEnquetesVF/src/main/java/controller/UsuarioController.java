package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.NivelAcessoDAO;
import dao.UsuarioDAO;
import model.NivelAcessoModel;
import model.UsuarioModel;

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
        String acao = request.getParameter("acao");
        HttpSession session = request.getSession(false);

        if ("logout".equals(acao)) {
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/inicio?logout=sucesso");
            return;
        }

        if ("perfil".equals(acao)) {
            UsuarioModel usuarioSessao = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
            if (usuarioSessao == null) {
                response.sendRedirect(request.getContextPath() + "/inicio?login=necessario");
                return;
            }
            UsuarioModel perfil = usuarioDAO.buscarPorId(usuarioSessao.getIdUsuario());
            if (perfil == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/inicio?erro=usuarioInativo");
                return;
            }
            session.setAttribute("usuarioLogado", perfil);
            request.setAttribute("perfil", perfil);
            request.getRequestDispatcher("/WEB-INF/views/perfil.jsp").forward(request, response);
            return;
        }

        UsuarioModel logado = obterAdmin(session);
        if (logado == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (acao == null || "listar".equals(acao)) {
            carregarLista(request);
            request.getRequestDispatcher("/WEB-INF/views/usuario.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            Integer id = parseInt(request.getParameter("id"));
            if (id == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            UsuarioModel usuario = usuarioDAO.buscarPorId(id);
            if (usuario == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("usuarioEdicao", usuario);
            carregarLista(request);
            request.getRequestDispatcher("/WEB-INF/views/usuario.jsp").forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");

        if ("login".equals(acao)) {
            login(request, response);
            return;
        }

        if ("cadastro".equals(acao)) {
            cadastro(request, response);
            return;
        }

        if ("atualizarPerfil".equals(acao)) {
            atualizarPerfil(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        UsuarioModel logado = obterAdmin(session);
        if (logado == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("atualizar".equals(acao)) {
            atualizar(request, response, session, logado);
            return;
        }

        if ("alterarStatus".equals(acao)) {
            Integer id = parseInt(request.getParameter("idUsuario"));
            Integer status = parseInt(request.getParameter("status"));
            if (id == null || status == null || (status != 0 && status != 1)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (id == logado.getIdUsuario() && status == 0) {
                response.sendRedirect(request.getContextPath() + "/usuario?acao=listar&erro=autoDesativacao");
                return;
            }
            usuarioDAO.atualizarStatus(id, status);
            response.sendRedirect(request.getContextPath() + "/usuario?acao=listar&msg=statusAtualizado");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = limpar(request.getParameter("email"));
        String senha = request.getParameter("senha");
        if (email.isEmpty() || senha == null || senha.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/inicio?erro=login");
            return;
        }

        UsuarioModel usuario = usuarioDAO.autenticar(email, senha);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/inicio?erro=login");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioLogado", usuario);
        session.setMaxInactiveInterval(30 * 60);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private void cadastro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = limpar(request.getParameter("nome"));
        String email = limpar(request.getParameter("email"));
        String senha = request.getParameter("senha");
        String tipoPerfil = limpar(request.getParameter("tipo"));

        if (nome.length() < 2 || email.isEmpty() || senha == null || senha.length() < 4) {
            response.sendRedirect(request.getContextPath() + "/inicio?erro=cadastroInvalido");
            return;
        }
        if (usuarioDAO.emailExiste(email, null)) {
            response.sendRedirect(request.getContextPath() + "/inicio?erro=emailExiste");
            return;
        }

        int idNivel = ("admin".equalsIgnoreCase(tipoPerfil) || "administrador".equalsIgnoreCase(tipoPerfil))
                ? NIVEL_ADMIN : NIVEL_COMUM;

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setStatus(1);
        NivelAcessoModel nivel = new NivelAcessoModel();
        nivel.setIdNivelAcesso(idNivel);
        usuario.setNivelAcesso(nivel);

        if (usuarioDAO.inserir(usuario)) {
            response.sendRedirect(request.getContextPath() + "/inicio?cadastro=sucesso");
        } else {
            response.sendRedirect(request.getContextPath() + "/inicio?erro=cadastro");
        }
    }

    private void atualizar(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, UsuarioModel logado) throws IOException {
        Integer id = parseInt(request.getParameter("idUsuario"));
        Integer status = parseInt(request.getParameter("status"));
        Integer idNivel = parseInt(request.getParameter("idNivel"));
        if (id == null || status == null || idNivel == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UsuarioModel existente = usuarioDAO.buscarPorId(id);
        if (existente == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nome = limpar(request.getParameter("nome"));
        String email = limpar(request.getParameter("email"));
        if (nome.length() < 2 || email.isEmpty() || usuarioDAO.emailExiste(email, id)) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=editar&id=" + id + "&erro=dados");
            return;
        }
        if (id == logado.getIdUsuario() && status == 0) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=editar&id=" + id + "&erro=autoDesativacao");
            return;
        }

        existente.setNome(nome);
        existente.setEmail(email);
        String senha = request.getParameter("senha");
        if (senha != null && !senha.isBlank()) {
            existente.setSenha(senha);
        }
        existente.setStatus(status == 1 ? 1 : 0);
        NivelAcessoModel nivel = new NivelAcessoModel();
        nivel.setIdNivelAcesso(idNivel == NIVEL_ADMIN ? NIVEL_ADMIN : NIVEL_COMUM);
        existente.setNivelAcesso(nivel);

        if (!usuarioDAO.atualizar(existente)) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=editar&id=" + id + "&erro=salvar");
            return;
        }

        if (id == logado.getIdUsuario()) {
            UsuarioModel atualizado = usuarioDAO.buscarPorId(id);
            session.setAttribute("usuarioLogado", atualizado);
            if (atualizado.getNivelAcesso().getIdNivelAcesso() != NIVEL_ADMIN) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/usuario?acao=listar&msg=usuarioAtualizado");
    }

    private void atualizarPerfil(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        UsuarioModel usuarioSessao = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuarioSessao == null) {
            response.sendRedirect(request.getContextPath() + "/inicio?login=necessario");
            return;
        }
        UsuarioModel existente = usuarioDAO.buscarPorId(usuarioSessao.getIdUsuario());
        if (existente == null || existente.getStatus() != 1) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/inicio?erro=usuarioInativo");
            return;
        }
        String nome = limpar(request.getParameter("nome"));
        String email = limpar(request.getParameter("email"));
        if (nome.length() < 2 || email.isEmpty() || usuarioDAO.emailExiste(email, existente.getIdUsuario())) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=perfil&erro=dados");
            return;
        }
        existente.setNome(nome);
        existente.setEmail(email);
        String senha = request.getParameter("senha");
        if (senha != null && !senha.isBlank()) {
            if (senha.length() < 4) {
                response.sendRedirect(request.getContextPath() + "/usuario?acao=perfil&erro=senha");
                return;
            }
            existente.setSenha(senha);
        }
        if (!usuarioDAO.atualizar(existente)) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=perfil&erro=salvar");
            return;
        }
        UsuarioModel atualizado = usuarioDAO.buscarPorId(existente.getIdUsuario());
        session.setAttribute("usuarioLogado", atualizado);
        response.sendRedirect(request.getContextPath() + "/usuario?acao=perfil&msg=salvo");
    }

    private void carregarLista(HttpServletRequest request) {
        request.setAttribute("usuarios", usuarioDAO.listarTodos());
        request.setAttribute("niveis", nivelDAO.listarTodos());
    }

    private static UsuarioModel obterAdmin(HttpSession session) {
        UsuarioModel usuario = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (usuario == null || usuario.getNivelAcesso() == null
                || usuario.getNivelAcesso().getIdNivelAcesso() != NIVEL_ADMIN) {
            return null;
        }
        return usuario;
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static Integer parseInt(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }
}
