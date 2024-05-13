package com.dbglobe;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.AccountType;
import com.dbglobe.domain.enums.RoleType;
import com.dbglobe.dto.request.UserRequest;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.UserRepository;
import com.dbglobe.service.UserRoleService;
import com.dbglobe.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;


@SpringBootApplication
public class ExpenseTrackerAppApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserService userService;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

    public ExpenseTrackerAppApplication(UserRoleService userRoleService, UserService userService, UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRoleService = userRoleService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// !!! fill Role table if empty
		if(userRoleService.getAllUserRole().isEmpty()){
			userRoleService.save(RoleType.ADMIN);
			userRoleService.save(RoleType.CUSTOMER);
		}
		// Create Built_In Admin if n't exist
		if(userService.countAdminOrCustomer(RoleType.ADMIN)==0){
			UserRequest adminRequest  = new UserRequest();
			adminRequest.setUsername("Admin");
			adminRequest.setPassword("12345678");
			adminRequest.setName("Lars");
			adminRequest.setSurname("Urich");
			adminRequest.setPhoneNumber("111-111-1111");
			userService.saveUser(adminRequest);
		}
		// Create Built_In Customer
		if(userService.countAdminOrCustomer(RoleType.CUSTOMER)==0){

			User customer  = new User();
			customer.setUsername("Customer");
			customer.setPassword(passwordEncoder.encode("123456"));
			customer.setName("Adem");
			customer.setSurname("Nedim");
			customer.setPhoneNumber("111-222-1111");
			customer.setBuilt_in(Boolean.TRUE);
			customer.setUserRole(userRoleService.getUserRole(RoleType.CUSTOMER));

			User savedCustomer = userRepository.save(customer);

			Account accountRequest =
					Account.builder()
							.accountName("Cash")
							.accountType(AccountType.EURO)
							.balance(BigDecimal.valueOf(0.0))
							.customer(savedCustomer)
							.build();
			accountRepository.save(accountRequest);
		}
	}
}
