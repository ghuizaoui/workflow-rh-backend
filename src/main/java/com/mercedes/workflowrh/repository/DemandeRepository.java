package com.mercedes.workflowrh.repository;

import com.mercedes.workflowrh.entity.Demande;
import com.mercedes.workflowrh.entity.Employe;
import com.mercedes.workflowrh.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findByEmployeMatricule(String matricule);

    /**
     * Trouve toutes les demandes pour un employé donné, triées par date de demande décroissante.
     * @param employe L'employé dont on veut récupérer les demandes.
     * @return Une liste de demandes.
     */
    List<Demande> findByEmployeOrderByDateDemandeDesc(Employe employe);

    /**
     * Trouve toutes les demandes ayant un statut donné.
     * @param statut Le statut de la demande.
     * @return Une liste de demandes.
     */
    List<Demande> findByStatut(StatutDemande statut);

    @Query("""
        select d from Demande d
        where d.employe.role = com.mercedes.workflowrh.entity.Role.EMPLOYE
    """)
    List<Demande> findAllForChefValidationAnyEmployee();

    @Query("""
        select d from Demande d
        where d.employe.role = com.mercedes.workflowrh.entity.Role.CHEF
    """)
    List<Demande> findAllForDrhValidation();
}
