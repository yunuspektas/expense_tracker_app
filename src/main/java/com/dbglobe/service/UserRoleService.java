package com.dbglobe.service;

import com.dbglobe.domain.UserRole;
import com.dbglobe.domain.enums.RoleType;
import com.dbglobe.exception.ConflictException;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

	private final UserRoleRepository userRoleRepository;

	public UserRole getUserRole (RoleType roleType){
		return userRoleRepository.findByEnumRoleEquals(roleType).orElseThrow(
				()-> new ConflictException(ErrorMessages.ROLE_NOT_FOUND));
	}

	public List<UserRole>getAllUserRole(){
		return userRoleRepository.findAll();
	}

	public UserRole save (RoleType roleType){
		if(userRoleRepository.existsByEnumRoleEquals(roleType)){
			throw new ConflictException(ErrorMessages.ROLE_ALREADY_EXIST);
		}
		UserRole userRole = UserRole.builder().roleType(roleType).build();
		return userRoleRepository.save(userRole);
	}
}
