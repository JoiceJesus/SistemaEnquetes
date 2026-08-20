<!DOCTYPE html>
<html lang="pt-BR" id="html-root">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Sistema de Enquetes - Dashboard</title>
  
  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      darkMode: 'class',
    }
  </script>
  
  <!-- Ícones Phosphor -->
  <script src="https://unpkg.com/@phosphor-icons/web"></script>
  
  <!-- Estilos Customizados -->
  <link rel="stylesheet" href="assets/css/style.css">
</head>
<body class="bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 antialiased font-sans min-h-screen flex flex-col transition-colors duration-300">

  <!-- ================= HEADER ================= -->
  <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      
      <!-- Logo -->
      <a href="index.jsp" class="flex items-center gap-2">
        <img src="assets/img/logo.png" alt="Enquete Online Logo" class="h-20 w-auto object-contain">
      </a>

      <!-- Campo de Busca -->
      <div class="hidden md:flex items-center flex-1 max-w-md mx-8">
        <div class="relative w-full">
          <i class="ph ph-magnifying-glass absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-lg"></i>
          <input type="text" placeholder="Buscar enquetes..." class="w-full pl-10 pr-4 py-2 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all">
        </div>
      </div>

      <!-- Ações do Usuário -->
      <div class="flex items-center gap-3">
        
        <!-- Alternador de Tema -->
        <button id="btn-theme-toggle" title="Alternar Tema" class="p-2 rounded-lg bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-200 hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors">
          <i id="theme-icon" class="ph ph-moon text-xl"></i>
        </button>

        <!-- Botão de Gerenciamento -->
        <button id="btn-open-manage" class="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-semibold flex items-center gap-2 transition-colors shadow-sm">
          <i class="ph ph-plus-circle text-lg"></i>
          <span class="hidden sm:inline">Gerenciar Enquetes</span>
        </button>

        <div class="h-6 w-px bg-gray-200 dark:bg-gray-700 mx-1"></div>

        <!-- Menu do Perfil -->
        <div class="relative" id="user-menu-container">
          <button id="btn-user-dropdown" class="flex items-center gap-2 p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors focus:outline-none">
            <div class="w-9 h-9 bg-indigo-100 text-indigo-700 dark:bg-indigo-900/60 dark:text-indigo-300 rounded-full flex items-center justify-center font-bold text-sm shadow-sm">
              JD
            </div>
            <div class="hidden sm:block text-left">
              <p class="text-sm font-medium leading-none">João da Silva</p>
              <span class="text-xs text-gray-500 dark:text-gray-400">Criador</span>
            </div>
            <i class="ph ph-caret-down text-xs text-gray-500 ml-1"></i>
          </button>

          <div id="user-dropdown" class="hidden absolute right-0 mt-2 w-56 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 py-2 z-50">
            <div class="px-4 py-2 border-b border-gray-100 dark:border-gray-700">
              <p class="text-sm font-semibold">João da Silva</p>
              <p class="text-xs text-gray-500 dark:text-gray-400 truncate">joao@email.com</p>
            </div>
            <div class="py-1">
              <a href="#" class="flex items-center gap-2.5 px-4 py-2 text-sm hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <i class="ph ph-gear text-lg text-gray-500"></i>
                <span>Configurações</span>
              </a>
              <a href="#" class="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                <i class="ph ph-check-square-offset text-lg text-gray-500"></i>
                <span>Meu Histórico de Votos</span>
              </a>
            </div>
            <div class="border-t border-gray-100 dark:border-gray-700 my-1"></div>
            <div class="px-1">
              <a href="index.jsp" class="flex items-center gap-2.5 px-3 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40 rounded-lg transition-colors font-medium">
                <i class="ph ph-sign-out text-lg"></i>
                <span>Sair da conta</span>
              </a>
            </div>
          </div>
        </div>

      </div>
    </div>
  </header>

  <!-- ================= CONTEÚDO PRINCIPAL ================= -->
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-4 flex gap-3">
  <a href="enquete?acao=listar" class="text-sm text-indigo-600 font-semibold">Enquetes</a>
  <a href="categoria?acao=listar" class="text-sm text-indigo-600 font-semibold">Categorias</a>
  <a href="usuario?acao=listar" class="text-sm text-indigo-600 font-semibold">Usuários</a>
</div>

