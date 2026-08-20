<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,model.*,dao.*" %>
<%!
private String esc(String s){if(s==null)return "";return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
%>
<%
request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
UsuarioModel logado=(UsuarioModel)session.getAttribute("usuarioLogado");
if(logado==null||logado.getNivelAcesso()==null||logado.getNivelAcesso().getIdNivelAcesso()!=2){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
List<CategoriaModel> categorias=new CategoriaDAO().listarTodos();
%>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Categorias</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-slate-100 p-5"><div class="max-w-5xl mx-auto"><div class="flex flex-wrap justify-between items-center gap-3 mb-5"><div><h1 class="text-2xl font-bold">Categorias</h1><p class="text-sm text-slate-500">Categorias cadastradas no banco.</p></div><div class="flex gap-3"><a href="<%=request.getContextPath()%>/dashboard" class="border px-4 py-2 rounded-lg">Dashboard</a><a href="<%=request.getContextPath()%>/categoria?acao=novo" class="bg-indigo-600 text-white px-4 py-2 rounded-lg">Nova categoria</a></div></div>
<div class="bg-white border rounded-xl overflow-hidden"><% if(categorias.isEmpty()){ %><div class="p-8 text-center text-slate-500">Nenhuma categoria cadastrada.</div><% } else { %><table class="w-full text-sm"><thead class="bg-slate-50"><tr><th class="text-left p-3">ID</th><th class="text-left p-3">Nome</th><th class="text-left p-3">Ações</th></tr></thead><tbody><% for(CategoriaModel c:categorias){ %><tr class="border-t"><td class="p-3"><%=c.getIdCategoria()%></td><td class="p-3 font-medium"><%=esc(c.getNomeCategoria())%></td><td class="p-3"><a class="text-indigo-700 mr-3" href="<%=request.getContextPath()%>/categoria?acao=editar&id=<%=c.getIdCategoria()%>">Editar</a><a class="text-red-600" href="<%=request.getContextPath()%>/categoria?acao=excluir&id=<%=c.getIdCategoria()%>" onclick="return confirm('Excluir esta categoria? A exclusão não será possível se houver enquetes vinculadas.')">Excluir</a></td></tr><% } %></tbody></table><% } %></div></div></body></html>
