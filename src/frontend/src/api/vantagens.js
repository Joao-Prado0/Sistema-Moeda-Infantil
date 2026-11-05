import { request } from '../services/api';

export const vantagemApi = {
  listar: () => request('/vantagens'),
  resgatar: (vantagemId, alunoId) =>
    request(`/vantagens/${vantagemId}/resgate`, {
      method: 'POST',
      headers: alunoId ? { 'X-Aluno-Id': alunoId } : {},
    }),
};

export default vantagemApi;

