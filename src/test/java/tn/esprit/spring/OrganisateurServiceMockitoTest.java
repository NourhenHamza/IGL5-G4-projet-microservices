package tn.esprit.spring;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.persistence.repositories.OrganisateurRepository;
import tn.esprit.spring.service.classes.OrganisateurService;

@ExtendWith(MockitoExtension.class)
public class OrganisateurServiceMockitoTest {

    @Mock
    private OrganisateurRepository organisateurRepository;

    @InjectMocks
    private OrganisateurService organisateurService;

    @BeforeEach
    void init() {
        // MockitoExtension takes care of initializing @Mock and @InjectMocks
        // MockitoAnnotations.openMocks(this); // not needed with @ExtendWith(MockitoExtension.class)
    }

    @Test
    public void testAddOrganisateur() {
        Organisateur o = new Organisateur();
        o.setNom("Test");
        // make the mock repository return back the same entity and simulate id assignment
        when(organisateurRepository.save(any(Organisateur.class)))
                .thenAnswer(inv -> {
                    Organisateur arg = inv.getArgument(0);
                    if (arg.getIdOrg() == 0) {
                        arg.setIdOrg(1); // simulate generated id
                    }
                    return arg;
                });

        Organisateur result = organisateurService.ajouterOrganisateur(o);
        assertEquals("Test", result.getNom());
    }

    @Test
    public void testRetrieveAllOrganisateurs() {
        List<Organisateur> list = Arrays.asList(new Organisateur(), new Organisateur());
        when(organisateurRepository.findAll()).thenReturn(list);

        List<Organisateur> result = organisateurService.getAllOrganisateurs();
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateOrganisateur() {
        Organisateur o = new Organisateur();
        o.setIdOrg(1);
        o.setNom("OldName");
        // allow save to return the passed entity
        when(organisateurRepository.save(any(Organisateur.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        o.setNom("NewName");

        Organisateur result = organisateurService.modifierOrganisateur(o);
        assertEquals("NewName", result.getNom());
    }

    @Test
    public void testDeleteOrganisateur() {
        organisateurService.supprimerOrganisateur(1);
        verify(organisateurRepository, times(1)).deleteById(1);
    }
}
