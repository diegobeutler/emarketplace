package br.edu.utfpr.emarketplace.service;

import br.edu.utfpr.emarketplace.model.PasswordResetToken;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.resetPassword.dto.PasswordDto;

public interface PasswordResetTokenService extends CrudService<PasswordResetToken, Long> {
    Usuario getUserByPasswordResetToken(String token);

    String validatePasswordResetToken(String token);

    boolean isTokenFound(PasswordResetToken passToken);

    boolean isTokenExpired(PasswordResetToken passToken);

    void enviarEmailResetPassword(String username);

    void updatePassword(PasswordDto passwordDto) throws Exception;
}
