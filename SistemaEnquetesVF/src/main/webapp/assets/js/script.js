// ================= CONFIGURAÇÕES GERAIS =================
if (typeof tailwind !== 'undefined') {
  tailwind.config = { darkMode: 'class' };
}

// ================= TEMA =================
function updateThemeIcon(isDark) {
  const themeIcon = document.getElementById('theme-icon');
  if (themeIcon) themeIcon.className = isDark ? 'ph ph-sun text-xl text-amber-400' : 'ph ph-moon text-xl text-gray-600 dark:text-gray-200';
  const darkIcon = document.getElementById('theme-toggle-dark-icon');
  const lightIcon = document.getElementById('theme-toggle-light-icon');
  if (darkIcon && lightIcon) {
    darkIcon.classList.toggle('hidden', !isDark);
    lightIcon.classList.toggle('hidden', isDark);
  }
}

function toggleDarkMode() {
  const isDark = document.documentElement.classList.toggle('dark');
  localStorage.setItem('theme', isDark ? 'dark' : 'light');
  updateThemeIcon(isDark);
}

if (localStorage.getItem('theme') === 'dark') document.documentElement.classList.add('dark');

// ================= DROPDOWN DO USUÁRIO =================
function toggleUserDropdown() {
  const dropdown = document.getElementById('user-dropdown');
  if (dropdown) dropdown.classList.toggle('hidden');
}

// ================= LOGIN / CADASTRO =================
function switchTab(tab) {
  const formLogin = document.getElementById('form-login');
  const formRegister = document.getElementById('form-register');
  const tabLogin = document.getElementById('tab-login');
  const tabRegister = document.getElementById('tab-register');
  if (!formLogin || !formRegister || !tabLogin || !tabRegister) return;
  const active = 'flex-1 py-3.5 text-sm font-semibold text-indigo-600 dark:text-indigo-400 border-b-2 border-indigo-600 dark:border-indigo-400 transition-all';
  const inactive = 'flex-1 py-3.5 text-sm font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-all';
  const login = tab === 'login';
  formLogin.classList.toggle('hidden', !login);
  formRegister.classList.toggle('hidden', login);
  tabLogin.className = login ? active : inactive;
  tabRegister.className = login ? inactive : active;
}

function toggleModal(show) {
  const modal = document.getElementById('modal-forgot-password');
  if (modal) modal.classList.toggle('hidden', !show);
}

function handleResetPassword(event) {
  event.preventDefault();
  alert('A recuperação automática por e-mail ainda não está configurada neste projeto. Entre em contato com um administrador para redefinir sua senha.');
}

// ================= FILTRO / BUSCA / ORDENAÇÃO =================
let activeCategory = 'all';

function applyPollFilters() {
  const query = (document.getElementById('poll-search')?.value || '').trim().toLowerCase();
  const cards = [...document.querySelectorAll('.poll-card')];
  let visible = 0;
  cards.forEach(card => {
    const title = (card.dataset.title || '').toLowerCase();
    const category = card.dataset.category || '';
    const matchesText = !query || title.includes(query) || category.toLowerCase().includes(query);
    const matchesCategory = activeCategory === 'all' || category === activeCategory;
    const show = matchesText && matchesCategory;
    card.classList.toggle('hidden', !show);
    if (show) visible++;
  });
  const noResults = document.getElementById('no-filter-results');
  if (noResults) noResults.classList.toggle('hidden', visible !== 0 || cards.length === 0);
}

function sortPolls(mode) {
  const grid = document.getElementById('poll-grid');
  if (!grid) return;
  const cards = [...grid.querySelectorAll('.poll-card')];
  cards.sort((a, b) => {
    if (mode === 'votadas') return Number(b.dataset.votes || 0) - Number(a.dataset.votes || 0);
    if (mode === 'encerrar') {
      const da = Date.parse(a.dataset.expiration || '') || Number.MAX_SAFE_INTEGER;
      const db = Date.parse(b.dataset.expiration || '') || Number.MAX_SAFE_INTEGER;
      return da - db;
    }
    const da = Date.parse(a.dataset.created || '') || 0;
    const db = Date.parse(b.dataset.created || '') || 0;
    return db - da;
  });
  cards.forEach(card => grid.appendChild(card));
}

function formatDateBRInput(input) {
  const digits = input.value.replace(/\D/g, '').slice(0, 8);
  let value = digits;
  if (digits.length > 2) value = digits.slice(0, 2) + '/' + digits.slice(2);
  if (digits.length > 4) value = digits.slice(0, 2) + '/' + digits.slice(2, 4) + '/' + digits.slice(4);
  input.value = value;
}

