package tn.esprit.spring.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tn.esprit.spring.dto.EvenementDTO;

import java.util.List;

@FeignClient(
    name = "evenement-service",
    fallback = EvenementClientFallback.class
)
public interface EvenementClient {

    @GetMapping("/evenements")
    List<EvenementDTO> getAllEvenements();

    @GetMapping("/evenements/{id}")
    EvenementDTO getEvenementById(@PathVariable("id") Long id);

    @PostMapping("/evenements/create-wrong-endpoint")
    EvenementDTO createEvenement(@RequestBody EvenementDTO evenementDTO);

    @GetMapping("/evenements/health")
    String healthCheck();
}