<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,java.time.format.DateTimeFormatter,model.*,dao.*" %>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");
UsuarioModel usuario = (UsuarioModel) session.getAttribute("usuarioLogado");
if (usuario == null) { response.sendRedirect(request.getContextPath() + "/index.jsp"); return; }
if (usuario.getNivelAcesso() == null || usuario.getNivelAcesso().getIdNivelAcesso() != 2) { response.sendRedirect(request.getContextPath() + "/usuario_comum.jsp"); return; }

EnqueteDAO enqueteDAO = new EnqueteDAO();
VotoDAO votoDAO = new VotoDAO();
UsuarioDAO usuarioDAO = new UsuarioDAO();
enqueteDAO.atualizarEnquetesExpiradas();
List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
int totalVotos = votoDAO.contarTodosVotos();
int enquetesAtivas = enqueteDAO.contarPorStatus("EM_CURSO");
int enquetesCriadas = 0;
for (EnqueteModel e : enqueteDAO.listarTodos()) {
    if (e.getUsuario() != null && e.getUsuario().getIdUsuario() == usuario.getIdUsuario()) {
        enquetesCriadas++;
    }
}
int totalUsuarios = usuarioDAO.listarTodos().size();
String categoriaMaisVotada = votoDAO.buscarCategoriaMaisVotada();
DateTimeFormatter dataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Painel Administrativo</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-100 text-slate-900 min-h-screen">
<header class="bg-white border-b sticky top-0 z-10">
    <div class="max-w-7xl mx-auto px-5 py-4 flex flex-wrap items-center justify-between gap-4">
        <div><h1 class="text-xl font-bold">Painel Administrativo</h1><p class="text-sm text-slate-500"><%=esc(usuario.getNome())%> • <%=esc(usuario.getEmail())%></p></div>
        <nav class="flex flex-wrap items-center gap-3 text-sm">
            <a class="text-indigo-700" href="<%=request.getContextPath()%>/dashboard">Início</a>
            <a class="text-indigo-700" href="<%=request.getContextPath()%>/enquete?acao=listar">Enquetes</a>
            <a class="text-indigo-700" href="<%=request.getContextPath()%>/categoria?acao=listar">Categorias</a>
            <a class="text-indigo-700" href="<%=request.getContextPath()%>/usuario?acao=listar">Usuários</a>
            <a class="text-red-600" href="<%=request.getContextPath()%>/usuario?acao=logout">Sair</a>
        </nav>
    </div>
</header>

<main class="max-w-7xl mx-auto p-5 md:p-7">
    <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
        <div><h2 class="text-3xl font-bold">Olá, <%=esc(usuario.getNome())%></h2><p class="text-slate-500">Visão real do banco de dados do sistema.</p></div>
        <a href="<%=request.getContextPath()%>/enquete?acao=novo" class="bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-3 rounded-lg font-semibold">+ Criar enquete</a>
    </div>

    <div class="grid sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Total de votos</p><p class="text-3xl font-bold"><%=totalVotos%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Enquetes ativas</p><p class="text-3xl font-bold"><%=enquetesAtivas%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Criadas por você</p><p class="text-3xl font-bold"><%=enquetesCriadas%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Usuários cadastrados</p><p class="text-3xl font-bold"><%=totalUsuarios%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Categoria mais votada</p><p class="text-lg font-bold"><%=esc(categoriaMaisVotada)%></p></div>
    </div>

    <section class="bg-white rounded-xl border overflow-hidden">
        <div class="p-5 border-b flex flex-wrap justify-between items-center gap-3">
            <div><h2 class="text-xl font-bold">Gerenciar enquetes</h2><p class="text-sm text-slate-500">Todas as enquetes abaixo vêm da tabela <code>enquete</code>.</p></div>
            <a href="<%=request.getContextPath()%>/enquete?acao=listar" class="text-indigo-700 font-medium">Abrir tela completa →</a>
        </div>
        <% if (enquetes.isEmpty()) { %>
            <div class="p-8 text-center text-slate-500">Nenhuma enquete cadastrada. Use “Criar enquete” para cadastrar a primeira.</div>
        <% } else { %>
        <div class="overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-slate-50"><tr><th class="text-left p-3">Título</th><th class="text-left p-3">Categoria</th><th class="text-left p-3">Status</th><th class="text-left p-3">Votos</th><th class="text-left p-3">Expiração</th><th class="text-left p-3">Criador</th><th class="text-left p-3">Ações</th></tr></thead>
                <tbody>
                <% for (EnqueteModel e : enquetes) { int votos = votoDAO.contarVotosEnquete(e.getIdEnquete()); %>
                    <tr class="border-t align-top">
                        <td class="p-3"><div class="font-semibold"><%=esc(e.getTitulo())%></div><div class="text-xs text-slate-500 mt-1 max-w-xs"><%=esc(e.getDescricao())%></div></td>
                        <td class="p-3"><%=esc(e.getCategoria() == null ? "-" : e.getCategoria().getNomeCategoria())%></td>
                        <td class="p-3"><span class="px-2 py-1 rounded-full text-xs <%= "EM_CURSO".equals(e.getStatus()) ? "bg-emerald-50 text-emerald-700" : "bg-slate-100 text-slate-600" %>"><%= "EM_CURSO".equals(e.getStatus()) ? "Em curso" : "Encerrada" %></span></td>
                        <td class="p-3"><%=votos%></td>
                        <td class="p-3 whitespace-nowrap"><%=e.getDataExpiracao() == null ? "-" : e.getDataExpiracao().format(dataHora)%></td>
                        <td class="p-3"><%=esc(e.getUsuario() == null ? "-" : e.getUsuario().getNome())%></td>
                        <td class="p-3 whitespace-nowrap">
                            <a class="text-indigo-700 mr-3" href="<%=request.getContextPath()%>/enquete?acao=editar&id=<%=e.getIdEnquete()%>">Editar</a>
                            <a class="text-slate-700 mr-3" href="<%=request.getContextPath()%>/opcao?acao=listar&idEnquete=<%=e.getIdEnquete()%>">Opções</a>
                            <a class="text-red-600" href="<%=request.getContextPath()%>/enquete?acao=excluir&id=<%=e.getIdEnquete()%>" onclick="return confirm('Excluir esta enquete e os votos vinculados a ela?')">Excluir</a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </section>
</main>
</body>
</html>
