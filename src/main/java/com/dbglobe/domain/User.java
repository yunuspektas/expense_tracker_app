package com.dbglobe.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

@Entity
@Table(name = "t_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String username;

	private String name;

	private String surname;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password; // hassas veri oldugu icin okuma islemlerinde kullanilmasin

	@Column(unique = true)
	private String phoneNumber;

	@Column(unique = true)
	private String email;

	@OneToOne
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UserRole userRole; // entity.concretes.user

	private Boolean built_in ;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Account> accounts = new HashSet<>();
}
