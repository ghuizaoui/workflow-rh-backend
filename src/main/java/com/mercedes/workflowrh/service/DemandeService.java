// src/main/java/com/mercedes/workflowrh/service/DemandeService.java
package com.mercedes.workflowrh.service;

import com.mercedes.workflowrh.dto.DemandeDetailDTO;
import com.mercedes.workflowrh.dto.DemandeListDTO;
import com.mercedes.workflowrh.entity.Demande;
import com.mercedes.workflowrh.entity.TypeDemande;
import com.mercedes.workflowrh.entity.StatutDemande;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List; // AJOUTER L'IMPORT


public interface DemandeService {
    Demande createCongeStandard(
            TypeDemande typeDemande,
            LocalDate dateDebut, LocalTime heureDebut,
            LocalDate dateFin,   LocalTime heureFin
    );

    Demande createCongeExceptionnel(
            TypeDemande typeDemande,
            LocalDate dateDebut, LocalTime heureDebut,
            LocalDate dateFin,   LocalTime heureFin
    );

    Demande createAutorisation(
            TypeDemande typeDemande,

            // PRÉVU (requis)
            LocalDate dateAutorisation,
            LocalTime heureDebut,
            LocalTime heureFin,

            // RÉEL (optionnel)
            LocalDate dateReelle,
            LocalTime heureSortieReelle,
            LocalTime heureRetourReel
    );

    Demande createOrdreMission(
            LocalDate dateDebut, LocalTime heureDebut,
            LocalDate dateFin,   LocalTime heureFin,
            String missionObjet
    );
    DemandeDetailDTO findDetail(Long id);

    List<DemandeListDTO> findAllForChef(String matriculeChef);

    List<DemandeListDTO> findAllForDrh();

    Demande validerDemande(Long demandeId, String matriculeValidateur);
    Demande refuserDemande(Long demandeId, String matriculeValidateur, String commentaire);

    List<Demande> getHistoriqueDemandes(String matricule);

    List<Demande> getDemandesEnAttente(String matriculeChef);
    List<Demande> getHistoriqueSubordonnes(String matriculeChef);



}



