package br.gov.caixa.simulador.application.simulacao;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.gov.caixa.simulador.api.simulacao.SimulacaoResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Arrays;

@ApplicationScoped
public class EventHubService {

    @Inject
    ObjectMapper mapper;

    @ConfigProperty(name = "eventhub.connection-string")
    String connectionString;

    public void sendJson(SimulacaoResponseDTO simulacao){
        try{
            String json = mapper.writeValueAsString(simulacao);

            EventHubProducerClient producer = new EventHubClientBuilder()
                    .connectionString(connectionString)
                    .buildProducerClient();

            EventData event = new EventData(json);
            producer.send(Arrays.asList(event));
            producer.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
