package controller;
import java.sql.Connection;

import util.Conexao;

public class TesteConexao {

    public static void main(String[] args) throws Exception {

        Connection conexao = Conexao.getConnection();

        if (conexao != null) {
            System.out.println("Teste de conexão com o banco realizado com sucesso!");
            Conexao.fechar(conexao);
        }

    }

}
