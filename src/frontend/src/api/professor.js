import { request } from '../services/api';

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

export default professorApi;

