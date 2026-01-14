package dao;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "proprietaires")
public class Proprietaire extends UserDAO {

}
