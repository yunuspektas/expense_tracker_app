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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;


@SpringBootApplication
@RequiredArgsConstructor
public class ExpenseTrackerAppApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserService userService;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;


    public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		createRolesIfRoleTableIsEmpty();
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
		if(userService.countAdminOrCustomer(RoleType.CUSTOMER)==0){

			User customer  = new User();
			customer.setUsername("Customer");
			customer.setPassword(passwordEncoder.encode("123456"));
			customer.setEmail("customer@customer.com");
			customer.setName("Adem");
			customer.setSurname("Nedim");
			customer.setPhoneNumber("111-222-1111");
			customer.setBuilt_in(Boolean.TRUE);
			customer.setUserRole(userRoleService.getUserRole(RoleType.CUSTOMER));

			return userRepository.save(customer);
		}
		return null;
	}

	private void createAdminIfNotExist(){
		if(userService.countAdminOrCustomer(RoleType.ADMIN)==0){
			UserRequest adminRequest  = new UserRequest();
			adminRequest.setUsername("Admin");
			adminRequest.setPassword("12345678");
			adminRequest.setEmail("admin@admin.com");
			adminRequest.setName("Lars");
			adminRequest.setSurname("Urich");
			adminRequest.setPhoneNumber("111-111-1111");
			userService.saveUser(adminRequest);
		}
	}

	private void createRolesIfRoleTableIsEmpty(){
		if(userRoleService.getAllUserRole().isEmpty()){
			userRoleService.save(RoleType.ADMIN);
			userRoleService.save(RoleType.CUSTOMER);
		}
	}
}
