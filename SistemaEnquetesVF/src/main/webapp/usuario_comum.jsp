<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Enquete - Votações e Estatísticas</title>
  
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Ícones Phosphor -->
  <script src="https://unpkg.com/@phosphor-icons/web"></script>

  <!-- Arquivo CSS Personalizado -->
  <link rel="stylesheet" href="assets/css/style.css">

  <!-- Script inline essencial para prevenir FOUC (troca de tema visível) -->
  <script>
    if (localStorage.getItem('theme') === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  </script>
</head>
<body class="bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 antialiased font-sans min-h-screen flex flex-col transition-colors duration-200">

  <!-- ================= HEADER ================= -->
  <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      
      <!-- Logo -->
      <a href="index.jsp" class="flex items-center gap-2">
        <img src="assets/img/logo.png" alt="Enquete Online Logo" class="h-20 w-auto object-contain">
      </a>

      <!-- Campo de Busca -->
      <div class="hidden md:flex items-center flex-1 max-w-md mx-8">
        <div class="relative w-full">
          <i class="ph ph-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500 text-lg"></i>
          <input type="text" placeholder="Buscar enquetes ou resultados..." class="w-full pl-10 pr-4 py-2 bg-gray-50 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:bg-white dark:focus:bg-gray-700 transition-all">
        </div>
      </div>

      <!-- Ações do Perfil e Tema -->
      <div class="flex items-center gap-3">
        
        <!-- BOTÃO DE ALTERAR TEMA -->
        <button onclick="toggleDarkMode()" aria-label="Alternar modo escuro" class="p-2 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 focus:outline-none transition-colors">
          <i id="theme-toggle-dark-icon" class="ph ph-moon text-xl hidden dark:block"></i>
          <i id="theme-toggle-light-icon" class="ph ph-sun text-xl block dark:hidden"></i>
        </button>

        <span class="hidden sm:inline-flex items-center gap-1 bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 text-xs font-semibold px-2.5 py-1 rounded-md border border-gray-200 dark:border-gray-600">
          <i class="ph ph-user text-sm"></i>
          Eleitor
        </span>

        <div class="h-6 w-px bg-gray-200 dark:bg-gray-700 mx-1"></div>

        <!-- Menu do Usuário -->
        <div class="relative" id="user-menu-container">
          <button onclick="toggleUserDropdown()" class="flex items-center gap-2 p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors focus:outline-none">
            <div class="w-9 h-9 bg-emerald-100 text-emerald-700 rounded-full flex items-center justify-center font-bold text-sm shadow-sm">
              MC
            </div>
            <div class="hidden sm:block text-left">
              <p class="text-sm font-medium text-gray-900 dark:text-gray-100 leading-none">Maria Clara</p>
              <span class="text-xs text-gray-500 dark:text-gray-400">Usuario:comum</span>
            </div>
            <i class="ph ph-caret-down text-xs text-gray-500 dark:text-gray-400 ml-1"></i>
          </button>

          <!-- Dropdown Menu -->
          <div id="user-dropdown" class="hidden absolute right-0 mt-2 w-56 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 py-2 z-50">
            <div class="px-4 py-2 border-b border-gray-100 dark:border-gray-700">
              <p class="text-sm font-semibold text-gray-900 dark:text-gray-100">Maria Clara</p>
              <p class="text-xs text-gray-500 dark:text-gray-400 truncate">maria@email.com</p>
            </div>
            <div class="py-1">
              <a href="#" class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <i class="ph ph-gear text-lg text-gray-500 dark:text-gray-400"></i>
                <span>Editar Perfil</span>
              </a>
              <a href="#" class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <i class="ph ph-check-square-offset text-lg text-gray-500 dark:text-gray-400"></i>
                <span>Meu Histórico de Votos</span>
              </a>
            </div>
            <div class="border-t border-gray-100 dark:border-gray-700 my-1"></div>
            <div class="px-1">
              <a href="index.jsp" class="flex items-center gap-2.5 px-3 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors font-medium">
                <i class="ph ph-sign-out text-lg text-red-600 dark:text-red-400"></i>
                <span>Sair da conta</span>
              </a>
            </div>
          </div>
        </div>
      </div>

    </div>
  </header>

  <!-- ================= CONTEÚDO PRINCIPAL ================= -->
  <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-grow w-full">

    <!-- ================= PAINEL DE ESTATÍSTICAS GLOBAIS ================= -->
    <section class="mb-8">
      <h2 class="text-xs font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400 mb-3 flex items-center gap-1.5">
        <i class="ph ph-chart-pie-slice text-indigo-600 dark:text-indigo-400 text-sm"></i> Estatísticas da Plataforma
      </h2>
      
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Card Stat 1 -->
        <div class="bg-white dark:bg-gray-800 p-5 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4 transition-colors">
          <div class="w-12 h-12 bg-indigo-50 dark:bg-indigo-950/50 text-indigo-600 dark:text-indigo-400 rounded-lg flex items-center justify-center text-2xl flex-shrink-0">
            <i class="ph ph-check-fat"></i>
          </div>
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">Total de Votos Registrados</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">12.450</p>
          </div>
        </div>

        <!-- Card Stat 2 -->
        <div class="bg-white dark:bg-gray-800 p-5 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4 transition-colors">
          <div class="w-12 h-12 bg-emerald-50 dark:bg-emerald-950/50 text-emerald-600 dark:text-emerald-400 rounded-lg flex items-center justify-center text-2xl flex-shrink-0">
            <i class="ph ph-user-check"></i>
          </div>
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">Seus Votos Computados</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">18</p>
          </div>
        </div>

        <!-- Card Stat 3 -->
        <div class="bg-white dark:bg-gray-800 p-5 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4 transition-colors">
          <div class="w-12 h-12 bg-amber-50 dark:bg-amber-950/50 text-amber-600 dark:text-amber-400 rounded-lg flex items-center justify-center text-2xl flex-shrink-0">
            <i class="ph ph-lightning"></i>
          </div>
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">Enquetes Ativas</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-gray-100">24</p>
          </div>
        </div>

        <!-- Card Stat 4 -->
        <div class="bg-white dark:bg-gray-800 p-5 rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm flex items-center gap-4 transition-colors">
          <div class="w-12 h-12 bg-purple-50 dark:bg-purple-950/50 text-purple-600 dark:text-purple-400 rounded-lg flex items-center justify-center text-2xl flex-shrink-0">
            <i class="ph ph-trophy"></i>
          </div>
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 font-medium">Categoria Mais Votada</p>
            <p class="text-lg font-bold text-gray-900 dark:text-gray-100 truncate">Tecnologia</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Filtros de Categoria -->
    <section class="mb-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-gray-900 dark:text-gray-100 flex items-center gap-2">
          <i class="ph ph-funnel text-indigo-600 dark:text-indigo-400"></i> Filtrar Por Categoria
        </h2>
      </div>

      <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
        <button class="bg-indigo-600 text-white px-4 py-2 rounded-full text-sm font-medium shadow-sm">Todas</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 px-4 py-2 rounded-full text-sm font-medium transition-colors">Tecnologia</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 px-4 py-2 rounded-full text-sm font-medium transition-colors">Educação</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300 px-4 py-2 rounded-full text-sm font-medium transition-colors">Entretenimento</button>
      </div>
    </section>

    <!-- Enquetes com Estatísticas -->
    <section>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

        <!-- CARD 1 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 flex flex-col justify-between transition-colors">
          <div>
            <div class="flex items-center justify-between mb-3">
              <span class="bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 text-xs font-semibold px-2.5 py-1 rounded-md">Tecnologia</span>
              <span class="bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-xs font-medium px-2.5 py-1 rounded-full">Ativa</span>
            </div>

            <h3 class="font-bold text-gray-900 dark:text-gray-100 text-lg mb-1">Linguagem Backend Preferida</h3>
            <p class="text-gray-600 dark:text-gray-400 text-sm mb-4">Estatísticas acumuladas de todas as respostas registradas nesta enquete:</p>

            <div class="space-y-4 mb-6">
              <div>
                <div class="flex justify-between text-xs font-medium mb-1">
                  <span class="text-gray-900 dark:text-gray-100 font-bold flex items-center gap-1">
                    Node.js (Seu voto) <i class="ph ph-check-circle-fill text-indigo-600 dark:text-indigo-400"></i>
                  </span>
                  <span class="text-indigo-600 dark:text-indigo-400 font-bold">52% (520 votos)</span>
                </div>
                <div class="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2.5">
                  <div class="bg-indigo-600 dark:bg-indigo-500 h-2.5 rounded-full" style="width: 52%"></div>
                </div>
              </div>

              <div>
                <div class="flex justify-between text-xs font-medium mb-1">
                  <span class="text-gray-700 dark:text-gray-300">Python</span>
                  <span class="text-gray-500 dark:text-gray-400">33% (330 votos)</span>
                </div>
                <div class="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2.5">
                  <div class="bg-gray-300 dark:bg-gray-500 h-2.5 rounded-full" style="width: 33%"></div>
                </div>
              </div>

              <div>
                <div class="flex justify-between text-xs font-medium mb-1">
                  <span class="text-gray-700 dark:text-gray-300">Java</span>
                  <span class="text-gray-500 dark:text-gray-400">15% (150 votos)</span>
                </div>
                <div class="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2.5">
                  <div class="bg-gray-300 dark:bg-gray-500 h-2.5 rounded-full" style="width: 15%"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="border-t border-gray-100 dark:border-gray-700 pt-4 flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
            <span class="flex items-center gap-1 font-semibold text-gray-700 dark:text-gray-300">
              <i class="ph ph-users text-indigo-600 dark:text-indigo-400 text-sm"></i> Total: 1.000 votos
            </span>
            <span class="text-xs text-indigo-600 dark:text-indigo-400 font-medium">99.8% de participação</span>
          </div>
        </div>

        <!-- CARD 2 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 flex flex-col justify-between hover:shadow-md transition-all">
          <div>
            <div class="flex items-center justify-between mb-3">
              <span class="bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 text-xs font-semibold px-2.5 py-1 rounded-md">Educação</span>
              <span class="inline-flex items-center gap-1.5 bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-xs font-medium px-2.5 py-1 rounded-full">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                Ativa
              </span>
            </div>

            <h3 class="font-bold text-gray-900 dark:text-gray-100 text-lg mb-1">Formato de Aulas do Próximo Semestre</h3>
            <p class="text-gray-600 dark:text-gray-400 text-sm mb-4">Escolha uma opção para incluir seu voto nas estatísticas:</p>

            <form class="space-y-2 mb-6">
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="opcao2" class="text-indigo-600 focus:ring-indigo-500 dark:bg-gray-700 border-gray-300 dark:border-gray-600 h-4 w-4">
                <span class="ml-3 text-sm font-medium text-gray-700 dark:text-gray-300">100% Presencial</span>
              </label>
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="opcao2" class="text-indigo-600 focus:ring-indigo-500 dark:bg-gray-700 border-gray-300 dark:border-gray-600 h-4 w-4">
                <span class="ml-3 text-sm font-medium text-gray-700 dark:text-gray-300">Híbrido (2x por semana)</span>
              </label>
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="opcao2" class="text-indigo-600 focus:ring-indigo-500 dark:bg-gray-700 border-gray-300 dark:border-gray-600 h-4 w-4">
                <span class="ml-3 text-sm font-medium text-gray-700 dark:text-gray-300">100% Online</span>
              </label>
            </form>
          </div>

          <div class="border-t border-gray-100 dark:border-gray-700 pt-4 flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
            <span class="flex items-center gap-1"><i class="ph ph-chart-bar text-sm"></i> 340 votos já registrados</span>
            <button class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg font-semibold text-xs transition-colors">
              Votar
            </button>
          </div>
        </div>

      </div>
    </section>
  </main>

  <!-- Arquivo JavaScript Externo -->
  <script src="assets/js/script.js"></script>
</body>
</html>