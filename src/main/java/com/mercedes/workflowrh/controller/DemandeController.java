// src/main/java/com/mercedes/workflowrh/controller/DemandeController.java
package com.mercedes.workflowrh.controller;

import com.mercedes.workflowrh.dto.*;
import com.mercedes.workflowrh.entity.Demande;
import com.mercedes.workflowrh.service.DemandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mercedes.workflowrh.entity.Employe;
import com.mercedes.workflowrh.security.AppUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;


@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;

    @PostMapping("/conge-standard")
    public ResponseEntity<Demande> createCongeStandard(@Valid @RequestBody CongeRequest req) {
        return ResponseEntity.ok(
                demandeService.createCongeStandard(
                        req.getTypeDemande(),
                        req.getDateDebut(), req.getHeureDebut(),
                        req.getDateFin(),   req.getHeureFin()
                )
        );
    }

    @PostMapping("/conge-exceptionnel")
    public ResponseEntity<Demande> createCongeExceptionnel(@Valid @RequestBody CongeRequest req) {
        return ResponseEntity.ok(
                demandeService.createCongeExceptionnel(
                        req.getTypeDemande(),
                        req.getDateDebut(), req.getHeureDebut(),
                        req.getDateFin(),   req.getHeureFin()
                )
        );
    }

    // src/main/java/com/mercedes/workflowrh/controller/DemandeController.java
    @PostMapping("/autorisation")
    public ResponseEntity<Demande> createAutorisation(@Valid @RequestBody AutorisationRequest req) {
        return ResponseEntity.ok(
                demandeService.createAutorisation(
                        req.getTypeDemande(),

                        // PRÉVU (requis)
                        req.getDateAutorisation(),
                        req.getHeureDebut(),
                        req.getHeureFin(),

                        // RÉEL (optionnel)
                        req.getDateReelle(),
                        req.getHeureSortieReelle(),
                        req.getHeureRetourReel()
                )
        );
    }


    @PostMapping("/ordre-mission")
    public ResponseEntity<Demande> createOrdreMission(@Valid @RequestBody OrdreMissionRequest req) {
        return ResponseEntity.ok(
                demandeService.createOrdreMission(
                        req.getDateDebut(), req.getHeureDebut(),
                        req.getDateFin(),   req.getHeureFin(),
                        req.getMissionObjet()
                )
        );
    }




    @PostMapping("/validation/{demandeId}")
    public ResponseEntity<Demande> validerRefuserDemande(
            @PathVariable Long demandeId,
            @RequestBody ValidationRequest validationRequest) { // Utilise le DTO

        // Récupérer l'employé connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String matriculeValidateur = auth.getName();

        // Appeler le service pour traiter la validation ou le refus
        Demande demandeMiseAJour;
        if (validationRequest.getIsValidee()) {
            demandeMiseAJour = demandeService.validerDemande(demandeId, matriculeValidateur);
        } else {
            demandeMiseAJour = demandeService.refuserDemande(demandeId, matriculeValidateur, validationRequest.getCommentaire());
        }

        return ResponseEntity.ok(demandeMiseAJour);
    }

    @GetMapping("/historique")
    public ResponseEntity<List<Demande>> getHistoriqueDemandes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Ajout d'une vérification pour déboguer le problème d'authentification
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "L'utilisateur n'est pas authentifié.");
        }

        String matriculeEmploye = auth.getName();

        if (matriculeEmploye == null || matriculeEmploye.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le matricule de l'employé n'a pas pu être récupéré.");
        }

        List<Demande> demandes = demandeService.getHistoriqueDemandes(matriculeEmploye);
        return ResponseEntity.ok(demandes);
    }
    @GetMapping("/demandes-en-attente")
    public ResponseEntity<List<Demande>> getDemandesEnAttenteForChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "L'utilisateur n'est pas authentifié.");
        }

        String matriculeChef = auth.getName();

        if (matriculeChef == null || matriculeChef.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le matricule du chef n'a pas pu être récupéré.");
        }

        List<Demande> demandes = demandeService.getDemandesEnAttente(matriculeChef);
        return ResponseEntity.ok(demandes);
    }
    // Récupérer l’historique des demandes des subordonnés d’un chef
    @GetMapping("/historique-subordonnes/{matriculeChef}")
    public ResponseEntity<List<Demande>> getHistoriqueSubordonnes(@PathVariable String matriculeChef) {
        List<Demande> demandes = demandeService.getHistoriqueSubordonnes(matriculeChef);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/chef")
    public List<DemandeListDTO> listForChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String matriculeChef = auth.getName(); // doit être le matricule
        return demandeService.findAllForChef(matriculeChef);
    }

    /** Affichage DRH : demandes des CHEFs (quel que soit le validateur connecté) */
    @GetMapping("/drh")
    public List<DemandeListDTO> listForDrh() {
        return demandeService.findAllForDrh();
    }

    /** Détail commun */
    @GetMapping("/{id}")
    public DemandeDetailDTO detail(@PathVariable Long id) {
        return demandeService.findDetail(id);
    }

    @PostMapping("/{demandeId}/valider")
    public ResponseEntity<Demande> valider(@PathVariable Long demandeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String matricule = auth.getName();
        Demande updated = demandeService.validerDemande(demandeId, matricule);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{demandeId}/refuser")
    public ResponseEntity<Demande> refuser(@PathVariable Long demandeId, @Valid @RequestBody RefusRequest body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String matricule = auth.getName();
        Demande updated = demandeService.refuserDemande(demandeId, matricule, body.getCommentaire());
        return ResponseEntity.ok(updated);
    }
}
