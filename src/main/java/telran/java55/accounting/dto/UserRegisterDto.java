package telran.java55.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterDto {
	String login;
    String password;
    String firstName;
    String lastName;
}
