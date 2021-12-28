package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
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

    public abstract void valid(T entity) throws Exception;

    @Override
    @Transactional
    public <S extends T> S save(S entity) throws Exception {
        valid(entity);
        return getRepository().save(entity);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    public T findById(ID id) {
        return getRepository().findById(id).orElse(null);
    }

}
