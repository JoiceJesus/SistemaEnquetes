package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import util.DataHoraUtil;

@WebServlet("/enquete")
public class EnqueteController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int NIVEL_ADMIN = 2;

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
        UsuarioModel logado = usuarioLogado(request);
        if (logado == null) {
            response.sendRedirect(request.getContextPath() + "/inicio?login=necessario");
            return;
        }

        enqueteDAO.atualizarEnquetesExpiradas();
        String acao = request.getParameter("acao");

        if (acao == null || "listar".equals(acao)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        if ("novo".equals(acao)) {
            if (!ehAdmin(logado)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.getRequestDispatcher("/WEB-INF/views/enquete-form.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            if (!ehAdmin(logado)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Integer id = parseInt(request.getParameter("id"));
            EnqueteModel enquete = id == null ? null : enqueteDAO.buscarPorId(id);
            if (enquete == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("enquete", enquete);
            request.setAttribute("categorias", categoriaDAO.listarTodos());
            request.setAttribute("opcoesExistentes", opcaoDAO.listarPorEnquete(id));
            request.setAttribute("dataExpiracaoForm", DataHoraUtil.formatarDataInput(enquete.getDataExpiracao()));
            request.setAttribute("horaExpiracaoForm", DataHoraUtil.formatarHoraInput(enquete.getDataExpiracao()));
            request.getRequestDispatcher("/WEB-INF/views/enquete-form.jsp").forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UsuarioModel logado = usuarioLogado(request);
        if (!ehAdmin(logado)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao = request.getParameter("acao");
        if ("salvar".equals(acao) || "atualizar".equals(acao)) {
            salvar(request, response, logado, "atualizar".equals(acao));
            return;
        }

        Integer id = parseInt(request.getParameter("idEnquete"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if ("excluir".equals(acao)) {
            if (enqueteDAO.excluir(id)) {
                response.sendRedirect(request.getContextPath() + "/dashboard?msg=enqueteExcluida");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?erro=enqueteExcluir");
            }
            return;
        }

        if ("alterarStatus".equals(acao)) {
            String status = request.getParameter("status");
            if (!"EM_CURSO".equals(status) && !"ENCERRADA".equals(status)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            enqueteDAO.atualizarStatus(id, status);
            response.sendRedirect(request.getContextPath() + "/dashboard?msg=statusEnquete");
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response, UsuarioModel logado, boolean atualizar)
            throws ServletException, IOException {
        EnqueteModel existente = null;
        if (atualizar) {
            Integer id = parseInt(request.getParameter("idEnquete"));
            existente = id == null ? null : enqueteDAO.buscarPorId(id);
            if (existente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        EnqueteModel enquete = new EnqueteModel();
        enquete.setIdEnquete(existente == null ? 0 : existente.getIdEnquete());
        LinkedHashSet<String> opcoesRecebidas = atualizar ? null : normalizarOpcoes(request.getParameterValues("opcao"));
        if (!atualizar) {
            request.setAttribute("opcoesForm", new ArrayList<>(opcoesRecebidas));
        }
        enquete.setTitulo(limpar(request.getParameter("titulo")));
        enquete.setDescricao(limpar(request.getParameter("descricao")));
        enquete.setTipoVotacao(normalizarTipo(request.getParameter("tipoVotacao")));
        enquete.setLimiteVotosIp(parseIntOuZero(request.getParameter("limiteVotosIp")));
        enquete.setLimiteQuantidadeVotos(parseIntOuZero(request.getParameter("limiteQuantidadeVotos")));

        String data = limpar(request.getParameter("dataExpiracao"));
        String hora = limpar(request.getParameter("horaExpiracao"));
        request.setAttribute("dataExpiracaoForm", data);
        request.setAttribute("horaExpiracaoForm", hora);

        try {
            enquete.setDataExpiracao(DataHoraUtil.parseDataHora(data, hora));
        } catch (DateTimeParseException e) {
            reexibirFormulario(request, response, enquete, existente, "Informe uma data válida no formato dd/mm/aaaa e uma hora válida.");
            return;
        }

        String status = atualizar ? request.getParameter("status") : "EM_CURSO";
        if (!"EM_CURSO".equals(status) && !"ENCERRADA".equals(status)) {
            status = "EM_CURSO";
        }
        enquete.setStatus(status);

        Integer idCategoria = parseInt(request.getParameter("idCategoria"));
        CategoriaModel categoria = idCategoria == null ? null : categoriaDAO.buscarPorId(idCategoria);
        if (categoria == null) {
            reexibirFormulario(request, response, enquete, existente, "Selecione uma categoria válida.");
            return;
        }
        enquete.setCategoria(categoria);

        if (enquete.getTitulo().length() < 3 || enquete.getDescricao().length() < 3) {
            reexibirFormulario(request, response, enquete, existente, "Preencha título e descrição da enquete.");
            return;
        }
        if (enquete.getDataExpiracao().isBefore(DataHoraUtil.agora()) && "EM_CURSO".equals(enquete.getStatus())) {
            reexibirFormulario(request, response, enquete, existente, "Uma enquete em curso precisa ter data de expiração futura.");
            return;
        }

        if (atualizar) {
            // Preserva o criador original da enquete; editar não transfere autoria.
            enquete.setUsuario(existente.getUsuario());
            if (!enqueteDAO.atualizar(enquete)) {
                reexibirFormulario(request, response, enquete, existente, "Não foi possível atualizar a enquete.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/dashboard?msg=enqueteAtualizada");
            return;
        }

        LinkedHashSet<String> opcoesValidas = opcoesRecebidas;
        if (opcoesValidas.size() < 2) {
            reexibirFormulario(request, response, enquete, null, "Informe pelo menos duas opções de resposta diferentes.");
            return;
        }

        UsuarioModel criador = new UsuarioModel();
        criador.setIdUsuario(logado.getIdUsuario());
        enquete.setUsuario(criador);
        int idEnquete = enqueteDAO.inserir(enquete);
        if (idEnquete <= 0) {
            reexibirFormulario(request, response, enquete, null, "Não foi possível criar a enquete.");
            return;
        }

        for (String texto : opcoesValidas) {
            OpcaoRespostaModel opcao = new OpcaoRespostaModel();
            opcao.setDescricaoOpcao(texto);
            EnqueteModel ref = new EnqueteModel();
            ref.setIdEnquete(idEnquete);
            opcao.setEnquete(ref);
            opcaoDAO.inserir(opcao);
        }

        response.sendRedirect(request.getContextPath() + "/dashboard?msg=enqueteCriada");
    }

    private void carregarCatalogo(HttpServletRequest request, UsuarioModel usuario) {
        List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
        Map<Integer, List<OpcaoRespostaModel>> opcoes = new HashMap<>();
        Map<Integer, Integer> totais = new HashMap<>();
        Map<Integer, Integer> participantes = new HashMap<>();
        Map<Integer, Integer> votosOpcao = new HashMap<>();
        for (EnqueteModel e : enquetes) {
            List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
            opcoes.put(e.getIdEnquete(), lista);
            totais.put(e.getIdEnquete(), votoDAO.contarVotosEnquete(e.getIdEnquete()));
            participantes.put(e.getIdEnquete(), votoDAO.contarParticipantesEnquete(e.getIdEnquete()));
            for (OpcaoRespostaModel o : lista) {
                votosOpcao.put(o.getIdOpcao(), votoDAO.contarVotosPorOpcao(o.getIdOpcao()));
            }
        }
        Set<Integer> votadas = new HashSet<>(votoDAO.listarIdsEnquetesVotadasPorUsuario(usuario.getIdUsuario()));
        request.setAttribute("enquetes", enquetes);
        request.setAttribute("categorias", categoriaDAO.listarTodos());
        request.setAttribute("opcoesPorEnquete", opcoes);
        request.setAttribute("totalPorEnquete", totais);
        request.setAttribute("participantesPorEnquete", participantes);
        request.setAttribute("votosPorOpcao", votosOpcao);
        request.setAttribute("enquetesVotadas", votadas);
        request.setAttribute("opcoesVotadas", new HashSet<>(votoDAO.listarIdsOpcoesVotadasPorUsuario(usuario.getIdUsuario())));
    }

    private void reexibirFormulario(HttpServletRequest request, HttpServletResponse response,
            EnqueteModel enquete, EnqueteModel existente, String erro) throws ServletException, IOException {
        request.setAttribute("erroFormulario", erro);
        request.setAttribute("enquete", enquete);
        request.setAttribute("categorias", categoriaDAO.listarTodos());
        if (existente != null) {
            request.setAttribute("opcoesExistentes", opcaoDAO.listarPorEnquete(existente.getIdEnquete()));
        }
        request.getRequestDispatcher("/WEB-INF/views/enquete-form.jsp").forward(request, response);
    }

    private static LinkedHashSet<String> normalizarOpcoes(String[] opcoes) {
        LinkedHashSet<String> validas = new LinkedHashSet<>();
        if (opcoes != null) {
            for (String texto : opcoes) {
                String limpo = limpar(texto);
                if (!limpo.isEmpty()) {
                    validas.add(limpo);
                }
            }
        }
        return validas;
    }

    private static String normalizarTipo(String tipo) {
        return "MULTIPLA".equals(tipo) ? "MULTIPLA" : "UNICA";
    }

    private static UsuarioModel usuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
    }

    private static boolean ehAdmin(UsuarioModel usuario) {
        return usuario != null && usuario.getNivelAcesso() != null
                && usuario.getNivelAcesso().getIdNivelAcesso() == NIVEL_ADMIN;
    }

    private static int parseIntOuZero(String valor) {
        Integer parsed = parseInt(valor);
        return parsed == null || parsed < 0 ? 0 : parsed;
    }

    private static Integer parseInt(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
