<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.EnqueteDAO,dao.UsuarioDAO,dao.VotoDAO" %>
<%
request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
EnqueteDAO homeEnqueteDAO = new EnqueteDAO();
VotoDAO homeVotoDAO = new VotoDAO();
UsuarioDAO homeUsuarioDAO = new UsuarioDAO();
homeEnqueteDAO.atualizarEnquetesExpiradas();
int homeTotalVotos = homeVotoDAO.contarTodosVotos();
int homeAtivas = homeEnqueteDAO.contarPorStatus("EM_CURSO");
int homeUsuarios = homeUsuarioDAO.listarTodos().size();
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Enquete - Plataforma de Votações e Enquetes</title>
  
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    // Configuração do Tailwind para alternar modo escuro via classe
    tailwind.config = {
      darkMode: 'class',
    }
  </script>

  <!-- Script para carregar a preferência de tema antes da renderização -->
  <script>
    if (localStorage.getItem('theme') === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  </script>

  <!-- Ícones Phosphor -->
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
</head>
<body class="bg-gray-50 dark:bg-gray-900 text-gray-800 dark:text-gray-100 antialiased font-sans min-h-screen flex flex-col justify-between transition-colors duration-200">

  <!-- ================= NAVBAR ================= -->
  <header class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      
      <!-- Logo -->
      <a href="index.jsp" class="flex items-center gap-2">
        <img src="assets/img/logo.png" alt="Enquete Online Logo" class="h-20 w-auto object-contain">
      </a>

      <!-- Links Rápidos -->
      <nav class="hidden md:flex items-center gap-6 text-sm font-medium text-gray-600 dark:text-gray-300">
        <a href="#recursos" class="hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors"></a>
        <a href="enquete?acao=listar" class="hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors"></a>
        <a href="index.jsp" class="hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors"></a>
      </nav>

      <!-- Botão de Ação Rápida e Botão de Tema -->
      <div class="flex items-center gap-3">
        <!-- BOTÃO DE ALTERAR TEMA -->
        <button onclick="toggleDarkMode()" aria-label="Alternar modo escuro" class="p-2 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 focus:outline-none transition-colors">
          <i id="theme-toggle-dark-icon" class="ph ph-moon text-xl hidden dark:block"></i>
          <i id="theme-toggle-light-icon" class="ph ph-sun text-xl block dark:hidden"></i>
        </button>

        <button onclick="switchTab('register'); document.getElementById('auth-card').scrollIntoView({behavior: 'smooth'});" class="bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 hover:bg-indigo-100 dark:hover:bg-indigo-900/50 px-4 py-2 rounded-lg text-sm font-semibold transition-colors border border-indigo-100 dark:border-indigo-800">
          Criar Conta
        </button>
      </div>
    </div>
  </header>

  <!-- ================= CONTEÚDO PRINCIPAL (SPLIT HERO) ================= -->
  <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 lg:py-12 flex-grow flex items-center">
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center w-full">

      <!-- COLUNA DA ESQUERDA: APRESENTAÇÃO DA PLATAFORMA -->
      <div class="lg:col-span-7 space-y-8">
        
        <!-- Badge -->
        <div class="inline-flex items-center gap-2 bg-indigo-50 dark:bg-indigo-950/60 border border-indigo-100 dark:border-indigo-800 text-indigo-700 dark:text-indigo-300 px-3.5 py-1.5 rounded-full text-xs font-semibold">
          <i class="ph ph-sparkle text-sm"></i>
          <span>Plataforma de Decisões Colaborativas</span>
        </div>

        <!-- Título principal -->
        <h1 class="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-gray-900 dark:text-gray-100 tracking-tight leading-tight">
          Sua voz conta. Suas decisões transformam a comunidade.
        </h1>

        <p class="text-base sm:text-lg text-gray-600 dark:text-gray-300 max-w-2xl leading-relaxed">
          Participe de votações em tempo real, acompanhe estatísticas transparentes e crie enquetes para engajar seu público em uma plataforma simples e segura.
        </p>

        <!-- CARDS DE MÉTRICAS -->
        <div class="grid grid-cols-3 gap-4 pt-2">
          <div class="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm transition-colors">
            <p class="text-2xl font-bold text-indigo-600 dark:text-indigo-400"><%=homeTotalVotos%></p>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium mt-0.5">Votos Computados</p>
          </div>
          <div class="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm transition-colors">
            <p class="text-2xl font-bold text-emerald-600 dark:text-emerald-400"><%=homeUsuarios%></p>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium mt-0.5">Usuários Cadastrados</p>
          </div>
          <div class="bg-white dark:bg-gray-800 p-4 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm transition-colors">
            <p class="text-2xl font-bold text-amber-600 dark:text-amber-400"><%=homeAtivas%></p>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium mt-0.5">Enquetes Ativas</p>
          </div>
        </div>

        <!-- RECURSOS DO SISTEMA -->
        <div id="recursos" class="space-y-3 pt-2">
          <div class="flex items-start gap-3">
            <div class="p-1.5 bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 rounded-lg mt-0.5">
              <i class="ph ph-check text-base"></i>
            </div>
            <div>
              <h4 class="text-sm font-bold text-gray-900 dark:text-gray-100">Perfis de Acesso Diferenciados</h4>
              <p class="text-xs text-gray-500 dark:text-gray-400">Escolha entre perfil <strong>Comum</strong> (apenas votar) e <strong>Criador</strong> (votar e gerenciar enquetes).</p>
            </div>
          </div>

          <div class="flex items-start gap-3">
            <div class="p-1.5 bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 rounded-lg mt-0.5">
              <i class="ph ph-chart-pie-slice text-base"></i>
            </div>
            <div>
              <h4 class="text-sm font-bold text-gray-900 dark:text-gray-100">Estatísticas e Gráficos Instantâneos</h4>
              <p class="text-xs text-gray-500 dark:text-gray-400">Acompanhe a apuração percentual e a distribuição de votos por opção em tempo real.</p>
            </div>
          </div>
        </div>

      </div>

      <!-- COLUNA DA DIREITA: CARD DE AUTENTICAÇÃO -->
      <div id="auth-card" class="lg:col-span-5 w-full max-w-md mx-auto">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden transition-colors">
          
          <!-- Alternador de Abas (Tabs) -->
          <div class="flex border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
            <button id="tab-login" onclick="switchTab('login')" class="flex-1 py-3.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400 transition-all">
              Entrar
            </button>
            <button id="tab-register" onclick="switchTab('register')" class="flex-1 py-3.5 text-sm font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-all">
              Criar Conta
            </button>
          </div>

          <div class="p-6 sm:p-8">
            <% if(request.getAttribute("erro")!=null){ %><div class="mb-4 rounded-lg bg-red-50 dark:bg-red-950/40 border border-red-200 dark:border-red-900 px-3 py-2 text-xs text-red-700 dark:text-red-300"><%=request.getAttribute("erro")%></div><% } %>
            <% if("sucesso".equals(request.getParameter("cadastro"))){ %><div class="mb-4 rounded-lg bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-900 px-3 py-2 text-xs text-emerald-700 dark:text-emerald-300">Cadastro realizado com sucesso. Faça login para continuar.</div><% } %>

            <!-- ================= FORMULÁRIO DE LOGIN ================= -->
            <form id="form-login" class="space-y-4" method="post" action="usuario">
              <input type="hidden" name="acao" value="login">
              
              <!-- E-mail -->
              <div>
                <label for="login-email" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">E-mail</label>
                <div class="relative">
                  <i class="ph ph-envelope-simple absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <input type="email" id="login-email" name="email" required placeholder="seu@email.com" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
                </div>
              </div>

              <!-- Senha -->
              <div>
                <div class="flex justify-between items-center mb-1">
                  <label for="login-senha" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider">Senha</label>
                  <button type="button" onclick="toggleModal(true)" class="text-xs text-indigo-600 dark:text-indigo-400 hover:underline font-medium focus:outline-none">
                    Esqueceu?
                  </button>
                </div>
                <div class="relative">
                  <i class="ph ph-lock-key absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <input type="password" id="login-senha" name="senha" required placeholder="••••••••" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
                </div>
              </div>

              <!-- Lembrar-me -->
              <div class="flex items-center justify-between pt-1">
                <label class="flex items-center cursor-pointer">
                  <input type="checkbox" class="rounded border-gray-300 dark:border-gray-600 dark:bg-gray-700 text-indigo-600 focus:ring-indigo-500 h-4 w-4">
                  <span class="ml-2 text-xs text-gray-600 dark:text-gray-400">Lembrar neste navegador</span>
                </label>
              </div>

              <!-- Botão Entrar -->
              <button type="submit" class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 px-4 rounded-lg text-sm shadow-sm transition-colors flex items-center justify-center gap-2 mt-2">
                <span>Entrar na conta</span>
                <i class="ph ph-arrow-right text-lg"></i>
              </button>
            </form>

            <!-- ================= FORMULÁRIO DE CADASTRO ================= -->
            <form id="form-register" class="space-y-4 hidden" method="post" action="usuario">
              <input type="hidden" name="acao" value="cadastro">
              
              <!-- Nome -->
              <div>
                <label for="reg-nome" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">Nome Completo</label>
                <div class="relative">
                  <i class="ph ph-user absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <input type="text" id="reg-nome" name="nome" required placeholder="Ex: João da Silva" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
                </div>
              </div>

              <!-- E-mail -->
              <div>
                <label for="reg-email" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">E-mail</label>
                <div class="relative">
                  <i class="ph ph-envelope-simple absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <input type="email" id="reg-email" name="email" required placeholder="seu@email.com" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
                </div>
              </div>

              <!-- Senha -->
              <div>
                <label for="reg-senha" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">Senha</label>
                <div class="relative">
                  <i class="ph ph-lock-key absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <input type="password" id="reg-senha" name="senha" required placeholder="Crie uma senha forte" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
                </div>
              </div>

              <!-- Tipo de Usuário -->
              <div>
                <label for="reg-tipo" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">Tipo de Perfil</label>
                <div class="relative">
                  <i class="ph ph-user-gear absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
                  <select id="reg-tipo" name="tipo" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all text-gray-700 dark:text-gray-100">
                    <option value="comum">Comum (Apenas Votar)</option>
                    <option value="administrador">Administrador (Gerenciar o sistema)</option>
                  </select>
                </div>
              </div>

              <!-- Botão Cadastrar -->
              <button type="submit" class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 px-4 rounded-lg text-sm shadow-sm transition-colors flex items-center justify-center gap-2 mt-2">
                <span>Criar minha conta</span>
                <i class="ph ph-check-circle text-lg"></i>
              </button>
            </form>

          </div>
        </div>

        <!-- Links Auxiliares -->
        <div class="text-center mt-4 flex justify-center gap-3">
          <% model.UsuarioModel homeLogado=(model.UsuarioModel)session.getAttribute("usuarioLogado"); boolean homeAdmin=homeLogado!=null&&homeLogado.getNivelAcesso()!=null&&homeLogado.getNivelAcesso().getIdNivelAcesso()==2; %><a href="<%= homeAdmin ? request.getContextPath()+"/enquete?acao=novo" : (homeLogado!=null ? request.getContextPath()+"/dashboard" : "#auth-card") %>" onclick="<%= homeLogado == null ? "switchTab(\'login\')" : "" %>" class="text-xs text-indigo-600 hover:underline font-medium">Criar enquete</a>
          <a href="enquete?acao=listar" class="text-xs text-indigo-600 hover:underline font-medium">Votar em enquete</a>
        </div>
      </div>

    </div>
  </main>

  <!-- ================= FOOTER ================= -->
  <footer class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 py-4 mt-8 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between text-xs text-gray-500 dark:text-gray-400 gap-2">
      <p>&copy; 2026 Sistema de Enquetes. Todos os direitos reservados.</p>
      <div class="flex gap-4">
        <a href="#" class="hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors">Termos de Uso</a>
        <a href="#" class="hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors">Privacidade</a>
      </div>
    </div>
  </footer>

  <!-- ================= POP-UP / MODAL (RECUPERAR SENHA) ================= -->
  <div id="modal-forgot-password" class="fixed inset-0 bg-gray-900/50 backdrop-blur-sm z-50 flex items-center justify-center p-4 hidden transition-opacity">
    <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl border border-gray-200 dark:border-gray-700 w-full max-w-sm p-6 relative transition-colors">
      
      <!-- Botão Fechar -->
      <button onclick="toggleModal(false)" class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 p-1 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
        <i class="ph ph-x text-lg"></i>
      </button>

      <!-- Ícone e Título -->
      <div class="text-center mb-6">
        <div class="w-12 h-12 bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 rounded-full flex items-center justify-center mx-auto mb-3 text-xl">
          <i class="ph ph-key-hole"></i>
        </div>
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100">Recuperar Senha</h2>
        <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Informe seu e-mail cadastrado para receber as instruções de redefinição.</p>
      </div>

      <!-- Formulário do Modal -->
      <form onsubmit="handleResetPassword(event)" class="space-y-4">
        <div>
          <label for="recovery-email" class="block text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider mb-1">E-mail Cadastrado</label>
          <div class="relative">
            <i class="ph ph-envelope-simple absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
            <input type="email" id="recovery-email" required placeholder="seu@email.com" class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
          </div>
        </div>

        <div class="flex gap-2 pt-2">
          <button type="button" onclick="toggleModal(false)" class="w-1/2 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-300 font-semibold py-2 px-4 rounded-lg text-sm transition-colors">
            Cancelar
          </button>
          <button type="submit" class="w-1/2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg text-sm shadow-sm transition-colors">
            Enviar Link
          </button>
        </div>
      </form>

    </div>
  </div>

  <!-- Scripts JS -->
  <script>
    // Alternar abas Login / Cadastro
    function switchTab(tab) {
      const formLogin = document.getElementById('form-login');
      const formRegister = document.getElementById('form-register');
      const tabLogin = document.getElementById('tab-login');
      const tabRegister = document.getElementById('tab-register');

      if (tab === 'login') {
        formLogin.classList.remove('hidden');
        formRegister.classList.add('hidden');
        
        tabLogin.className = "flex-1 py-3.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400 transition-all";
        tabRegister.className = "flex-1 py-3.5 text-sm font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-all";
      } else {
        formLogin.classList.add('hidden');
        formRegister.classList.remove('hidden');
        
        tabRegister.className = "flex-1 py-3.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400 transition-all";
        tabLogin.className = "flex-1 py-3.5 text-sm font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-all";
      }
    }

    // Controlar visibilidade do Pop-up (Modal)
    function toggleModal(show) {
      const modal = document.getElementById('modal-forgot-password');
      if (show) {
        modal.classList.remove('hidden');
      } else {
        modal.classList.add('hidden');
      }
    }

    // Simulação do envio de e-mail de recuperação
    function handleResetPassword(event) {
      event.preventDefault();
      const email = document.getElementById('recovery-email').value;
      alert(`Um link de redefinição foi enviado para: ${email}`);
      toggleModal(false);
    }

    // Alternar Modo Escuro
    function toggleDarkMode() {
      if (document.documentElement.classList.contains('dark')) {
        document.documentElement.classList.remove('dark');
        localStorage.setItem('theme', 'light');
      } else {
        document.documentElement.classList.add('dark');
        localStorage.setItem('theme', 'dark');
      }
    }
  </script>

</body>
</html>