import { request } from '../services/api';

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

export default empresaApi;

