package tn.esprit.spring.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tn.esprit.spring.dto.EvenementDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EvenementClientFallback implements EvenementClient {

    @Override
    public List<EvenementDTO> getAllEvenements() {
        log.error("ERREUR : Service evenement-service non disponible");
        return new ArrayList<>();
    }

    @Override
    public EvenementDTO getEvenementById(Long id) {
        log.error("ERREUR : Timeout lors de l appel a evenement-service pour l ID {}", id);
        
        EvenementDTO fallbackDTO = new EvenementDTO();
        fallbackDTO.setId(id);
        fallbackDTO.setDescription("Service temporairement indisponible");
        return fallbackDTO;
    }

    @Override
    public EvenementDTO createEvenement(EvenementDTO evenementDTO) {
        log.error("ERREUR 404 : Endpoint inexistant");
        return null;
    }

    @Override
    public String healthCheck() {
        log.error("ERREUR : Service evenement-service inaccessible");
        return "Service Evenement indisponible";
    }
}