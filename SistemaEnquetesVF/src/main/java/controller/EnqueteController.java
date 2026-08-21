package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
            if (!ehAdmin(logado)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            if (!ehAdmin(logado)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("enquete", enqueteDAO.buscarPorId(id));
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.setAttribute("opcoesExistentes", opcaoDAO.listarPorEnquete(id));
            request.getRequestDispatcher("/enquete-form.jsp").forward(request, response);
            return;
        }

        if ("excluir".equals(acao)) {
            if (!ehAdmin(logado)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            int id = Integer.parseInt(request.getParameter("id"));
            enqueteDAO.excluir(id);
            response.sendRedirect(request.getContextPath() + "/dashboard");
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

        if (!ehAdmin(logado)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");
        boolean atualizar = "atualizar".equals(acao);

        if (!atualizar && !"inserir".equals(acao)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String titulo = limpar(request.getParameter("titulo"));
        String descricao = limpar(request.getParameter("descricao"));
        String tipoVotacao = limpar(request.getParameter("tipoVotacao"));

        if (titulo.isEmpty() || descricao.isEmpty()
                || (!"UNICA".equals(tipoVotacao) && !"MULTIPLA".equals(tipoVotacao))) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=dadosInvalidos");
            return;
        }

        int idCategoria = parseInt(request.getParameter("idCategoria"), 0);
        CategoriaModel categoria = idCategoria > 0
                ? categoriaDAO.buscarPorId(idCategoria)
                : null;

        if (categoria == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=categoriaInvalida");
            return;
        }

        String expiracao = limpar(request.getParameter("dataExpiracao"));
        if (expiracao.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=dataObrigatoria");
            return;
        }

        LocalDate dataExpiracao;
        try {
            DateTimeFormatter br = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
            dataExpiracao = LocalDate.parse(expiracao, br);
        } catch (DateTimeParseException ex) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=dataInvalida");
            return;
        }

        if (dataExpiracao.isBefore(LocalDate.now())) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=dataPassada");
            return;
        }

        EnqueteModel enquete = new EnqueteModel();
        enquete.setTitulo(titulo);
        enquete.setDescricao(descricao);
        enquete.setTipoVotacao(tipoVotacao);
        enquete.setLimiteVotosIp(parseInt(request.getParameter("limiteVotosIp"), 0));
        enquete.setLimiteQuantidadeVotos(
                parseInt(request.getParameter("limiteQuantidadeVotos"), 0));
        enquete.setDataExpiracao(dataExpiracao.atTime(23, 59, 59));
        enquete.setCategoria(categoria);

        if (atualizar) {
            int id = parseInt(request.getParameter("idEnquete"), 0);
            EnqueteModel existente = id > 0 ? enqueteDAO.buscarPorId(id) : null;

            if (existente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            enquete.setIdEnquete(id);
            enquete.setStatus(existente.getStatus());
            enquete.setUsuario(existente.getUsuario());

            if (!enqueteDAO.atualizar(enquete)) {
                response.sendRedirect(request.getContextPath() + "/dashboard?erro=falhaAtualizacao");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/dashboard?sucesso=enquete");
            return;
        }

        enquete.setStatus("EM_CURSO");
        enquete.setUsuario(logado);

        String[] recebidas = request.getParameterValues("opcao");
        java.util.LinkedHashSet<String> opcoesValidas =
                new java.util.LinkedHashSet<>();

        if (recebidas != null) {
            for (String texto : recebidas) {
                texto = limpar(texto);
                if (!texto.isEmpty()) {
                    opcoesValidas.add(texto);
                }
            }
        }

        if (opcoesValidas.size() < 2) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=poucasOpcoes");
            return;
        }

        int idCriado = enqueteDAO.inserirComOpcoes(enquete, opcoesValidas);

        if (idCriado <= 0) {
            response.sendRedirect(request.getContextPath() + "/dashboard?erro=falhaCriacao");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/dashboard?sucesso=enquete");
    }

    private static boolean ehAdmin(UsuarioModel usuario) {
        return usuario != null && usuario.getNivelAcesso() != null
                && usuario.getNivelAcesso().getIdNivelAcesso() == 2;
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception e) { return fallback; }
    }

    private static String limpar(String value) {
        return value == null ? "" : value.trim();
    }
}
