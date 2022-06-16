package br.edu.utfpr.emarketplace.repository.criteria.impl;

import br.edu.utfpr.emarketplace.enumeration.Operacao;
import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.model.Anuncio_;
import br.edu.utfpr.emarketplace.model.Usuario_;
import br.edu.utfpr.emarketplace.repository.criteria.AnuncioRepositoryCustom;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class AnuncioRepositoryCustomImpl implements AnuncioRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<Anuncio> findAnunciosByFilter(AnuncioFilter filter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Anuncio> query = criteriaBuilder.createQuery(Anuncio.class);
        Root<Anuncio> anuncio = query.from(Anuncio.class);

        Join<Object, Object> usurioOrigem = anuncio.join(Anuncio_.USUARIO_ORIGEM, JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (nonNull(filter.getId())) {
            predicates.add(criteriaBuilder.equal(anuncio.get("id"), filter.getId()));
        }
        if (nonNull(filter.getTitulo())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("titulo")), "%" + filter.getTitulo().toLowerCase() + "%"));
        }
        if (nonNull(filter.getDescricao())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("descricao")), "%" + filter.getDescricao().toLowerCase() + "%"));
        }
        if (nonNull(filter.getOperacao())) {
            predicates.add(criteriaBuilder.equal(anuncio.get("operacao"), filter.getOperacao()));
        }
        if (nonNull(filter.getStatus())) {
            predicates.add(criteriaBuilder.equal(anuncio.get("status"), filter.getStatus()));
        }
        if (nonNull(filter.getDataPublicacaoMin())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMin()));
        }
        if (nonNull(filter.getDataPublicacaoMax())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMax()));
        }
        if (nonNull(filter.getCategoria())) {
            predicates.add(criteriaBuilder.equal(anuncio.get("categoria"), filter.getCategoria()));
        }
        if (nonNull(filter.getEstado()) && isNull(filter.getCidade())) {
            Join<Object, Object> cidadeUsuarioOrigem = usurioOrigem.join(Usuario_.CIDADE, JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(cidadeUsuarioOrigem.get("estado"), filter.getEstado()));
        } else if (nonNull(filter.getCidade())) {
            predicates.add(criteriaBuilder.equal(usurioOrigem.get("cidade"), filter.getCidade()));
        }

        Predicate operacaoNotEqualDoacaoProduto = criteriaBuilder.notEqual(anuncio.get("operacao"), Operacao.DOACAO_PRODUTO);

        if (nonNull(filter.getUsuarioLogado())) {
            Predicate usuarioOrigem = criteriaBuilder.equal(anuncio.get("usuarioOrigem"), filter.getUsuarioLogado());
            Predicate usuarioInstituicao = criteriaBuilder.equal(anuncio.get("usuarioInstituicao"), filter.getUsuarioLogado());
            if (filter.getAnunciei()) {
                predicates.add(usuarioOrigem);
            } else if (filter.getAdquiri()) {
                Predicate usuarioInstituicaoOrDestino = criteriaBuilder.or(usuarioInstituicao, criteriaBuilder.equal(anuncio.get("usuarioDestino"), filter.getUsuarioLogado()));
                predicates.add(usuarioInstituicaoOrDestino);
            }
            Predicate usuarioOrigemORInstituicao = criteriaBuilder.or(usuarioOrigem, usuarioInstituicao);
            Predicate operacaoNotEqualDoacaoProdutoOR_UsuarioOrigemORInstituicao = criteriaBuilder.or(operacaoNotEqualDoacaoProduto, usuarioOrigemORInstituicao);
            predicates.add(operacaoNotEqualDoacaoProdutoOR_UsuarioOrigemORInstituicao);
        } else {
            predicates.add(operacaoNotEqualDoacaoProduto);
        }

        query.where(predicates.toArray(Predicate[]::new));

        TypedQuery<Anuncio> queryResult = this.entityManager.createQuery(query);
        return queryResult.getResultList();
    }
}

