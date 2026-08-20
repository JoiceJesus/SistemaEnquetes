<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*,model.*,dao.*" %>
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

EnqueteDAO enqueteDAO = new EnqueteDAO();
OpcaoRespostaDAO opcaoDAO = new OpcaoRespostaDAO();
VotoDAO votoDAO = new VotoDAO();
UsuarioModel logado = (UsuarioModel) session.getAttribute("usuarioLogado");

enqueteDAO.atualizarEnquetesExpiradas();
List<EnqueteModel> todas = enqueteDAO.listarTodos();
List<EnqueteModel> destaques = new ArrayList<EnqueteModel>();
for (EnqueteModel e : todas) {
    if ("EM_CURSO".equals(e.getStatus())) destaques.add(e);
}
Collections.sort(destaques, new Comparator<EnqueteModel>() {
    public int compare(EnqueteModel a, EnqueteModel b) {
        return Integer.compare(votoDAO.contarVotosEnquete(b.getIdEnquete()), votoDAO.contarVotosEnquete(a.getIdEnquete()));
    }
});
if (destaques.size() > 3) destaques = new ArrayList<EnqueteModel>(destaques.subList(0, 3));

int totalVotos = votoDAO.contarTodosVotos();
int totalAtivas = enqueteDAO.contarPorStatus("EM_CURSO");
String categoriaMaisVotada = votoDAO.buscarCategoriaMaisVotada();
String erro = (String) request.getAttribute("erro");
boolean cadastroSucesso = "sucesso".equals(request.getParameter("cadastro"));
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema de Enquetes</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 text-slate-900 min-h-screen">
<header class="bg-white border-b">
    <div class="max-w-7xl mx-auto px-5 py-4 flex flex-wrap items-center justify-between gap-3">
        <a href="<%=request.getContextPath()%>/index.jsp" class="text-xl font-bold text-indigo-700">Sistema de Enquetes</a>
        <div class="flex items-center gap-3 text-sm">
            <a href="#destaques" class="text-slate-600 hover:text-indigo-700">Enquetes em destaque</a>
            <% if (logado != null) { %>
                <a href="<%=request.getContextPath()%>/dashboard" class="bg-indigo-600 text-white px-4 py-2 rounded-lg">Ir para meu painel</a>
                <a href="<%=request.getContextPath()%>/usuario?acao=logout" class="text-red-600">Sair</a>
            <% } %>
        </div>
    </div>
</header>

