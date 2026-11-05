import professorApi from '../api/professor';

export const professorService = {
  buscarPorId: (id) => professorApi.buscarPorId(id),
  cadastrar: (dados) => professorApi.cadastrar(dados),
  atualizar: (id, dados) => professorApi.atualizar(id, dados),
  deletar: (id) => professorApi.deletar(id),
  obterDashboard: (professorId) => professorApi.obterDashboard(professorId),
  enviarMoedas: (professorId, dados) => professorApi.enviarMoedas(professorId, dados),
  aplicarCreditoSemestral: () => professorApi.aplicarCreditoSemestral(),
};

export default professorService;
