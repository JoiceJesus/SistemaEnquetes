<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.*,dao.*" %>
<%!
private String esc(String s){if(s==null)return "";return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
%>
<%
request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
UsuarioModel logado=(UsuarioModel)session.getAttribute("usuarioLogado");
if(logado==null||logado.getNivelAcesso()==null||logado.getNivelAcesso().getIdNivelAcesso()!=2){response.sendRedirect(request.getContextPath()+"/index.jsp");return;}
CategoriaModel categoria=(CategoriaModel)request.getAttribute("categoria");
if(categoria==null && request.getParameter("id")!=null){try{categoria=new CategoriaDAO().buscarPorId(Integer.parseInt(request.getParameter("id")));}catch(Exception ignored){}}
boolean editar=categoria!=null;
%>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title><%=editar?"Editar":"Nova"%> categoria</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-slate-100 p-5"><div class="max-w-xl mx-auto bg-white border rounded-xl p-6"><h1 class="text-2xl font-bold mb-5"><%=editar?"Editar categoria":"Nova categoria"%></h1><form method="post" action="<%=request.getContextPath()%>/categoria" class="space-y-4"><input type="hidden" name="acao" value="<%=editar?"atualizar":"inserir"%>"><% if(editar){ %><input type="hidden" name="idCategoria" value="<%=categoria.getIdCategoria()%>"><% } %><div><label class="block text-sm font-medium mb-1">Nome da categoria</label><input class="w-full border rounded-lg p-2.5" name="nomeCategoria" required maxlength="100" value="<%=editar?esc(categoria.getNomeCategoria()):""%>" placeholder="Ex.: Tecnologia"></div><div class="flex gap-3"><button class="bg-indigo-600 text-white px-4 py-2 rounded-lg">Salvar</button><a class="border px-4 py-2 rounded-lg" href="<%=request.getContextPath()%>/categoria?acao=listar">Cancelar</a></div></form></div></body></html>
