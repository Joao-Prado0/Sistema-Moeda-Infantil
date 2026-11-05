import { request } from '../services/api';

export const instituicaoApi = {
  listar: () => request('/instituicoes'),
  listarCursos: () => request('/instituicoes/cursos'),
};

export default instituicaoApi;

