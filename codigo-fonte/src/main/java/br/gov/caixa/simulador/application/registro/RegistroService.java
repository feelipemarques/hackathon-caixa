package br.gov.caixa.simulador.application.registro;

import br.gov.caixa.simulador.api.registro.ListaRegistrosDTO;
import br.gov.caixa.simulador.api.registro.RegistroSimulacaoDTO;
import br.gov.caixa.simulador.domain.registrodb.Registro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegistroService {

    @Transactional
    public void persistRegistro(RegistroSimulacaoDTO registroDTO, int codigoProduto){
        Registro registro = new Registro();

        registro.idSimulacao = registroDTO.idSimulacao;
        registro.prazo = registroDTO.prazo;
        registro.valorDesejado = registroDTO.valorDesejado;
        registro.valorTotalParcelasSAC = registroDTO.valorTotalParcelasSAC;
        registro.valorTotalParcelasPRICE = registroDTO.valorTotalParcelasPRICE;
        registro.codigoProduto = codigoProduto;
        registro.persist();
    }

    public ListaRegistrosDTO listarSimulacoesPaginadas(int pagina, int qtdRegistrosPagina) {
        ListaRegistrosDTO lista = new ListaRegistrosDTO();
        lista.pagina = pagina;
        lista.qtdRegistrosPagina = qtdRegistrosPagina;

        lista.qtdRegistros = (int) Registro.count();

        int offset = (pagina - 1) * qtdRegistrosPagina;

        List<Registro> registros = Registro.findAll()
                .page(offset / qtdRegistrosPagina, qtdRegistrosPagina)
                .list();

        lista.registros = registros.stream()
                .map(RegistroSimulacaoDTO::fromEntity)
                .collect(Collectors.toList());

        return lista;
    }

}