// ================= INICIALIZAÇÃO =================
document.addEventListener('DOMContentLoaded', () => {
  updateThemeIcon(document.documentElement.classList.contains('dark'));

  document.getElementById('btn-theme-toggle')?.addEventListener('click', toggleDarkMode);
  document.getElementById('btn-user-dropdown')?.addEventListener('click', (e) => { e.stopPropagation(); toggleUserDropdown(); });

  window.addEventListener('click', (e) => {
    const container = document.getElementById('user-menu-container');
    const dropdown = document.getElementById('user-dropdown');
    if (container && dropdown && !container.contains(e.target)) dropdown.classList.add('hidden');
  });

  const manageModal = document.getElementById('manage-modal');
  document.getElementById('btn-open-manage')?.addEventListener('click', () => manageModal?.classList.remove('hidden'));
  document.getElementById('btn-close-modal')?.addEventListener('click', () => manageModal?.classList.add('hidden'));
  manageModal?.addEventListener('click', (e) => { if (e.target === manageModal) manageModal.classList.add('hidden'); });

  const tabList = document.getElementById('tab-btn-list');
  const tabCreate = document.getElementById('tab-btn-create');
  const contentList = document.getElementById('tab-content-list');
  const contentCreate = document.getElementById('tab-content-create');
  const active = 'py-3 px-4 text-sm font-semibold border-b-2 border-indigo-600 text-indigo-600 dark:text-indigo-400 flex items-center gap-2 transition-all';
  const inactive = 'py-3 px-4 text-sm font-semibold border-b-2 border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 flex items-center gap-2 transition-all';
  function switchManageTab(mode) {
    if (!contentList || !contentCreate || !tabList || !tabCreate) return;
    const list = mode === 'list';
    contentList.classList.toggle('hidden', !list);
    contentCreate.classList.toggle('hidden', list);
    tabList.className = list ? active : inactive;
    tabCreate.className = list ? inactive : active;
  }
  tabList?.addEventListener('click', () => switchManageTab('list'));
  tabCreate?.addEventListener('click', () => switchManageTab('create'));
  document.getElementById('btn-cancel-create')?.addEventListener('click', () => switchManageTab('list'));

  const optionsContainer = document.getElementById('options-container');
  document.getElementById('btn-add-option')?.addEventListener('click', () => {
    if (!optionsContainer) return;
    const count = optionsContainer.querySelectorAll('.option-row').length + 1;
    const row = document.createElement('div');
    row.className = 'flex items-center gap-2 option-row';
    row.innerHTML = `<input name="opcao" type="text" maxlength="255" placeholder="Opção ${count}" class="option-input w-full px-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-300 dark:border-gray-600 rounded-lg text-sm dark:text-white"><button type="button" class="btn-remove-option p-2.5 text-gray-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40 rounded-lg"><i class="ph ph-trash text-lg"></i></button>`;
    optionsContainer.appendChild(row);
  });
  optionsContainer?.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-remove-option');
    if (!btn) return;
    const rows = optionsContainer.querySelectorAll('.option-row');
    if (rows.length <= 2) return alert('Uma enquete precisa de no mínimo 2 opções.');
    btn.closest('.option-row')?.remove();
  });

  document.getElementById('modal-poll-form')?.addEventListener('submit', (e) => {
    const values = [...document.querySelectorAll('#options-container input[name="opcao"]')].map(i => i.value.trim()).filter(Boolean);
    if (new Set(values.map(v => v.toLowerCase())).size < 2) {
      e.preventDefault();
      alert('Informe pelo menos duas opções de resposta diferentes.');
    }
  });

  document.querySelectorAll('.date-br').forEach(input => input.addEventListener('input', () => formatDateBRInput(input)));

  document.getElementById('poll-search')?.addEventListener('input', applyPollFilters);
  document.querySelectorAll('.category-filter').forEach(btn => btn.addEventListener('click', () => {
    activeCategory = btn.dataset.categoryFilter || 'all';
    document.querySelectorAll('.category-filter').forEach(b => {
      const selected = b === btn;
      b.classList.toggle('bg-indigo-600', selected);
      b.classList.toggle('text-white', selected);
      b.classList.toggle('bg-white', !selected);
      b.classList.toggle('dark:bg-gray-800', !selected);
    });
    applyPollFilters();
  }));
  document.getElementById('poll-sort')?.addEventListener('change', (e) => sortPolls(e.target.value));

  // Em votação múltipla o HTML não marca todos os checkboxes como obrigatórios;
  // a validação correta é "pelo menos um".
  document.querySelectorAll('.poll-vote-form').forEach(form => form.addEventListener('submit', (e) => {
    const checks = form.querySelectorAll('input[type="checkbox"][name="idOpcao"]');
    if (checks.length && ![...checks].some(c => c.checked)) {
      e.preventDefault();
      alert('Selecione pelo menos uma opção para votar.');
    }
  }));
});
