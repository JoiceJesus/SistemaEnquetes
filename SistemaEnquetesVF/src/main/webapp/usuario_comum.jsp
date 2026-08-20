<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,java.time.*,java.time.format.DateTimeFormatter,model.*,dao.*" %>
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
if (usuario.getNivelAcesso() != null && usuario.getNivelAcesso().getIdNivelAcesso() == 2) { response.sendRedirect(request.getContextPath() + "/usuario_adm.jsp"); return; }

EnqueteDAO enqueteDAO = new EnqueteDAO();
OpcaoRespostaDAO opcaoDAO = new OpcaoRespostaDAO();
VotoDAO votoDAO = new VotoDAO();
enqueteDAO.atualizarEnquetesExpiradas();
List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
Set<Integer> votadas = new HashSet<Integer>(votoDAO.listarIdsEnquetesVotadasPorUsuario(usuario.getIdUsuario()));
int totalVotos = votoDAO.contarTodosVotos();
int votosUsuario = votoDAO.contarVotosUsuario(usuario.getIdUsuario());
int enquetesAtivas = enqueteDAO.contarPorStatus("EM_CURSO");
String categoriaMaisVotada = votoDAO.buscarCategoriaMaisVotada();
DateTimeFormatter dataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Painel do Usuário</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-slate-100 text-slate-900 min-h-screen">
<header class="bg-white border-b">
    <div class="max-w-7xl mx-auto px-5 py-4 flex flex-wrap items-center justify-between gap-3">
        <div><h1 class="text-xl font-bold">Sistema de Enquetes</h1><p class="text-sm text-slate-500">Olá, <%=esc(usuario.getNome())%> • <%=esc(usuario.getEmail())%></p></div>
        <nav class="flex gap-3 text-sm"><a class="text-indigo-700" href="<%=request.getContextPath()%>/dashboard">Atualizar painel</a><a class="text-indigo-700" href="<%=request.getContextPath()%>/enquete?acao=listar">Todas as enquetes</a><a class="text-red-600" href="<%=request.getContextPath()%>/usuario?acao=logout">Sair</a></nav>
    </div>
</header>
<main class="max-w-7xl mx-auto p-5 md:p-7">
    <div class="mb-6"><h2 class="text-3xl font-bold">Bem-vindo, <%=esc(usuario.getNome())%></h2><p class="text-slate-500">Vote nas enquetes reais cadastradas no sistema e acompanhe os resultados.</p></div>
    <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Total de votos no sistema</p><p class="text-3xl font-bold"><%=totalVotos%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Seus votos</p><p class="text-3xl font-bold"><%=votosUsuario%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Enquetes ativas</p><p class="text-3xl font-bold"><%=enquetesAtivas%></p></div>
        <div class="bg-white rounded-xl border p-5"><p class="text-xs text-slate-500">Categoria mais votada</p><p class="text-lg font-bold"><%=esc(categoriaMaisVotada)%></p></div>
    </div>

    <div class="flex items-end justify-between gap-3 mb-4"><div><h2 class="text-2xl font-bold">Enquetes</h2><p class="text-sm text-slate-500">Nenhum conteúdo desta lista é placeholder.</p></div></div>
    <% if (enquetes.isEmpty()) { %>
        <div class="bg-white border border-dashed rounded-xl p-8 text-center text-slate-500">Nenhuma enquete cadastrada no banco.</div>
    <% } else { %>
    <div class="grid md:grid-cols-2 gap-5">
        <% for (EnqueteModel e : enquetes) {
            List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
            int total = votoDAO.contarVotosEnquete(e.getIdEnquete());
            boolean jaVotou = votadas.contains(e.getIdEnquete());
            boolean aberta = "EM_CURSO".equals(e.getStatus()) && e.getDataExpiracao() != null && e.getDataExpiracao().isAfter(LocalDateTime.now());
        %>
        <section class="bg-white rounded-xl border p-5">
            <div class="flex justify-between gap-3">
                <div><span class="text-xs font-semibold text-indigo-700"><%=esc(e.getCategoria() == null ? "Sem categoria" : e.getCategoria().getNomeCategoria())%></span><h3 class="text-xl font-bold mt-1"><%=esc(e.getTitulo())%></h3></div>
                <span class="text-xs px-2 py-1 rounded-full h-fit <%=aberta?"bg-emerald-50 text-emerald-700":"bg-slate-100 text-slate-600"%>"><%=aberta?"Em curso":"Encerrada"%></span>
            </div>
            <p class="text-sm text-slate-600 mt-3"><%=esc(e.getDescricao())%></p>
            <p class="text-xs text-slate-500 mt-2">Expira em: <strong><%=e.getDataExpiracao()==null?"-":e.getDataExpiracao().format(dataHora)%></strong></p>

            <% if (jaVotou || !aberta) { %>
                <div class="mt-4 space-y-3">
                    <% for (OpcaoRespostaModel o : lista) { int v = votoDAO.contarVotosPorOpcao(o.getIdOpcao()); int pct = total == 0 ? 0 : (int)Math.round(v * 100.0 / total); %>
                    <div><div class="flex justify-between text-sm gap-3"><span><%=esc(o.getDescricaoOpcao())%></span><span class="whitespace-nowrap"><%=v%> (<%=pct%>%)</span></div><div class="h-2 bg-slate-100 rounded-full mt-1"><div class="h-2 bg-indigo-500 rounded-full" style="width:<%=pct%>%"></div></div></div>
                    <% } %>
                </div>
                <p class="mt-4 text-xs text-slate-500">Total: <%=total%> voto(s)<%=jaVotou?" • Você já votou nesta enquete":""%>.</p>
            <% } else { %>
                <form method="post" action="<%=request.getContextPath()%>/voto" class="mt-4 space-y-2" onsubmit="return validarVoto(this)">
                    <input type="hidden" name="idEnquete" value="<%=e.getIdEnquete()%>">
                    <% for (OpcaoRespostaModel o : lista) { %>
                    <label class="block border rounded-lg p-3 hover:bg-slate-50 cursor-pointer"><input type="<%="MULTIPLA".equals(e.getTipoVotacao())?"checkbox":"radio"%>" name="idOpcao" value="<%=o.getIdOpcao()%>" class="mr-2"><%=esc(o.getDescricaoOpcao())%></label>
                    <% } %>
                    <button class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg">Registrar voto</button>
                </form>
            <% } %>
        </section>
        <% } %>
    </div>
    <% } %>
</main>
<script>
function validarVoto(form) {
    const marcadas = form.querySelectorAll('input[name="idOpcao"]:checked');
    if (marcadas.length === 0) { alert('Selecione pelo menos uma opção antes de votar.'); return false; }
    return true;
}
</script>
</body>
</html>
