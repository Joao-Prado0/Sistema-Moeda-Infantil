import professorApi from '../api/professor';

export const professorService = {
  obterDashboard: (professorId) => professorApi.obterDashboard(professorId),
  enviarMoedas: (professorId, dados) => professorApi.enviarMoedas(professorId, dados),
  aplicarCreditoSemestral: () => professorApi.aplicarCreditoSemestral(),
};

export default professorService;

