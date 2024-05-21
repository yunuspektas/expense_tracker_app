package com.dbglobe;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.AccountType;
import com.dbglobe.domain.enums.RoleType;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;


@SpringBootApplication
@RequiredArgsConstructor
public class ExpenseTrackerAppApplication implements CommandLineRunner {

    private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;


    public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		createAdminIfNotExist();
		User user = createCustomerIfNotExist();
		createAccount(user);
	}

	private void createAccount(User user){
		if(user!=null) {
			Account accountRequest =
					Account.builder()
							.accountName("Cash")
							.accountType(AccountType.EURO)
							.balance(BigDecimal.valueOf(0.0))
							.customer(user)
							.build();
			accountRepository.save(accountRequest);
		}
	}

	private User createCustomerIfNotExist(){
        if (userRepository.existsByRole(RoleType.CUSTOMER))
            return null;

        var customer  = new User();
        customer.setUsername("Customer");
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setEmail("customer@customer.com");
        customer.setName("Adem");
        customer.setSurname("Nedim");
        customer.setPhoneNumber("111-222-1111");
        customer.setBuilt_in(Boolean.TRUE);
        customer.setRole(RoleType.CUSTOMER);

        return userRepository.save(customer);
    }

	private void createAdminIfNotExist(){
        if (userRepository.existsByRole(RoleType.ADMIN))
            return;

        var admin  = new User();
        admin.setUsername("Admin");
        admin.setPassword(passwordEncoder.encode("12345678"));
        admin.setEmail("admin@admin.com");
        admin.setName("Lars");
        admin.setSurname("Urich");
        admin.setPhoneNumber("111-111-1111");
        admin.setBuilt_in(Boolean.TRUE);
        admin.setRole(RoleType.ADMIN);

        userRepository.save(admin);
    }
}
