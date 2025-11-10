package tn.esprit.spring.dto;

import java.io.Serializable;

/**
 * DTO for Salle entity
 * Used to transfer data between controller and service layers
 * This prevents exposing JPA entities directly in the API
 */
public class SalleDTO implements Serializable {

    private Integer idSalle;
    private String nom;
    private String adresse;
    private int capacite;
    private boolean disponible;

    public SalleDTO() {
    }

    public SalleDTO(Integer idSalle, String nom, String adresse, int capacite, boolean disponible) {
        this.idSalle = idSalle;
        this.nom = nom;
        this.adresse = adresse;
        this.capacite = capacite;
        this.disponible = disponible;
    }

    public Integer getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(Integer idSalle) {
        this.idSalle = idSalle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}