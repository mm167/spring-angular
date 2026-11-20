package fr.mm.formation.location.comptes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByIdentifiant(String identifiant);
}