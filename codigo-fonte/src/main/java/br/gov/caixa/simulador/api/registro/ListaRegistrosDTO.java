package br.gov.caixa.simulador.api.registro;

import java.util.List;

public class ListaRegistrosDTO {

    public int pagina;
    public int qtdRegistros;
    public int qtdRegistrosPagina;
    public List<RegistroSimulacaoDTO> registros;

}
