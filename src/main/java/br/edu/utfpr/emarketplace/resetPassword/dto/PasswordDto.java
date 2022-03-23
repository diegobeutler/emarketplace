package br.edu.utfpr.emarketplace.resetPassword.dto;

import lombok.Data;

@Data
public class PasswordDto {
    private String token;
    private String newPassword;

}
