import vantagemApi from '../api/vantagens';

export const vantagemService = {
  listarTodas: () => vantagemApi.listar(),
  resgatar: (vantagemId, alunoId) => vantagemApi.resgatar(vantagemId, alunoId),
};

export default vantagemService;

