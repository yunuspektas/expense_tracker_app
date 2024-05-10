package com.dbglobe;

import com.dbglobe.domain.enums.RoleType;
import com.dbglobe.dto.request.UserRequest;
import com.dbglobe.service.UserRoleService;
import com.dbglobe.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class ExpenseTrackerAppApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserService userService;

    public ExpenseTrackerAppApplication(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
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
		// Create one Customer
		if(userService.countAdminOrCustomer(RoleType.CUSTOMER)==0){
			UserRequest customerRequest  = new UserRequest();
			customerRequest.setUsername("Customer");
			customerRequest.setPassword("12345678");
			customerRequest.setName("Adem");
			customerRequest.setSurname("Nedim");
			customerRequest.setPhoneNumber("111-222-1111");
			userService.saveUser(customerRequest);
		}
	}
}
