<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, model.*, dao.*" %>

<%!
private String esc(String s) {
    if (s == null) return "";

    return s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
}
%>

<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

UsuarioModel logado =
    (UsuarioModel) session.getAttribute("usuarioLogado");

if (
    logado == null ||
    logado.getNivelAcesso() == null ||
    logado.getNivelAcesso().getIdNivelAcesso() != 2
) {
    response.sendRedirect(
        request.getContextPath() + "/index.jsp"
    );
    return;
}

String idTexto = request.getParameter("idEnquete");

int idEnquete = 0;

try {
    idEnquete = Integer.parseInt(idTexto);
} catch (Exception ignored) {
}

OpcaoRespostaDAO opcaoDAO = new OpcaoRespostaDAO();
EnqueteDAO enqueteDAO = new EnqueteDAO();

List<OpcaoRespostaModel> opcoes = new ArrayList<>();
EnqueteModel enquete = null;

if (idEnquete > 0) {
    opcoes = opcaoDAO.listarPorEnquete(idEnquete);
    enquete = enqueteDAO.buscarPorId(idEnquete);
}
%>

<!DOCTYPE html>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">

    <meta
        http-equiv="Content-Type"
        content="text/html; charset=UTF-8"
    >

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1"
    >

    <title>Opções da enquete</title>

    <script src="https://cdn.tailwindcss.com"></script>
</head>

<body class="bg-slate-100 p-5">

<div class="max-w-2xl mx-auto">

    <%
    if (idEnquete <= 0) {
    %>

        <div class="bg-white border rounded-xl p-5">

            <p class="text-red-600 font-medium">
                Enquete não informada.
            </p>

            <div class="mt-4">
                <a
                    class="text-indigo-700"
                    href="<%=request.getContextPath()%>/enquete?acao=listar"
                >
                    ← Voltar para enquetes
                </a>
            </div>

        </div>

    <%
    } else {
    %>

        <div class="mb-5">

            <h1 class="text-2xl font-bold">
                Opções de resposta
            </h1>

            <p class="text-slate-500">

                <%
                if (enquete == null) {
                    out.print(
                        "Enquete #" + idEnquete
                    );
                } else {
                    out.print(
                        esc(enquete.getTitulo())
                    );
                }
                %>

            </p>

        </div>

        <div class="bg-white border rounded-xl p-5">

            <form
                method="post"
                action="<%=request.getContextPath()%>/opcao"
                class="flex flex-col sm:flex-row gap-2 mb-5"
            >

                <input
                    type="hidden"
                    name="idEnquete"
                    value="<%=idEnquete%>"
                >

                <input
                    class="flex-1 border rounded-lg p-2.5"
                    type="text"
                    name="descricaoOpcao"
                    required
                    maxlength="255"
                    placeholder="Nova opção"
                >

                <button
                    type="submit"
                    class="bg-indigo-600 text-white px-4 py-2 rounded-lg"
                >
                    Adicionar
                </button>

            </form>

            <%
            if (opcoes == null || opcoes.isEmpty()) {
            %>

                <p class="text-slate-500">
                    Nenhuma opção cadastrada.
                </p>

            <%
            } else {
            %>

                <ul class="space-y-2">

                    <%
                    for (OpcaoRespostaModel o : opcoes) {
                    %>

                        <li
                            class="border rounded-lg p-3 flex justify-between gap-3"
                        >

                            <span>
                                <%=esc(o.getDescricaoOpcao())%>
                            </span>

                            <a
                                class="text-red-600"
                                href="<%=request.getContextPath()%>/opcao?acao=excluir&id=<%=o.getIdOpcao()%>&idEnquete=<%=idEnquete%>"
                                onclick="return confirm('Excluir esta opção?');"
                            >
                                Excluir
                            </a>

                        </li>

                    <%
                    }
                    %>

                </ul>

            <%
            }
            %>

            <div class="mt-5">

                <a
                    class="text-indigo-700"
                    href="<%=request.getContextPath()%>/enquete?acao=listar"
                >
                    ← Voltar para enquetes
                </a>

            </div>

        </div>

    <%
    }
    %>

</div>

</body>
</html>