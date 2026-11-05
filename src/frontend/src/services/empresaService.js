import empresaApi from '../api/empresa';

export const empresaService = {
  obterVantagens: (empresaId) => empresaApi.listarVantagens(empresaId),
  criarVantagem: (empresaId, dados) => empresaApi.criarVantagem(empresaId, dados),
  editarVantagem: (empresaId, vantagemId, dados) => empresaApi.editarVantagem(empresaId, vantagemId, dados),
  excluirVantagem: (empresaId, vantagemId) => empresaApi.excluirVantagem(empresaId, vantagemId),
  listarCupons: (empresaId) => empresaApi.listarCupons(empresaId),
  conferirCupom: (empresaId, codigo) => empresaApi.conferirCupom(empresaId, codigo),
  cadastrar: (dados) => empresaApi.cadastrar(dados),
};

export default empresaService;

