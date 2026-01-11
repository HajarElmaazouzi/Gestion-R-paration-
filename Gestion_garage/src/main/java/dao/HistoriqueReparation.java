package dao;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historique_reparation")
public class HistoriqueReparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private LocalDate date;

    @ManyToOne
    private Reparateur reparateur;

    @ManyToOne
    private Boutique boutique;

    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public Reparateur getReparateur() { return reparateur; }
    public Boutique getBoutique() { return boutique; }
}
