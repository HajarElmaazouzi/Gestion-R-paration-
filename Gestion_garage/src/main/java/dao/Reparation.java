package dao;

import javax.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reparations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reparation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ====== SUIVI CLIENT ======
    @Column(unique = true, nullable = false)
    private String numero;
    
    @Column(nullable = false, length = 100)
    private String client;
    
    @Column(length = 15)
    private String telephone;
    
    // ====== PHOTO CLIENT ======
    @Column(name = "photo_path", length = 500)
    private String photoPath;  // Chemin vers la photo du client (capturée par caméra de surveillance)
    
    // ====== INFOS TECHNIQUES ======
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;
    
    @Column(columnDefinition = "TEXT")
    private String piecesUtilisees;
    
    private Double coutTotal = 0.0;
    
    // ====== PAIEMENT ======
    private Double avance = 0.0;  // Montant d'avance payé
    private Double reste = 0.0;   // Montant restant à payer
    
    @Enumerated(EnumType.STRING)
    private EtatReparation etat = EtatReparation.OUVERT;
    
    // ====== RELATIONS ======
    @ManyToMany
    @JoinTable(
        name = "reparation_appareil",
        joinColumns = @JoinColumn(name = "reparation_id"),
        inverseJoinColumns = @JoinColumn(name = "appareil_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Appareil> appareils;
    
    @ManyToOne
    @JoinColumn(name = "reparateur_id", nullable = false)
    private UserDAO reparateur;
    
    // ====== INITIALISATION ======
    @PrePersist
    public void init() {
        if (dateCreation == null) {
            dateCreation = new Date();
        }
        if (numero == null) {
            numero = "REP" + (System.currentTimeMillis() % 10000);
        }
    }
}