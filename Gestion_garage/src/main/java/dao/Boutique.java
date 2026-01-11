package dao;

import javax.persistence.*;

@Entity
public class Boutique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String adresse;

    public Boutique() {}
    public Boutique(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
    }

    // getters & setters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}
