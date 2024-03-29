package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.service.CrudService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class CrudServiceImpl<T, ID extends Serializable> implements CrudService<T, ID> {

    public abstract JpaRepository<T, ID> getRepository();

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllById(Iterable<ID> ids) {
        return getRepository().findAllById(ids);
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return getRepository().saveAll(entities);
    }

    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        return getRepository().saveAndFlush(entity);
    }

    public void valid(T entity) throws Exception {

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class )
    public <S extends T> S save(S entity) throws Exception {
        valid(entity);
        preSave(entity);
        getRepository().save(entity);
        postSave(entity);
        return entity;
    }

    public void preSave(T entity) {
    }

    public void postSave(T entity) {
    }

    @Override
    @Transactional
    public void delete(ID id) {
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        getRepository().delete(entity);
    }

    @Override
    @Transactional
    public T findById(ID id) {
        return getRepository().findById(id).orElse(null);
    }

}
