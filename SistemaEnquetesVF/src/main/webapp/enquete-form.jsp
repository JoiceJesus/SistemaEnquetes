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
UsuarioModel logado = (UsuarioModel) session.getAttribute("usuarioLogado");
if (logado == null || logado.getNivelAcesso() == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
CategoriaDAO categoriaDAO = new CategoriaDAO();
EnqueteDAO enqueteDAO = new EnqueteDAO();
OpcaoRespostaDAO opcaoDAO = new OpcaoRespostaDAO();
EnqueteModel enquete = (EnqueteModel)request.getAttribute("enquete");
if (enquete == null) {
    String idEdicao = request.getParameter("id") != null ? request.getParameter("id") : request.getParameter("idEnquete");
    if (idEdicao != null) { try { enquete = enqueteDAO.buscarPorId(Integer.parseInt(idEdicao)); } catch(Exception ignored){} }
}
boolean editar = enquete != null;
List<CategoriaModel> categorias = categoriaDAO.listarTodos();
List<OpcaoRespostaModel> existentes = editar ? opcaoDAO.listarPorEnquete(enquete.getIdEnquete()) : Collections.<OpcaoRespostaModel>emptyList();
DateTimeFormatter br = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
String expiracaoBR = editar && enquete.getDataExpiracao()!=null ? enquete.getDataExpiracao().format(br) : "";
String erro = (String)request.getAttribute("erro");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head><meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title><%=editar?"Editar":"Criar"%> enquete</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-slate-100 min-h-screen p-5">
<div class="max-w-3xl mx-auto bg-white border rounded-xl p-6">
    <div class="mb-6"><h1 class="text-2xl font-bold"><%=editar?"Editar enquete":"Criar enquete"%></h1><p class="text-sm text-slate-500">Datas no padrão brasileiro: <strong>dd/mm/aaaa hh:mm</strong>.</p></div>
    <% if(erro!=null){ %><div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-3 mb-4"><%=esc(erro)%></div><% } %>
    <form id="formEnquete" method="post" action="<%=request.getContextPath()%>/enquete" class="space-y-4" onsubmit="return prepararData()">
        <input type="hidden" name="acao" value="<%=editar?"atualizar":"inserir"%>">
        <% if(editar){ %><input type="hidden" name="idEnquete" value="<%=enquete.getIdEnquete()%>"><% } %>
        <div><label class="block text-sm font-medium mb-1">Título</label><input class="w-full border rounded-lg p-2.5" name="titulo" required maxlength="255" value="<%=editar?esc(enquete.getTitulo()):""%>"></div>
        <div><label class="block text-sm font-medium mb-1">Descrição</label><textarea class="w-full border rounded-lg p-2.5 min-h-28" name="descricao" required maxlength="1000"><%=editar?esc(enquete.getDescricao()):""%></textarea></div>
        <div class="grid md:grid-cols-2 gap-4">
            <div><label class="block text-sm font-medium mb-1">Tipo de votação</label><select class="w-full border rounded-lg p-2.5" name="tipoVotacao"><option value="UNICA" <%=editar&&"UNICA".equals(enquete.getTipoVotacao())?"selected":""%>>Escolha única</option><option value="MULTIPLA" <%=editar&&"MULTIPLA".equals(enquete.getTipoVotacao())?"selected":""%>>Múltipla escolha</option></select></div>
            <div><label class="block text-sm font-medium mb-1">Categoria</label><select class="w-full border rounded-lg p-2.5" name="idCategoria" required><option value="">Selecione</option><% for(CategoriaModel c:categorias){ %><option value="<%=c.getIdCategoria()%>" <%=editar&&enquete.getCategoria()!=null&&enquete.getCategoria().getIdCategoria()==c.getIdCategoria()?"selected":""%>><%=esc(c.getNomeCategoria())%></option><% } %></select></div>
        </div>
        <div class="grid md:grid-cols-2 gap-4">
            <div><label class="block text-sm font-medium mb-1">Máximo de usuários por IP <span class="text-slate-400">(0 = sem limite)</span></label><input class="w-full border rounded-lg p-2.5" type="number" min="0" name="limiteVotosIp" value="<%=editar?enquete.getLimiteVotosIp():0%>"></div>
            <div><label class="block text-sm font-medium mb-1">Limite total de votos <span class="text-slate-400">(0 = sem limite)</span></label><input class="w-full border rounded-lg p-2.5" type="number" min="0" name="limiteQuantidadeVotos" value="<%=editar?enquete.getLimiteQuantidadeVotos():0%>"></div>
        </div>
        <div>
            <label class="block text-sm font-medium mb-1">Data e hora de expiração</label>
            <input id="dataExpiracaoBR" class="w-full border rounded-lg p-2.5" type="text" inputmode="numeric" required placeholder="dd/mm/aaaa hh:mm" value="<%=expiracaoBR%>" maxlength="16">
            <input id="dataExpiracao" type="hidden" name="dataExpiracao">
            <p id="erroData" class="hidden mt-1 text-sm text-red-600">Use o formato dd/mm/aaaa hh:mm e informe uma data/hora válida.</p>
        </div>
        <% if(!editar){ %>
        <div><label class="block text-sm font-medium mb-1">Opções de resposta <span class="text-slate-400">(mínimo 2)</span></label><input class="w-full border rounded-lg p-2.5" name="opcao" required maxlength="255" placeholder="Opção 1"><input class="w-full border rounded-lg p-2.5 mt-2" name="opcao" required maxlength="255" placeholder="Opção 2"><input class="w-full border rounded-lg p-2.5 mt-2" name="opcao" maxlength="255" placeholder="Opção 3 (opcional)"><input class="w-full border rounded-lg p-2.5 mt-2" name="opcao" maxlength="255" placeholder="Opção 4 (opcional)"></div>
        <% } else { %>
        <div class="bg-slate-50 border rounded-lg p-4"><p class="font-medium">Opções atuais</p><ul class="list-disc ml-5 mt-2 text-sm text-slate-600"><% for(OpcaoRespostaModel o:existentes){ %><li><%=esc(o.getDescricaoOpcao())%></li><% } %></ul><a class="inline-block mt-3 text-indigo-700 text-sm font-medium" href="<%=request.getContextPath()%>/opcao?acao=listar&idEnquete=<%=enquete.getIdEnquete()%>">Gerenciar opções →</a></div>
        <% } %>
        <div class="flex gap-3 pt-2"><button class="bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-lg font-semibold">Salvar enquete</button><a class="border px-5 py-2.5 rounded-lg" href="<%=request.getContextPath()%>/dashboard">Cancelar</a></div>
    </form>
</div>
<script>
function prepararData(){
    const campo=document.getElementById('dataExpiracaoBR');
    const erro=document.getElementById('erroData');
    const m=campo.value.trim().match(/^(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})$/);
    if(!m){erro.classList.remove('hidden');campo.focus();return false;}
    const d=Number(m[1]), mo=Number(m[2]), y=Number(m[3]), h=Number(m[4]), mi=Number(m[5]);
    const teste=new Date(y,mo-1,d,h,mi,0,0);
    const valido=teste.getFullYear()===y&&teste.getMonth()===mo-1&&teste.getDate()===d&&teste.getHours()===h&&teste.getMinutes()===mi;
    if(!valido){erro.classList.remove('hidden');campo.focus();return false;}
    erro.classList.add('hidden');
    const pad=n=>String(n).padStart(2,'0');
    document.getElementById('dataExpiracao').value=y+'-'+pad(mo)+'-'+pad(d)+'T'+pad(h)+':'+pad(mi);
    return true;
}
</script>
</body>
</html>
