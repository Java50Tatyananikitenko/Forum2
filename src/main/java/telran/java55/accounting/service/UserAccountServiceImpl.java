package telran.java55.accounting.service;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java55.accounting.dao.UserAccountRepository;
import telran.java55.accounting.dto.RolesDto;
import telran.java55.accounting.dto.UserDto;
import telran.java55.accounting.dto.UserEditDto;
import telran.java55.accounting.dto.UserRegisterDto;
import telran.java55.accounting.dto.exceptions.UserNotFoundException;
import telran.java55.accounting.model.UserAccount;
@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
	
	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if(userAccountRepository.existsById(userRegisterDto.getLogin())) {
			throw new IllegalArgumentException("User with login: "+ userRegisterDto.getLogin() + "already exists.");
		}
		 UserAccount userAccount = new UserAccount(
		            userRegisterDto.getLogin(),
		            passwordEncoder.encode(userRegisterDto.getPassword()),
		            userRegisterDto.getFirstName(),
		            userRegisterDto.getLastName()
		        );

		        userAccountRepository.save(userAccount);
		        return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		 UserAccount userAccount = userAccountRepository.findById(login)
	                .orElseThrow(UserNotFoundException::new);
	        userAccountRepository.delete(userAccount);
	        return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userAccountRepository.findById(login)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		 UserAccount userAccount = userAccountRepository.findById(login)
	                .orElseThrow(UserNotFoundException::new);

	        if (userEditDto.getFirstName() != null) {
	            userAccount.setFirstName(userEditDto.getFirstName());
	        }
	        if (userEditDto.getLastName() != null) {
	            userAccount.setLastName(userEditDto.getLastName());
	        }

	        userAccountRepository.save(userAccount);
	        return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = userAccountRepository.findById(login)
                .orElseThrow(UserNotFoundException::new);

        boolean modified;
        if (isAddRole) {
            modified = userAccount.addRole(role);
        } else {
            modified = userAccount.removeRole(role);
        }

        if (modified) {
            userAccountRepository.save(userAccount);
        }

        return RolesDto.builder()
                .login(login)
                .roles(userAccount.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .build();
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userAccountRepository.findById(login)
                .orElseThrow(UserNotFoundException::new);

        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(userAccount);

	}

}
