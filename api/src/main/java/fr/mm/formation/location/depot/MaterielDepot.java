package fr.mm.formation.location.depot;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import fr.mm.formation.location.contrat.modele.Categorie;
import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.contrat.modele.NouveauMateriel;
import org.springframework.stereotype.Repository;

@Repository
public class MaterielDepot {

    private final Map<Long, Materiel> materiels = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public MaterielDepot() {
        creer(echantillon("Mini-pelle 1,5 t", Categorie.MINI_PELLE, 180.0));
        creer(echantillon("Nacelle ciseaux 8 m", Categorie.NACELLE, 220.0));
        creer(echantillon("Compacteur monorouleau 3 t", Categorie.COMPACTEUR, 150.0));
    }

    public List<Materiel> lister() {
        return List.copyOf(materiels.values());
    }

    public Optional<Materiel> trouver(Long id) {
        return Optional.ofNullable(materiels.get(id));
    }

    public Materiel creer(NouveauMateriel nouveau) {
        Materiel materiel = new Materiel();
        materiel.setId(sequence.incrementAndGet());
        materiel.setNom(nouveau.getNom());
        materiel.setCategorie(nouveau.getCategorie());
        materiel.setTarifJournalier(nouveau.getTarifJournalier());
        materiels.put(materiel.getId(), materiel);
        return materiel;
    }

    private static NouveauMateriel echantillon(String nom, Categorie categorie, double tarifJournalier) {
        NouveauMateriel nouveau = new NouveauMateriel();
        nouveau.setNom(nom);
        nouveau.setCategorie(categorie);
        nouveau.setTarifJournalier(tarifJournalier);
        return nouveau;
    }
}