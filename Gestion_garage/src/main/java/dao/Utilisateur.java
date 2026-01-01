package dao;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected int id;

    protected String nom;
    protected String email;
    protected String password;

    @ManyToOne
    protected Boutique boutique;

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Boutique getBoutique() { return boutique; }
}
