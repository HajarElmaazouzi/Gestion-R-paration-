package dao;

import javax.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "emprunts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpruntDAO {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "montant", nullable = false)
    private Double montant;
    
    @Column(name = "date_emprunt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "emprunteur_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDAO emprunteur;
    
    @ManyToOne
    @JoinColumn(name = "preteur_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDAO preteur;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20)
    private StatutEmprunt statut = StatutEmprunt.EN_COURS;
    
    @Column(name = "date_remboursement")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRemboursement;
    
    public enum StatutEmprunt {
        EN_COURS,
        REMBOURSE,
        ANNULE
    }
}
