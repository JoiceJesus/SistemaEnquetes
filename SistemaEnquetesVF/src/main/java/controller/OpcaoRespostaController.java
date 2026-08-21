package controller;

import java.io.IOException;

import dao.OpcaoRespostaDAO;
import model.EnqueteModel;
import model.OpcaoRespostaModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.UsuarioModel;

@WebServlet("/opcao")
public class OpcaoRespostaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private OpcaoRespostaDAO opcaoDAO;

    @Override
    public void init() {
        opcaoDAO = new OpcaoRespostaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getNivelAcesso() == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String acao =
            request.getParameter("acao");

        if ("listar".equals(acao)) {

            int idEnquete =
                Integer.parseInt(
                    request.getParameter(
                        "idEnquete"
                    )
                );

            request.setAttribute(
                "opcoes",
                opcaoDAO.listarPorEnquete(
                    idEnquete
                )
            );

            request.getRequestDispatcher(
                "/opcao.jsp"
            ).forward(request, response);

        } else if ("excluir".equals(acao)) {

            int id =
                Integer.parseInt(
                    request.getParameter("id")
                );

            opcaoDAO.excluir(id);

            response.sendRedirect(
                request.getContextPath()
                + "/opcao?acao=listar&idEnquete="
                + request.getParameter(
                    "idEnquete"
                )
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UsuarioModel logado = session == null ? null : (UsuarioModel) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getNivelAcesso() == null || logado.getNivelAcesso().getIdNivelAcesso() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int idEnquete =
            Integer.parseInt(
                request.getParameter(
                    "idEnquete"
                )
            );

        OpcaoRespostaModel opcao =
            new OpcaoRespostaModel();

        opcao.setDescricaoOpcao(
            request.getParameter(
                "descricaoOpcao"
            )
        );

        EnqueteModel enquete =
            new EnqueteModel();

        enquete.setIdEnquete(idEnquete);

        opcao.setEnquete(enquete);

        opcaoDAO.inserir(opcao);

        response.sendRedirect(
            request.getContextPath()
            + "/opcao?acao=listar&idEnquete="
            + idEnquete
        );
    }
}