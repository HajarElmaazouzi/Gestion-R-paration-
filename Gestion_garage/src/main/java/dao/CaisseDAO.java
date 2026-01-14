package dao;

import javax.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "caisses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseDAO {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "solde_actuel", nullable = false)
    private Double soldeActuel = 0.0;
    
    @Column(name = "total_encaisse")
    private Double totalEncaisse = 0.0;
    
    @Column(name = "total_retire")
    private Double totalRetire = 0.0;
    
    @Column(name = "date_creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation = new Date();
    
    @Column(name = "date_derniere_maj")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereMaj = new Date();
    
    // Bidirectional relationship - allows query: c.user.id
    @OneToOne(mappedBy = "caisse")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserDAO user;
    
}
