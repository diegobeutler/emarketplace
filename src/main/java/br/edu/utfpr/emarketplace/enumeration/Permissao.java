package br.edu.utfpr.emarketplace.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Permissao {
    ROLE_ADMIN(br.edu.utfpr.emarketplace.model.Permissao.builder().id(1L).nome("ROLE_ADMIN").build()),
    ROLE_USER(br.edu.utfpr.emarketplace.model.Permissao.builder().id(2L).nome("ROLE_USER").build()),
    ROLE_INSTITUTION(br.edu.utfpr.emarketplace.model.Permissao.builder().id(3L).nome("ROLE_INSTITUTION").build());
    private br.edu.utfpr.emarketplace.model.Permissao permissao;
}
