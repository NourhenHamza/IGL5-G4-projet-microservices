package tn.esprit.spring;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional; // Added this import

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.persistence.repositories.OrganisateurRepository;
import tn.esprit.spring.service.classes.OrganisateurService;

import javax.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrganisateurServiceMockitoTest {

    @Mock
    private OrganisateurRepository organisateurRepository;

    @InjectMocks
    private OrganisateurService organisateurService;

    @BeforeEach
    void init() {
        // MockitoExtension takes care of initializing @Mock and @InjectMocks
    }

    @Test
    public void testAddOrganisateur() {
        Organisateur o = new Organisateur();
        o.setNom("TestName");
        o.setPrenom("TestPrenom");
        o.setEmail("test@example.com");

        // Mock the save to simulate the database assigning an ID (1)
        when(organisateurRepository.save(any(Organisateur.class)))
                .thenAnswer(inv -> {
                    Organisateur arg = inv.getArgument(0);
                    // Simulate ID generation only if it's a new entity (ID=0)
                    if (arg.getIdOrg() == 0) {
                        arg.setIdOrg(1); 
                    }
                    return arg;
                });

        Organisateur result = organisateurService.ajouterOrganisateur(o);
        assertEquals("TestName", result.getNom());
        assertEquals(1, result.getIdOrg());
        verify(organisateurRepository, times(1)).save(o);
    }

    @Test
    public void testRetrieveAllOrganisateurs() {
        Organisateur o1 = new Organisateur();
        o1.setIdOrg(1);
        o1.setNom("O1");

        Organisateur o2 = new Organisateur();
        o2.setIdOrg(2);
        o2.setNom("O2");

        List<Organisateur> list = Arrays.asList(o1, o2);
        when(organisateurRepository.findAll()).thenReturn(list);

        List<Organisateur> result = organisateurService.getAllOrganisateurs();
        assertEquals(2, result.size());
        assertEquals("O1", result.get(0).getNom());
        verify(organisateurRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateOrganisateur_Success() {
        int organizerId = 1;

        // 1. Mock the existing entity the service will retrieve (before update)
        Organisateur existing = new Organisateur();
        existing.setIdOrg(organizerId);
        existing.setNom("OldName");
        existing.setPrenom("OldPrenom");
        existing.setEmail("old@example.com");

        // 2. Mock the input entity with new values
        Organisateur updateInput = new Organisateur();
        updateInput.setIdOrg(organizerId);
        updateInput.setNom("NewName");
        updateInput.setPrenom("NewPrenom"); 
        updateInput.setEmail("new@example.com"); 

        // *** CRITICAL FIX: Mock the findById call to return the existing entity ***
        when(organisateurRepository.findById(organizerId)).thenReturn(Optional.of(existing));

        // 3. Mock the save call (to return the modified entity after the service applies changes)
        when(organisateurRepository.save(any(Organisateur.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Execute the service method
        Organisateur result = organisateurService.modifierOrganisateur(updateInput);

        // Assertions
        assertEquals("NewName", result.getNom());
        assertEquals("NewPrenom", result.getPrenom());
        assertEquals("new@example.com", result.getEmail());

        // Verification
        verify(organisateurRepository, times(1)).findById(organizerId);
        verify(organisateurRepository, times(1)).save(any(Organisateur.class));
    }
    
    @Test
    public void testUpdateOrganisateur_NotFound() {
        int nonExistentId = 99; 
        Organisateur input = new Organisateur();
        input.setIdOrg(nonExistentId);
        input.setNom("Name");

        // Mock the findById to return empty, triggering the EntityNotFoundException
        when(organisateurRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Assert that the specific EntityNotFoundException is thrown
        assertThrows(EntityNotFoundException.class, () -> 
            organisateurService.modifierOrganisateur(input)
        );

        // Verify that save was NOT called
        verify(organisateurRepository, never()).save(any());
    }

    @Test
    public void testUpdateOrganisateur_InvalidId() {
        int invalidId = 0; 
        Organisateur input = new Organisateur();
        input.setIdOrg(invalidId);
        input.setNom("Name");

        // Assert that the specific IllegalArgumentException is thrown (due to service logic check for ID == 0)
        assertThrows(IllegalArgumentException.class, () -> 
            organisateurService.modifierOrganisateur(input)
        );

        // Verify that findById and save were NOT called
        verify(organisateurRepository, never()).findById(anyInt());
        verify(organisateurRepository, never()).save(any());
    }

    @Test
    public void testDeleteOrganisateur() {
        // Mock the repository's deleteById method to do nothing when called with ID 1
        doNothing().when(organisateurRepository).deleteById(1);

        organisateurService.supprimerOrganisateur(1);
        
        // Verify that the deleteById method was called exactly once with ID 1
        verify(organisateurRepository, times(1)).deleteById(1);
    }
    
    @Test
    public void testRetrieveOrganisateurById_Found() {
        int organizerId = 5;
        Organisateur expected = new Organisateur();
        expected.setIdOrg(organizerId);
        expected.setNom("Test Organisateur");

        // Mock the findById to return the entity
        when(organisateurRepository.findById(organizerId)).thenReturn(Optional.of(expected));
        
        Optional<Organisateur> result = organisateurService.getOrganisateurById(organizerId);
        
        assertTrue(result.isPresent());
        assertEquals(organizerId, result.get().getIdOrg());
        verify(organisateurRepository, times(1)).findById(organizerId);
    }
    
    @Test
    public void testRetrieveOrganisateurById_NotFound() {
        int organizerId = 6;
        
        // Mock the findById to return empty
        when(organisateurRepository.findById(organizerId)).thenReturn(Optional.empty());
        
        Optional<Organisateur> result = organisateurService.getOrganisateurById(organizerId);
        
        assertFalse(result.isPresent());
        verify(organisateurRepository, times(1)).findById(organizerId);
    }
}