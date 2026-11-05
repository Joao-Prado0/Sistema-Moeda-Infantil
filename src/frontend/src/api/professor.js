import { request } from '../services/api';

export const professorApi = {
  buscarPorId: (id) => request(`/professores/${id}`),
  cadastrar: (dados) => request('/professores', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => request(`/professores/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
  deletar: (id) => request(`/professores/${id}`, { method: 'DELETE' }),
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

export default professorApi;
