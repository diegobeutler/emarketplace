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
        /**
         * First, we get a CriteriaBuilder reference, which we can use to create
         * different parts of the query
         */
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        /**
         * Using the CriteriaBuilder, we create a CriteriaQuery<Anuncio>, which describes
         * what we want to do in the query. Also, it declares the type of a row in the
         * result
         */
        CriteriaQuery<Anuncio> query = criteriaBuilder.createQuery(Anuncio.class);
        /**
         * With CriteriaQuery<Anuncio> we declare the starting point of the query (Anuncio
         * entity), and we store it in the Anuncio variable for later use
         */
        Root<Anuncio> anuncio = query.from(Anuncio.class);

        Join<Object, Object> usurioOrigem = anuncio.join(Anuncio_.USUARIO_ORIGEM, JoinType.LEFT);
//        Join<Object, Object> cidadeUsuarioOrigem = usurioOrigem.join(Usuario_.CIDADE, JoinType.LEFT);
//        Join<Object, Object> ufUsuarioOrigem = cidadeUsuarioOrigem.join(Cidade_.ESTADO, JoinType.LEFT);
        Join<Object, Object> usuarioOrigem = anuncio.join(Anuncio_.USUARIO_ORIGEM);
//        Join<Object, Object> book = root.join(Author_.BOOKS);
//
//        ParameterExpression<String> pTitle = cb.parameter(String.class);
//        cq.where(cb.like(book.get(Book_.TITLE), pTitle));
//
//        ParameterExpression<String> pTitle = cb.parameter(String.class);
//        cq.where(cb.like(book.get(Book_.TITLE), pTitle));
//
//        TypedQuery<Author> q = em.createQuery(cq);
//        q.setParameter(pTitle, "%Hibernate%");
//        List<Author> authors = q.getResultList();





        /**
         * Create a dinamic list of predicates
         */
        List<Predicate> predicates = new ArrayList<>();
        /**
         * Next, with CriteriaBuilder we create predicates against our Anuncio entity.
         * Note, that these predicates don't have any effect yet
         */

        if ( filter.getId() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("id"), filter.getId()));
        }
        if ( filter.getTitulo() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("titulo")), "%" + filter.getTitulo().toLowerCase() + "%"));
        }
        if ( filter.getDescricao() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(anuncio.get("descricao")), "%" + filter.getDescricao().toLowerCase() + "%"));
        }
        if ( filter.getOperacao() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("operacao"), filter.getOperacao()));
        }
        if ( filter.getStatus() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("status"), filter.getStatus()));
        }
        if ( filter.getAutor() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("usuarioOrigem"), filter.getAutor()));
        }
        if ( filter.getAdquirente() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("usuarioDestino"), filter.getAdquirente()));
        }
        if ( filter.getDataPublicacaoMin() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMin()));
        }
        if ( filter.getDataPublicacaoMax() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(anuncio.get("dataPublicacao"), filter.getDataPublicacaoMax()));
        }
        if ( filter.getCategoria() != null) {
            predicates.add(criteriaBuilder.equal(anuncio.get("categoria"), filter.getCategoria()));
        }
        if ( filter.getEstado() != null && filter.getCidade() == null) {
            Join<Object, Object> cidadeUsuarioOrigem = usurioOrigem.join(Usuario_.CIDADE, JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(cidadeUsuarioOrigem.get("estado"), filter.getEstado()));
        }
        if ( filter.getCidade() != null) {
            predicates.add(criteriaBuilder.equal(usurioOrigem.get("cidade"), filter.getCidade()));
        }
        /**
         * We apply both predicates to our CriteriaQuery.
         * CriteriaQuery.where(Predicate…) combines its arguments in a logical and. This
         * is the point when we tie these predicates to the query
         */

        /**
         Verify the predicates to add in where clause
         */
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }
        /**
         * After that, we create a TypedQuery<Anuncio> instance from our CriteriaQuery
         */
        TypedQuery<Anuncio> queryResult = this.entityManager.createQuery(query);
        /**
         * Finally, we return all matching Anuncio entities
         */
        return queryResult.getResultList();
    }
}