<main>
    <section class="max-w-7xl mx-auto px-5 py-10 grid lg:grid-cols-2 gap-10 items-start">
        <div>
            <span class="inline-block text-xs font-semibold text-indigo-700 bg-indigo-50 border border-indigo-100 px-3 py-1 rounded-full">Votações com dados reais do sistema</span>
            <h1 class="text-4xl md:text-5xl font-extrabold mt-4 leading-tight">Participe, acompanhe e gerencie enquetes em um só lugar.</h1>
            <p class="text-slate-600 text-lg mt-4 max-w-2xl">As enquetes, votos e resultados mostrados nesta página são carregados diretamente do banco <strong>sistema_enquetes</strong>.</p>

            <div class="grid sm:grid-cols-3 gap-4 mt-8">
                <div class="bg-white border rounded-xl p-4 shadow-sm">
                    <p class="text-3xl font-bold text-indigo-700"><%=totalVotos%></p>
                    <p class="text-sm text-slate-500">Votos registrados</p>
                </div>
                <div class="bg-white border rounded-xl p-4 shadow-sm">
                    <p class="text-3xl font-bold text-emerald-700"><%=totalAtivas%></p>
                    <p class="text-sm text-slate-500">Enquetes ativas</p>
                </div>
                <div class="bg-white border rounded-xl p-4 shadow-sm">
                    <p class="text-lg font-bold text-amber-700"><%=esc(categoriaMaisVotada)%></p>
                    <p class="text-sm text-slate-500">Categoria mais votada</p>
                </div>
            </div>
        </div>

        <% if (logado == null) { %>
        <div id="acesso" class="bg-white border rounded-2xl shadow-sm overflow-hidden">
            <div class="grid grid-cols-2 border-b">
                <button id="btnLogin" type="button" onclick="mostrarAba('login')" class="py-3 font-semibold text-indigo-700 border-b-2 border-indigo-600">Entrar</button>
                <button id="btnCadastro" type="button" onclick="mostrarAba('cadastro')" class="py-3 font-semibold text-slate-500">Criar conta</button>
            </div>
            <div class="p-6">
                <% if (erro != null) { %><div class="mb-4 bg-red-50 border border-red-200 text-red-700 rounded-lg p-3"><%=esc(erro)%></div><% } %>
                <% if (cadastroSucesso) { %><div class="mb-4 bg-emerald-50 border border-emerald-200 text-emerald-700 rounded-lg p-3">Cadastro realizado com sucesso. Faça seu login.</div><% } %>

                <form id="formLogin" method="post" action="<%=request.getContextPath()%>/usuario" class="space-y-4">
                    <input type="hidden" name="acao" value="login">
                    <div><label class="block text-sm font-medium mb-1">E-mail</label><input type="email" name="email" required class="w-full border rounded-lg px-3 py-2" placeholder="seu@email.com"></div>
                    <div><label class="block text-sm font-medium mb-1">Senha</label><input type="password" name="senha" required class="w-full border rounded-lg px-3 py-2" placeholder="Sua senha"></div>
                    <button class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-lg py-2.5">Entrar</button>
                </form>

                <form id="formCadastro" method="post" action="<%=request.getContextPath()%>/usuario" class="space-y-4 hidden">
                    <input type="hidden" name="acao" value="cadastro">
                    <div><label class="block text-sm font-medium mb-1">Nome completo</label><input type="text" name="nome" required maxlength="100" class="w-full border rounded-lg px-3 py-2" placeholder="Seu nome"></div>
                    <div><label class="block text-sm font-medium mb-1">E-mail</label><input type="email" name="email" required maxlength="150" class="w-full border rounded-lg px-3 py-2" placeholder="seu@email.com"></div>
                    <div><label class="block text-sm font-medium mb-1">Senha</label><input type="password" name="senha" required class="w-full border rounded-lg px-3 py-2" placeholder="Crie uma senha"></div>
                    <div>
                        <label class="block text-sm font-medium mb-1">Tipo de perfil</label>
                        <select name="tipo" required class="w-full border rounded-lg px-3 py-2">
                            <option value="comum">Usuário comum</option>
                            <option value="administrador">Administrador</option>
                        </select>
                    </div>
                    <button class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-lg py-2.5">Criar conta</button>
                </form>
            </div>
        </div>
        <% } else { %>
        <div class="bg-white border rounded-2xl shadow-sm p-6">
            <p class="text-sm text-slate-500">Sessão ativa</p>
            <h2 class="text-2xl font-bold mt-1"><%=esc(logado.getNome())%></h2>
            <p class="text-slate-600"><%=esc(logado.getEmail())%></p>
            <div class="flex flex-wrap gap-3 mt-5">
                <a href="<%=request.getContextPath()%>/dashboard" class="bg-indigo-600 text-white px-4 py-2 rounded-lg">Abrir meu painel</a>
                <% if (logado.getNivelAcesso() != null && logado.getNivelAcesso().getIdNivelAcesso() == 2) { %>
                <a href="<%=request.getContextPath()%>/enquete?acao=novo" class="border border-indigo-600 text-indigo-700 px-4 py-2 rounded-lg">Criar enquete</a>
                <% } %>
            </div>
        </div>
        <% } %>
    </section>

    <section id="destaques" class="bg-white border-y">
        <div class="max-w-7xl mx-auto px-5 py-10">
            <div class="flex flex-wrap items-end justify-between gap-3 mb-6">
                <div>
                    <h2 class="text-2xl font-bold">Enquetes em destaque</h2>
                    <p class="text-slate-500">As enquetes ativas com mais votos neste momento.</p>
                </div>
                <% if (logado != null) { %><a href="<%=request.getContextPath()%>/enquete?acao=listar" class="text-indigo-700 font-medium">Ver todas →</a><% } %>
            </div>

            <% if (destaques.isEmpty()) { %>
                <div class="border border-dashed rounded-xl p-8 text-center text-slate-500">Nenhuma enquete ativa cadastrada no banco no momento.</div>
            <% } else { %>
            <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-5">
                <% for (EnqueteModel e : destaques) {
                    List<OpcaoRespostaModel> lista = opcaoDAO.listarPorEnquete(e.getIdEnquete());
                    int total = votoDAO.contarVotosEnquete(e.getIdEnquete());
                %>
                <article class="border rounded-xl p-5 shadow-sm">
                    <div class="flex items-center justify-between gap-2">
                        <span class="text-xs font-semibold text-indigo-700 bg-indigo-50 rounded-full px-2 py-1"><%=esc(e.getCategoria() == null ? "Sem categoria" : e.getCategoria().getNomeCategoria())%></span>
                        <span class="text-xs text-slate-500"><%=total%> voto(s)</span>
                    </div>
                    <h3 class="text-lg font-bold mt-3"><%=esc(e.getTitulo())%></h3>
                    <p class="text-sm text-slate-600 mt-2"><%=esc(e.getDescricao())%></p>
                    <div class="mt-4 space-y-2">
                        <% for (OpcaoRespostaModel o : lista) {
                            int v = votoDAO.contarVotosPorOpcao(o.getIdOpcao());
                            int pct = total == 0 ? 0 : (int)Math.round(v * 100.0 / total);
                        %>
                            <div>
                                <div class="flex justify-between text-xs"><span><%=esc(o.getDescricaoOpcao())%></span><span><%=pct%>%</span></div>
                                <div class="h-1.5 bg-slate-100 rounded-full mt-1"><div class="h-1.5 bg-indigo-500 rounded-full" style="width:<%=pct%>%"></div></div>
                            </div>
                        <% } %>
                    </div>
                    <div class="mt-5">
                        <% if (logado != null) { %>
                            <a href="<%=request.getContextPath()%>/enquete?acao=listar" class="text-indigo-700 font-semibold text-sm">Abrir para votar →</a>
                        <% } else { %>
                            <a href="#acesso" onclick="mostrarAba('login')" class="text-indigo-700 font-semibold text-sm">Entre para votar →</a>
                        <% } %>
                    </div>
                </article>
                <% } %>
            </div>
            <% } %>
        </div>
    </section>
</main>

<footer class="max-w-7xl mx-auto w-full px-5 py-6 text-sm text-slate-500">© 2026 Sistema de Enquetes.</footer>
<script>
function mostrarAba(aba) {
    const login = document.getElementById('formLogin');
    const cadastro = document.getElementById('formCadastro');
    const btnLogin = document.getElementById('btnLogin');
    const btnCadastro = document.getElementById('btnCadastro');
    if (!login || !cadastro) return;
    const ehLogin = aba === 'login';
    login.classList.toggle('hidden', !ehLogin);
    cadastro.classList.toggle('hidden', ehLogin);
    btnLogin.className = ehLogin ? 'py-3 font-semibold text-indigo-700 border-b-2 border-indigo-600' : 'py-3 font-semibold text-slate-500';
    btnCadastro.className = !ehLogin ? 'py-3 font-semibold text-indigo-700 border-b-2 border-indigo-600' : 'py-3 font-semibold text-slate-500';
}
</script>
</body>
</html>
