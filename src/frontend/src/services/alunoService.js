import alunoApi from '../api/aluno';
import vantagemApi from '../api/vantagens';

export const alunoService = {
  obterDashboard: (alunoId) => alunoApi.obterDashboard(alunoId),
  listarCupons: (alunoId) => alunoApi.listarCupons(alunoId),
  resgatarVantagem: (vantagemId, alunoId) => vantagemApi.resgatar(vantagemId, alunoId),
  cadastrar: (dados) => alunoApi.cadastrar(dados),
};

export default alunoService;

