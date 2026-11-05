import { request } from '../services/api';

export const instituicaoApi = {
  listar: () => request('/instituicoes'),
  listarCursos: () => request('/instituicoes/cursos'),
  listarDepartamentos: () => request('/departamentos'),
};

export default instituicaoApi;

