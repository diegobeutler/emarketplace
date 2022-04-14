package br.edu.utfpr.emarketplace.repository.criteria.impl;

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

@Repository
@RequiredArgsConstructor
public class AnuncioRepositoryCustomImpl implements AnuncioRepositoryCustom {


    @PersistenceContext
    private EntityManager entityManager;

    public AnuncioRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Anuncio> findAnunciosByFilter(AnuncioFilter filter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Anuncio> query = criteriaBuilder.createQuery(Anuncio.class);
        Root<Anuncio> anuncio = query.from(Anuncio.class);

        Join<Object, Object> usurioOrigem = anuncio.join(Anuncio_.USUARIO_ORIGEM, JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getId() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("id"), filter.getId()));
        }
        if (filter.getTitulo() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("titulo")), "%" + filter.getTitulo().toLowerCase() + "%"));
        }
        if (filter.getDescricao() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("descricao")), "%" + filter.getDescricao().toLowerCase() + "%"));
        }
        if (filter.getOperacao() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("operacao"), filter.getOperacao()));
        }
        if (filter.getStatus() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("status"), filter.getStatus()));
        }
        if (filter.getAnunciei() && filter.getUsuarioLogado() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("usuarioOrigem"), filter.getUsuarioLogado()));
        }
        if (filter.getAdquiri() && filter.getUsuarioLogado() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("usuarioDestino"), filter.getUsuarioLogado()));
        }
        if (filter.getDataPublicacaoMin() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMin()));
        }
        if (filter.getDataPublicacaoMax() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMax()));
        }
        if (filter.getCategoria() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("categoria"), filter.getCategoria()));
        }
        if (filter.getEstado() != null && filter.getCidade() == null) {
            Join<Object, Object> cidadeUsuarioOrigem = usurioOrigem.join(Usuario_.CIDADE, JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(cidadeUsuarioOrigem.get("estado"), filter.getEstado()));
        }
        if (filter.getCidade() != null) {
            predicates.add(criteriaBuilder.equal(usurioOrigem.get("cidade"), filter.getCidade()));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }

        TypedQuery<Anuncio> queryResult = this.entityManager.createQuery(query);
        return queryResult.getResultList();
    }
}

