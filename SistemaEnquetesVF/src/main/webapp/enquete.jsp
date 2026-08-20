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
if (usuario == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
boolean admin = usuario.getNivelAcesso() != null && usuario.getNivelAcesso().getIdNivelAcesso() == 2;
EnqueteDAO enqueteDAO = new EnqueteDAO();
OpcaoRespostaDAO opcaoDAO = new OpcaoRespostaDAO();
VotoDAO votoDAO = new VotoDAO();
enqueteDAO.atualizarEnquetesExpiradas();
List<EnqueteModel> enquetes = enqueteDAO.listarTodos();
Set<Integer> votadas = new HashSet<Integer>(votoDAO.listarIdsEnquetesVotadasPorUsuario(usuario.getIdUsuario()));
DateTimeFormatter dataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
String erro = request.getParameter("erro");
String sucesso = request.getParameter("sucesso");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Enquetes</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-slate-100 text-slate-900 min-h-screen">
<header class="bg-white border-b"><div class="max-w-7xl mx-auto px-5 py-4 flex flex-wrap items-center justify-between gap-3"><div><h1 class="text-xl font-bold">Enquetes</h1><p class="text-sm text-slate-500">Usuário: <%=esc(usuario.getNome())%></p></div><div class="flex flex-wrap gap-3 text-sm"><a class="text-indigo-700" href="<%=request.getContextPath()%>/dashboard">Meu painel</a><% if(admin){ %><a class="bg-indigo-600 text-white px-3 py-2 rounded-lg" href="<%=request.getContextPath()%>/enquete?acao=novo">Nova enquete</a><a class="text-indigo-700" href="<%=request.getContextPath()%>/categoria?acao=listar">Categorias</a><% } %><a class="text-red-600" href="<%=request.getContextPath()%>/usuario?acao=logout">Sair</a></div></div></header>
<main class="max-w-7xl mx-auto p-5 md:p-7">
    <% if (sucesso != null) { %><div class="mb-5 bg-emerald-50 border border-emerald-200 text-emerald-700 rounded-lg p-3">Voto registrado com sucesso.</div><% } %>
    <% if (erro != null) { %><div class="mb-5 bg-red-50 border border-red-200 text-red-700 rounded-lg p-3">Não foi possível concluir a operação: <%=esc(erro)%>.</div><% } %>
    <div class="mb-6"><h2 class="text-3xl font-bold">Todas as enquetes</h2><p class="text-slate-500">Lista carregada diretamente do banco de dados.</p></div>
    <% if(enquetes.isEmpty()){ %><div class="bg-white border border-dashed rounded-xl p-8 text-center text-slate-500">Nenhuma enquete cadastrada.</div><% } else { %>
    <div class="grid md:grid-cols-2 gap-5">
        <% for(EnqueteModel e : enquetes) {
            List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
            int total = votoDAO.contarVotosEnquete(e.getIdEnquete());
            boolean ja = votadas.contains(e.getIdEnquete());
            boolean aberta = "EM_CURSO".equals(e.getStatus()) && e.getDataExpiracao()!=null && e.getDataExpiracao().isAfter(LocalDateTime.now());
        %>
        <article class="bg-white border rounded-xl p-5">
            <div class="flex justify-between gap-4"><div><span class="text-xs font-semibold text-indigo-700"><%=esc(e.getCategoria()==null?"Sem categoria":e.getCategoria().getNomeCategoria())%></span><h3 class="text-xl font-bold mt-1"><%=esc(e.getTitulo())%></h3></div><span class="text-xs h-fit px-2 py-1 rounded-full <%=aberta?"bg-emerald-50 text-emerald-700":"bg-slate-100 text-slate-600"%>"><%=aberta?"Em curso":"Encerrada"%></span></div>
            <p class="text-sm text-slate-600 mt-3"><%=esc(e.getDescricao())%></p>
            <div class="text-xs text-slate-500 mt-3 space-y-1"><p>Tipo: <%="MULTIPLA".equals(e.getTipoVotacao())?"Múltipla escolha":"Escolha única"%></p><p>Expiração: <%=e.getDataExpiracao()==null?"-":e.getDataExpiracao().format(dataHora)%></p><p>Criada por: <%=esc(e.getUsuario()==null?"-":e.getUsuario().getNome())%></p></div>

            <% if (aberta && !ja) { %>
            <form method="post" action="<%=request.getContextPath()%>/voto" class="mt-4 space-y-2" onsubmit="return validarVoto(this)">
                <input type="hidden" name="idEnquete" value="<%=e.getIdEnquete()%>">
                <% for(OpcaoRespostaModel o : lista){ %><label class="block border rounded-lg p-3 hover:bg-slate-50"><input type="<%="MULTIPLA".equals(e.getTipoVotacao())?"checkbox":"radio"%>" name="idOpcao" value="<%=o.getIdOpcao()%>" class="mr-2"><%=esc(o.getDescricaoOpcao())%></label><% } %>
                <button class="bg-indigo-600 text-white px-4 py-2 rounded-lg">Votar</button>
            </form>
            <% } else { %>
            <div class="mt-4 space-y-3">
                <% for(OpcaoRespostaModel o : lista){ int v=votoDAO.contarVotosPorOpcao(o.getIdOpcao()); int pct=total==0?0:(int)Math.round(v*100.0/total); %>
                <div><div class="flex justify-between text-sm gap-3"><span><%=esc(o.getDescricaoOpcao())%></span><span><%=v%> (<%=pct%>%)</span></div><div class="h-2 bg-slate-100 rounded-full mt-1"><div class="h-2 bg-indigo-500 rounded-full" style="width:<%=pct%>%"></div></div></div>
                <% } %>
            </div>
            <p class="mt-3 text-xs text-slate-500"><%=total%> voto(s)<%=ja?" • Você já votou":""%></p>
            <% } %>

            <% if(admin){ %><div class="mt-5 pt-4 border-t text-sm"><a class="text-indigo-700 mr-3" href="<%=request.getContextPath()%>/enquete?acao=editar&id=<%=e.getIdEnquete()%>">Editar</a><a class="text-slate-700 mr-3" href="<%=request.getContextPath()%>/opcao?acao=listar&idEnquete=<%=e.getIdEnquete()%>">Gerenciar opções</a><a class="text-red-600" href="<%=request.getContextPath()%>/enquete?acao=excluir&id=<%=e.getIdEnquete()%>" onclick="return confirm('Excluir esta enquete?')">Excluir</a></div><% } %>
        </article>
        <% } %>
    </div>
    <% } %>
</main>
<script>
function validarVoto(form){const itens=form.querySelectorAll('input[name="idOpcao"]:checked');if(itens.length===0){alert('Selecione pelo menos uma opção.');return false;}return true;}
</script>
</body>
</html>
