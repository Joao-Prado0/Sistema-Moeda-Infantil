const API_BASE = '/api';

export async function request(endpoint, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
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

export const alunoApi = {
  listar: () => request('/alunos'),
  buscarPorId: (id) => request(`/alunos/${id}`),
  cadastrar: (dados) => request('/alunos', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => request(`/alunos/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
  deletar: (id) => request(`/alunos/${id}`, { method: 'DELETE' }),
};

export const empresaApi = {
  listar: () => request('/empresas'),
  buscarPorId: (id) => request(`/empresas/${id}`),
  cadastrar: (dados) => request('/empresas', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => request(`/empresas/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
  deletar: (id) => request(`/empresas/${id}`, { method: 'DELETE' }),
};

export const instituicaoApi = {
  listar: () => request('/instituicoes'),
  listarCursos: () => request('/instituicoes/cursos'),
};

