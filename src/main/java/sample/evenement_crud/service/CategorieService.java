package sample.evenement_crud.service;

import jakarta.transaction.Transactional;
import sample.evenement_crud.entity.Categorie;
import sample.evenement_crud.entity.Evenement;
import sample.evenement_crud.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    // Obtenir toutes les catégories
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    // Obtenir une catégorie par ID
    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id : " + id));
    }

    // Créer une nouvelle catégorie
    public Categorie createCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    // Mettre à jour une catégorie existante
    public Categorie updateCategorie(Long id, Categorie categorieDetails) {
        Categorie categorie = getCategorieById(id);
        categorie.setNom(categorieDetails.getNom());
        return categorieRepository.save(categorie);
    }

    // Supprimer une catégorie
    @Transactional
    public void deleteCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id : " + id));

        // Rompre les relations avec les événements
        for (Evenement evenement : categorie.getEvenements()) {
            evenement.getCategories().remove(categorie);
        }

        categorieRepository.delete(categorie);
    }

}
