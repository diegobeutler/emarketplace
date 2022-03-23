package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.exception.InvalidTokenException;
import br.edu.utfpr.emarketplace.model.PasswordResetToken;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.repository.PasswordResetTokenRepository;
import br.edu.utfpr.emarketplace.repository.UsuarioRepository;
import br.edu.utfpr.emarketplace.resetPassword.dto.PasswordDto;
import br.edu.utfpr.emarketplace.service.PasswordResetTokenService;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.email.EnvioEmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDate.now;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl extends CrudServiceImpl<PasswordResetToken, Long> implements PasswordResetTokenService {// todo ver se precisa estender crud service


    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final EnvioEmailServiceImpl envioEmailServiceImpl;

    @Override
    public Usuario getUserByPasswordResetToken(String token) {
        var passwordToken = passwordResetTokenRepository.findByToken(token);
        return passwordToken != null ? passwordToken.getUsuario() : null;
    }

    @Override
    public JpaRepository<PasswordResetToken, Long> getRepository() {
        return passwordResetTokenRepository;
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Override
    public boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    @Override
    public boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(now());
    }

    @Override
    public void enviarEmailResetPassword(String username) {
        Optional<Usuario> usuario = usuarioRepository.findUsuarioByUsername(username);
        if (usuario.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(usuario.get(), token);
        envioEmailServiceImpl.resetTokenEmail("http://localhost:4200/usuario", token, usuario.get().getEmail());
    }

    @Override
    public void updatePassword(PasswordDto passwordDto) throws Exception {
        String result = validatePasswordResetToken(passwordDto.getToken());
        if (result != null) {
            throw new InvalidTokenException("Token não encontrado ou expirado.");
        }
        Usuario usuario = getUserByPasswordResetToken(passwordDto.getToken());
        if (usuario != null) {
            usuarioService.changeUserPassword(usuario, passwordDto.getNewPassword());
        } else {
            throw new InvalidTokenException("Token não encontrado");
        }
    }

    private void createPasswordResetTokenForUser(Usuario usuario, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, usuario);
        passwordResetTokenRepository.save(myToken);
    }
}
