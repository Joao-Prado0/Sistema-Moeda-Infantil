const API_BASE = '/api';

function getStoredUser() {
  try {
    const raw = localStorage.getItem('usuario_logado');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export async function request(endpoint, options = {}) {
  const user = getStoredUser();
  const authHeaders = {};

  if (user?.id) {
    if (user.perfil === 'ALUNO') authHeaders['X-Aluno-Id'] = user.id;
    if (user.perfil === 'PROFESSOR') authHeaders['X-Professor-Id'] = user.id;
    if (user.perfil === 'EMPRESA') authHeaders['X-Empresa-Id'] = user.id;
  }
  if (user?.token) {
    authHeaders['Authorization'] = user.token;
  }

  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders,
      ...options.headers,
    },
    ...options,
  };

  const response = await fetch(`${API_BASE}${endpoint}`, config);

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const errorMsg = data?.message || data?.error || 'Erro na requisição ao servidor';
    throw new Error(errorMsg);
  }

  return data;
}

export const authApi = {
  login: (credenciais) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credenciais),
    }),
};

export const alunoApi = {
  listar: () => request('/alunos'),
  buscarPorId: (id) => request(`/alunos/${id}`),
  cadastrar: (dados) => request('/alunos', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => request(`/alunos/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
  deletar: (id) => request(`/alunos/${id}`, { method: 'DELETE' }),
  obterDashboard: (alunoId) =>
    request('/alunos/me/dashboard', {
      headers: alunoId ? { 'X-Aluno-Id': alunoId } : {},
    }),
  listarCupons: (alunoId) =>
    request('/alunos/me/cupons', {
      headers: alunoId ? { 'X-Aluno-Id': alunoId } : {},
    }),
};

export const professorApi = {
  obterDashboard: (professorId) =>
    request('/professores/me/dashboard', {
      headers: professorId ? { 'X-Professor-Id': professorId } : {},
    }),
  enviarMoedas: (professorId, dados) =>
    request('/professores/me/envios', {
      method: 'POST',
      headers: professorId ? { 'X-Professor-Id': professorId } : {},
      body: JSON.stringify(dados),
    }),
  aplicarCreditoSemestral: () =>
    request('/professores/credito-semestral', {
      method: 'POST',
    }),
};

export const vantagemApi = {
  listar: () => request('/vantagens'),
  resgatar: (vantagemId, alunoId) =>
    request(`/vantagens/${vantagemId}/resgate`, {
      method: 'POST',
      headers: alunoId ? { 'X-Aluno-Id': alunoId } : {},
    }),
};

export const empresaApi = {
  listar: () => request('/empresas'),
  buscarPorId: (id) => request(`/empresas/${id}`),
  cadastrar: (dados) => request('/empresas', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => request(`/empresas/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
  deletar: (id) => request(`/empresas/${id}`, { method: 'DELETE' }),
  listarVantagens: (empresaId) =>
    request('/empresas/me/vantagens', {
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
    }),
  criarVantagem: (empresaId, dados) =>
    request('/empresas/me/vantagens', {
      method: 'POST',
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
      body: JSON.stringify(dados),
    }),
  editarVantagem: (empresaId, vantagemId, dados) =>
    request(`/empresas/me/vantagens/${vantagemId}`, {
      method: 'PUT',
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
      body: JSON.stringify(dados),
    }),
  excluirVantagem: (empresaId, vantagemId) =>
    request(`/empresas/me/vantagens/${vantagemId}`, {
      method: 'DELETE',
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
    }),
  listarCupons: (empresaId) =>
    request('/empresas/me/cupons', {
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
    }),
  conferirCupom: (empresaId, codigo) =>
    request('/empresas/me/cupons/conferir', {
      method: 'POST',
      headers: empresaId ? { 'X-Empresa-Id': empresaId } : {},
      body: JSON.stringify({ codigo }),
    }),
};

export const instituicaoApi = {
  listar: () => request('/instituicoes'),
  listarCursos: () => request('/instituicoes/cursos'),
};
