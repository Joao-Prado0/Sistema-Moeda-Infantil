import { request } from '../services/api';

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

export default alunoApi;

