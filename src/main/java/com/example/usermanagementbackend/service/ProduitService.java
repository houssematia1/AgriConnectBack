package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.entity.Produit;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProduitService {
    Produit creer(Produit produit);


    /**
     * Récupère la liste de tous les produits.
     *
     * @return la liste des produits
     */
    List<Produit> lire();


    /**
     * Modifie un produit existant.
     *
     * @param id l'identifiant du produit à modifier
     * @param produit les nouvelles informations du produit
     * @return le produit modifié
     */
    Produit modifier(Integer id, Produit produit);


    /**
     * Supprime un produit existant.
     *
     * @param id l'identifiant du produit à supprimer
     * @return un message indiquant si la suppression a été effectuée avec succès
     */
    String supprimer(Integer id);


    /**
     * Trouve un produit par son identifiant.
     *
     * @param id l'identifiant du produit à trouver
     * @return le produit correspondant à l'identifiant
     */
    Produit trouverParId(Integer id);


    Page<Produit> lireProduitsPagine(int numeroPage, int taillePage, String triPar) ;


    public Page<Produit> recherche(String recherche, String critere);
}
