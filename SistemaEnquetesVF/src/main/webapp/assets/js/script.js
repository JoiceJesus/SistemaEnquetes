// ================= CONFIGURAÇÕES GERAIS =================
if (typeof tailwind !== 'undefined') {
  tailwind.config = {
    darkMode: 'class',
  };
}

// ================= GERENCIAMENTO DE MODO ESCURO (GLOBAL) =================
function toggleDarkMode() {
  const isDark = document.documentElement.classList.toggle('dark');
  localStorage.setItem('theme', isDark ? 'dark' : 'light');
  updateThemeIcon(isDark);
}

function updateThemeIcon(isDark) {
  const themeIcon = document.getElementById('theme-icon');
  if (themeIcon) {
    themeIcon.className = isDark 
      ? "ph ph-sun text-xl text-amber-400" 
      : "ph ph-moon text-xl text-gray-600";
  }
}

// Aplica o tema salvo no carregamento
if (localStorage.getItem('theme') === 'dark') {
  document.documentElement.classList.add('dark');
}

// ================= DROPDOWN DO USUÁRIO (GLOBAL) =================
function toggleUserDropdown() {
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown) dropdown.classList.toggle('hidden');
}

window.addEventListener('click', (e) => {
  const container = document.getElementById('user-menu-container');
  const dropdown = document.getElementById('user-dropdown');
  if (container && dropdown && !container.contains(e.target)) {
    dropdown.classList.add('hidden');
  }
});

// ================= AUTENTICAÇÃO / LOGIN / CADASTRO =================
function switchTab(tab) {
  const formLogin = document.getElementById('form-login');
  const formRegister = document.getElementById('form-register');
  const tabLogin = document.getElementById('tab-login');
  const tabRegister = document.getElementById('tab-register');

  if (!formLogin || !formRegister) return;

  const activeClass = "flex-1 py-3.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400 transition-all";
  const inactiveClass = "flex-1 py-3.5 text-sm font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-all";

  if (tab === 'login') {
    formLogin.classList.remove('hidden');
    formRegister.classList.add('hidden');
    tabLogin.className = activeClass;
    tabRegister.className = inactiveClass;
  } else {
    formLogin.classList.add('hidden');
    formRegister.classList.remove('hidden');
    tabRegister.className = activeClass;
    tabLogin.className = inactiveClass;
  }
}

function toggleModal(show) {
  const modal = document.getElementById('modal-forgot-password');
  if (modal) {
    modal.classList.toggle('hidden', !show);
  }
}

function handleResetPassword(event) {
  event.preventDefault();
  const emailInput = document.getElementById('recovery-email');
  if (emailInput) {
    alert(`Um link de redefinição foi enviado para: ${emailInput.value}`);
    toggleModal(false);
  }
}

// ================= INICIALIZAÇÃO E EVENTOS DOM =================
document.addEventListener('DOMContentLoaded', () => {

  // Sincroniza o ícone do tema ao carregar
  updateThemeIcon(document.documentElement.classList.contains('dark'));

  // Botão de alternar tema (se existir na página)
  const btnThemeToggle = document.getElementById('btn-theme-toggle');
  if (btnThemeToggle) {
    btnThemeToggle.addEventListener('click', toggleDarkMode);
  }

  // Botão do dropdown de usuário (se existir)
  const btnUserDropdown = document.getElementById('btn-user-dropdown');
  if (btnUserDropdown) {
    btnUserDropdown.addEventListener('click', toggleUserDropdown);
  }

  // Modal de Gerenciamento
  const manageModal = document.getElementById('manage-modal');
  const btnOpenManage = document.getElementById('btn-open-manage');
  const btnCloseModal = document.getElementById('btn-close-modal');

  if (manageModal && btnOpenManage && btnCloseModal) {
    btnOpenManage.addEventListener('click', () => manageModal.classList.remove('hidden'));
    btnCloseModal.addEventListener('click', () => manageModal.classList.add('hidden'));
  }

  // Navegação entre Abas do Modal
  const tabBtnList = document.getElementById('tab-btn-list');
  const tabBtnCreate = document.getElementById('tab-btn-create');
  const tabContentList = document.getElementById('tab-content-list');
  const tabContentCreate = document.getElementById('tab-content-create');
  const btnCancelCreate = document.getElementById('btn-cancel-create');

  if (tabBtnList && tabBtnCreate && tabContentList && tabContentCreate) {
    const activeTabClass = "py-3 px-4 text-sm font-semibold border-b-2 border-indigo-600 text-indigo-600 dark:text-indigo-400 flex items-center gap-2 transition-all";
    const inactiveTabClass = "py-3 px-4 text-sm font-semibold border-b-2 border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 flex items-center gap-2 transition-all";

    const switchModalTab = (tab) => {
      const isList = tab === 'list';
      tabContentList.classList.toggle('hidden', !isList);
      tabContentCreate.classList.toggle('hidden', isList);
      tabBtnList.className = isList ? activeTabClass : inactiveTabClass;
      tabBtnCreate.className = isList ? inactiveTabClass : activeTabClass;
    };

    tabBtnList.addEventListener('click', () => switchModalTab('list'));
    tabBtnCreate.addEventListener('click', () => switchModalTab('create'));
    if (btnCancelCreate) {
      btnCancelCreate.addEventListener('click', () => switchModalTab('list'));
    }
  }

  // Opções Dinâmicas do Formulário
  const optionsContainer = document.getElementById('options-container');
  const btnAddOption = document.getElementById('btn-add-option');

  if (optionsContainer && btnAddOption) {
    btnAddOption.addEventListener('click', () => {
      const count = optionsContainer.querySelectorAll('.option-row').length + 1;
      const div = document.createElement('div');
      div.className = "flex items-center gap-2 option-row";
      div.innerHTML = `
        <input type="text" required placeholder="Opção ${count}" class="option-input w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500">
        <button type="button" class="btn-remove-option p-2.5 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40 rounded-lg transition-colors">
          <i class="ph ph-trash text-lg"></i>
        </button>
      `;
      optionsContainer.appendChild(div);
    });

    optionsContainer.addEventListener('click', (e) => {
      const removeBtn = e.target.closest('.btn-remove-option');
      if (removeBtn) {
        const rows = optionsContainer.querySelectorAll('.option-row');
        if (rows.length > 2) {
          removeBtn.closest('.option-row').remove();
        } else {
          alert("Uma enquete precisa de no mínimo 2 opções.");
        }
      }
    });
  }

  // Submissão do Formulário de Enquetes
  const modalPollForm = document.getElementById('modal-poll-form');
  if (modalPollForm) {
    modalPollForm.addEventListener('submit', (e) => {
      e.preventDefault();
      alert("Enquete criada com sucesso!");
      modalPollForm.reset();
      const tabBtnList = document.getElementById('tab-btn-list');
      if (tabBtnList) tabBtnList.click();
    });
  }

  // Edição de Status na Lista
  const myPollsList = document.getElementById('my-polls-list');
  if (myPollsList) {
    myPollsList.addEventListener('click', (e) => {
      const editBtn = e.target.closest('.btn-edit-status');
      if (editBtn) {
        const pollId = editBtn.getAttribute('data-poll-id');
        alert(`Editar configurações/status da enquete ID: ${pollId}`);
      }
    });
  }

});


