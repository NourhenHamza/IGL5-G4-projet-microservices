package tn.esprit.spring.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client de test pour démontrer le Load Balancing via Eureka
 * 
 * Utilise @LoadBalanced RestTemplate pour distribuer automatiquement
 * les appels entre les instances disponibles
 * 
 * @author Votre Nom
 * @version 1.0
 */
@Component
public class LoadBalancingTestClient {

    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    private static final String SERVICE_NAME = "gestion-evenement-service";
    private static final String SERVICE_URL = "http://" + SERVICE_NAME + "/GestionEvenement";

    /**
     * Effectuer un appel via Load Balancer
     * 
     * @return Réponse du service
     */
    public Map<String, Object> callServiceViaLoadBalancer() {
        try {
            // Appel via le nom de service - Eureka fait le Load Balancing
            String url = SERVICE_URL + "/service/info";
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Échec de l'appel: " + e.getMessage());
            return error;
        }
    }

    /**
     * Obtenir la liste des instances disponibles
     * 
     * @return Liste des instances
     */
    public List<ServiceInstance> getAvailableInstances() {
        return discoveryClient.getInstances(SERVICE_NAME);
    }

    /**
     * Tester le Load Balancing avec plusieurs appels
     * 
     * @param numberOfCalls Nombre d'appels à effectuer
     * @return Statistiques de répartition
     */
    public Map<String, Object> testLoadBalancing(int numberOfCalls) {
        Map<String, Integer> instanceHits = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < numberOfCalls; i++) {
            try {
                Map<String, Object> response = callServiceViaLoadBalancer();
                
                if (response.containsKey("port")) {
                    String port = response.get("port").toString();
                    instanceHits.put(port, instanceHits.getOrDefault(port, 0) + 1);
                    successCount++;
                } else {
                    failureCount++;
                }
                
                // Petit délai entre les appels
                Thread.sleep(100);
                
            } catch (Exception e) {
                failureCount++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", numberOfCalls);
        stats.put("successfulCalls", successCount);
        stats.put("failedCalls", failureCount);
        stats.put("instanceHits", instanceHits);
        stats.put("availableInstances", getAvailableInstances().size());

        return stats;
    }

    /**
     * Afficher les informations des instances disponibles
     */
    public void printInstancesInfo() {
        List<ServiceInstance> instances = getAvailableInstances();
        
        System.out.println("==========================================");
        System.out.println("  Instances de " + SERVICE_NAME);
        System.out.println("==========================================");
        System.out.println("Nombre total d'instances: " + instances.size());
        System.out.println();
        
        int count = 1;
        for (ServiceInstance instance : instances) {
            System.out.println("Instance " + count + ":");
            System.out.println("  - ID: " + instance.getInstanceId());
            System.out.println("  - Host: " + instance.getHost());
            System.out.println("  - Port: " + instance.getPort());
            System.out.println("  - URI: " + instance.getUri());
            System.out.println();
            count++;
        }
    }
}