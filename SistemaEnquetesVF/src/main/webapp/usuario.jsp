<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,model.UsuarioModel" %>
<!DOCTYPE html><html lang="pt-BR"><head><meta charset="UTF-8"><title>Usuários</title><link rel="stylesheet" href="assets/css/style.css"></head>
<body class="bg-gray-100 p-8"><div class="max-w-6xl mx-auto bg-white rounded-xl shadow p-6">
<h1 class="text-2xl font-bold mb-6">Gerenciamento de Usuários</h1>
<% List<UsuarioModel> usuarios=(List<UsuarioModel>)request.getAttribute("usuarios"); %>
<table class="w-full text-sm"><thead><tr><th class="text-left p-2">ID</th><th class="text-left p-2">Nome</th><th class="text-left p-2">E-mail</th><th class="text-left p-2">Perfil</th><th class="text-left p-2">Ações</th></tr></thead><tbody>
<% if (usuarios != null) for (UsuarioModel u: usuarios) { %><tr class="border-t"><td class="p-2"><%=u.getIdUsuario()%></td><td class="p-2"><%=u.getNome()%></td><td class="p-2"><%=u.getEmail()%></td><td class="p-2"><%=u.getNivelAcesso()==null?"-":u.getNivelAcesso().getTipo()%></td><td class="p-2"><a class="text-indigo-600 mr-3" href="usuario?acao=editar&id=<%=u.getIdUsuario()%>">Editar</a><a class="text-red-600" href="usuario?acao=excluir&id=<%=u.getIdUsuario()%>" onclick="return confirm('Excluir este usuário?')">Excluir</a></td></tr><% } %>
</tbody></table><div class="mt-6"><a class="text-indigo-600" href="usuario_adm.jsp">← Voltar</a></div></div></body></html>