<main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-grow w-full">

    <!-- Hero / Banner -->
    <div class="bg-gradient-to-r from-indigo-600 to-violet-600 rounded-2xl p-6 sm:p-8 text-white mb-8 shadow-lg">
      <h1 class="text-2xl sm:text-3xl font-bold mb-2">Dê sua opinião e participe das decisões</h1>
      <p class="text-indigo-100 max-w-2xl text-sm sm:text-base">Vote nas enquetes ativas da comunidade ou crie a sua própria votação para obter respostas em tempo real.</p>
    </div>

    <!-- Filtros de Categorias -->
    <section class="mb-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold flex items-center gap-2">
          <i class="ph ph-squares-four text-indigo-600 dark:text-indigo-400"></i> Categorias
        </h2>
      </div>

      <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
        <button class="bg-indigo-600 text-white px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap shadow-sm">Todas</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors">Tecnologia</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors">Educação</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors">Entretenimento</button>
        <button class="bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-700 px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors">Esportes</button>
      </div>
    </section>

    <!-- Listagem de Enquetes -->
    <section>
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-bold">Enquetes em Destaque</h2>
        <select class="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 text-sm rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-indigo-500">
          <option>Mais recentes</option>
          <option>Mais votadas</option>
          <option>Prestes a encerrar</option>
        </select>
      </div>

      <!-- Grid de Enquetes -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

        <!-- Enquete 1 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 flex flex-col justify-between hover:shadow-md transition-all">
          <div>
            <div class="flex items-center justify-between mb-3">
              <span class="bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 text-xs font-semibold px-2.5 py-1 rounded-md">Tecnologia</span>
              <span class="inline-flex items-center gap-1.5 bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-xs font-medium px-2.5 py-1 rounded-full">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                Ativa
              </span>
            </div>
            <h3 class="font-bold text-lg mb-1">Qual a melhor linguagem para desenvolvimento Backend em 2026?</h3>
            <p class="text-gray-600 dark:text-gray-300 text-sm mb-4">Pesquisa rápida sobre as preferências da comunidade de desenvolvedores.</p>

            <form class="space-y-2 mb-6">
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="enquete_1" class="text-indigo-600 focus:ring-indigo-500 h-4 w-4">
                <span class="ml-3 text-sm font-medium">Node.js / TypeScript</span>
              </label>
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="enquete_1" class="text-indigo-600 focus:ring-indigo-500 h-4 w-4">
                <span class="ml-3 text-sm font-medium">Python</span>
              </label>
              <label class="flex items-center p-3 border border-gray-200 dark:border-gray-700 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
                <input type="radio" name="enquete_1" class="text-indigo-600 focus:ring-indigo-500 h-4 w-4">
                <span class="ml-3 text-sm font-medium">Go (Golang)</span>
              </label>
            </form>
          </div>

          <div class="border-t border-gray-100 dark:border-gray-700 pt-4 flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
            <div class="flex items-center gap-1">
              <i class="ph ph-clock"></i>
              <span>Encerra em 2 dias</span>
            </div>
            <button class="bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1.5 rounded-lg font-semibold text-xs transition-colors">Votar</button>
          </div>
        </div>

        <!-- Enquete 2 -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 flex flex-col justify-between hover:shadow-md transition-all">
          <div>
            <div class="flex items-center justify-between mb-3">
              <span class="bg-amber-50 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 text-xs font-semibold px-2.5 py-1 rounded-md">Educação</span>
              <span class="inline-flex items-center gap-1.5 bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 text-xs font-medium px-2.5 py-1 rounded-full">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                Ativa
              </span>
            </div>

            <h3 class="font-bold text-lg mb-1">Modelo de trabalho preferido após a faculdade?</h3>
            <p class="text-gray-600 dark:text-gray-300 text-sm mb-4">Queremos saber a opinião dos formandos sobre a jornada de trabalho.</p>

            <div class="space-y-3 mb-6">
              <div>
                <div class="flex justify-between text-xs font-medium mb-1">
                  <span>Remoto</span>
                  <span class="text-gray-500 dark:text-gray-400">65% (130 votos)</span>
                </div>
                <div class="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2">
                  <div class="bg-indigo-600 h-2 rounded-full" style="width: 65%"></div>
                </div>
              </div>
              <div>
                <div class="flex justify-between text-xs font-medium mb-1">
                  <span>Híbrido</span>
                  <span class="text-gray-500 dark:text-gray-400">25% (50 votos)</span>
                </div>
                <div class="w-full bg-gray-100 dark:bg-gray-700 rounded-full h-2">
                  <div class="bg-indigo-600 h-2 rounded-full" style="width: 25%"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="border-t border-gray-100 dark:border-gray-700 pt-4 flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
            <span class="flex items-center gap-1">
              <i class="ph ph-check-circle text-emerald-600 text-sm"></i> Você já votou
            </span>
            <span class="font-medium">Total: 200 votos</span>
          </div>
        </div>

      </div>
    </section>
  </main>

  <!-- ================= POP-UP / MODAL ================= -->
  <div id="manage-modal" class="fixed inset-0 bg-gray-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4 hidden">
    <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700 w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-200">
      
      <!-- Cabeçalho do Modal -->
      <div class="p-6 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between bg-gray-50 dark:bg-gray-800/50">
        <div>
          <h2 class="text-xl font-bold flex items-center gap-2">
            <i class="ph ph-sliders-horizontal text-indigo-600 dark:text-indigo-400"></i> Central de Gerenciamento de Enquetes
          </h2>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">Crie novas votações ou gerencie as enquetes já existentes.</p>
        </div>
        <button id="btn-close-modal" class="p-2 rounded-lg text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
          <i class="ph ph-x text-xl"></i>
        </button>
      </div>

      <!-- Abas de Navegação -->
      <div class="flex border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-6">
        <button id="tab-btn-list" class="tab-btn active py-3 px-4 text-sm font-semibold border-b-2 border-indigo-600 text-indigo-600 dark:text-indigo-400 flex items-center gap-2 transition-all">
          <i class="ph ph-list-bullets text-lg"></i> Enquetes Criadas
        </button>
        <button id="tab-btn-create" class="tab-btn inactive py-3 px-4 text-sm font-semibold border-b-2 border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 flex items-center gap-2 transition-all">
          <i class="ph ph-plus-circle text-lg"></i> Criar Nova Enquete
        </button>
      </div>

      <!-- Corpo do Modal -->
      <div class="p-6 overflow-y-auto flex-grow space-y-6">

        <!-- ABA 1: LISTAGEM E EDIÇÃO -->
        <div id="tab-content-list" class="space-y-4">
          <div class="flex justify-between items-center mb-2">
            <span class="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Suas Enquetes Cadastradas</span>
            <span class="text-xs bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 font-bold px-2.5 py-1 rounded-full">3 Enquetes</span>
          </div>

          <div class="space-y-3" id="my-polls-list">
            
            <!-- Item Enquete 1 -->
            <div class="bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 p-4 rounded-xl flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div class="space-y-1">
                <div class="flex items-center gap-2">
                  <span class="bg-indigo-100 dark:bg-indigo-900/60 text-indigo-700 dark:text-indigo-300 text-xs font-bold px-2 py-0.5 rounded">Tecnologia</span>
                  <span class="text-xs text-emerald-600 dark:text-emerald-400 font-semibold flex items-center gap-1">● Em Votação</span>
                </div>
                <h4 class="font-bold text-sm">Qual a melhor linguagem para desenvolvimento Backend em 2026?</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">Criada em 20/07/2026 • 200 votos computados</p>
              </div>
              <div class="flex items-center gap-2 self-end sm:self-center">
                <button data-poll-id="1" class="btn-edit-status p-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-600 rounded-lg text-xs font-medium hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
                  <i class="ph ph-pencil-simple text-base text-indigo-600"></i> Editar Status
                </button>
              </div>
            </div>

            <!-- Item Enquete 2 -->
            <div class="bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 p-4 rounded-xl flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div class="space-y-1">
                <div class="flex items-center gap-2">
                  <span class="bg-amber-100 dark:bg-amber-900/60 text-amber-700 dark:text-amber-300 text-xs font-bold px-2 py-0.5 rounded">Educação</span>
                  <span class="text-xs text-emerald-600 dark:text-emerald-400 font-semibold flex items-center gap-1">● Em Votação</span>
                </div>
                <h4 class="font-bold text-sm">Modelo de trabalho preferido após a faculdade?</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">Criada em 18/07/2026 • 150 votos computados</p>
              </div>
              <div class="flex items-center gap-2 self-end sm:self-center">
                <button data-poll-id="2" class="btn-edit-status p-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-600 rounded-lg text-xs font-medium hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
                  <i class="ph ph-pencil-simple text-base text-indigo-600"></i> Editar Status
                </button>
              </div>
            </div>

            <!-- Item Enquete 3 -->
            <div class="bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 p-4 rounded-xl flex flex-col sm:flex-row sm:items-center justify-between gap-4 opacity-75">
              <div class="space-y-1">
                <div class="flex items-center gap-2">
                  <span class="bg-purple-100 dark:bg-purple-900/60 text-purple-700 dark:text-purple-300 text-xs font-bold px-2 py-0.5 rounded">Entretenimento</span>
                  <span class="text-xs text-gray-500 font-semibold">Encerrada</span>
                </div>
                <h4 class="font-bold text-sm">Melhor filme lançado no último mês?</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">Encerrada em 10/07/2026 • 320 votos</p>
              </div>
              <div class="flex items-center gap-2 self-end sm:self-center">
                <button data-poll-id="3" class="btn-edit-status p-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-600 rounded-lg text-xs font-medium hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
                  <i class="ph ph-pencil-simple text-base text-indigo-600"></i> Reabrir
                </button>
              </div>
            </div>

          </div>
        </div>

        <!-- ABA 2: FORMULÁRIO DE CRIAÇÃO -->
        <div id="tab-content-create" class="hidden">
          <form id="modal-poll-form" class="space-y-6">
            
            <!-- Título -->
            <div>
              <label for="poll-title" class="block text-xs font-semibold uppercase tracking-wider mb-2">Título da Enquete *</label>
              <input type="text" id="poll-title" required placeholder="Ex: Qual o melhor framework frontend para 2026?" class="w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all">
            </div>

            <!-- Descrição -->
            <div>
              <label for="poll-desc" class="block text-xs font-semibold uppercase tracking-wider mb-2">Descrição *</label>
              <textarea id="poll-desc" required rows="2" placeholder="Forneça um contexto para os eleitores..." class="w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all resize-none"></textarea>
            </div>

            <!-- Categoria e Tipo -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="poll-category" class="block text-xs font-semibold uppercase tracking-wider mb-2">Categoria *</label>
                <select id="poll-category" class="w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:text-white">
                  <option value="Tecnologia">Tecnologia</option>
                  <option value="Educação">Educação</option>
                  <option value="Entretenimento">Entretenimento</option>
                  <option value="Esportes">Esportes</option>
                </select>
              </div>

              <div>
                <label for="poll-type" class="block text-xs font-semibold uppercase tracking-wider mb-2">Tipo de Escolha *</label>
                <select id="poll-type" class="w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:text-white">
                  <option value="single">Voto Único (Radio Button)</option>
                  <option value="multiple">Voto Múltiplo (Checkbox)</option>
                </select>
              </div>
            </div>

            <hr class="border-gray-200 dark:border-gray-700">

            <!-- Opções Dinâmicas -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-xs font-semibold uppercase tracking-wider">Opções de Resposta *</label>
                <span class="text-xs text-gray-400">Mínimo de 2 opções</span>
              </div>

              <div id="options-container" class="space-y-3">
                <div class="flex items-center gap-2 option-row">
                  <input type="text" required placeholder="Opção 1" class="option-input w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
                  <button type="button" class="btn-remove-option p-2.5 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40 rounded-lg transition-colors">
                    <i class="ph ph-trash text-lg"></i>
                  </button>
                </div>

                <div class="flex items-center gap-2 option-row">
                  <input type="text" required placeholder="Opção 2" class="option-input w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
                  <button type="button" class="btn-remove-option p-2.5 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40 rounded-lg transition-colors">
                    <i class="ph ph-trash text-lg"></i>
                  </button>
                </div>
              </div>

              <button type="button" id="btn-add-option" class="mt-3 inline-flex items-center gap-1.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 hover:underline focus:outline-none">
                <i class="ph ph-plus-circle text-lg"></i>
                <span>Adicionar Opção</span>
              </button>
            </div>

            <hr class="border-gray-200 dark:border-gray-700">

            <!-- Agendamento -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="poll-start" class="block text-xs font-semibold uppercase tracking-wider mb-1.5">Data de Início *</label>
                <input type="datetime-local" id="poll-start" required class="w-full px-3 py-2 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
              </div>
              <div>
                <label for="poll-end" class="block text-xs font-semibold uppercase tracking-wider mb-1.5">Data de Término *</label>
                <input type="datetime-local" id="poll-end" required class="w-full px-3 py-2 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
              </div>
            </div>

            <!-- Botões do Formulário -->
            <div class="flex gap-3 pt-4 border-t border-gray-200 dark:border-gray-700">
              <button type="button" id="btn-cancel-create" class="w-1/2 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200 font-semibold py-3 px-4 rounded-lg text-sm transition-colors">
                Cancelar
              </button>
              <button type="submit" class="w-1/2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-4 rounded-lg text-sm shadow-sm transition-colors flex items-center justify-center gap-2">
                <i class="ph ph-check-circle text-lg"></i>
                <span>Salvar e Publicar</span>
              </button>
            </div>

          </form>
        </div>

      </div>

    </div>
  </div>

  <!-- Footer -->
  <footer class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 py-6 mt-12 transition-colors">
    <div class="max-w-7xl mx-auto px-4 text-center text-xs text-gray-500 dark:text-gray-400">
      <p>&copy; 2026 Enquete Online. Todos os direitos reservados.</p>
    </div>
  </footer>

  <!-- Script JS -->
  <script src="assets/js/script.js"></script>
</body>
</html>