package br.edu.utfpr.emarketplace.service;

import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

public interface CrudService<T, ID extends Serializable> {
    List<T> findAll();

    List<T> findAll(Sort sort);

    List<T> findAllById(Iterable<ID> ids);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    <S extends T> S saveAndFlush(S entity);

    <S extends T> S save(S entity) throws Exception;

    void delete(ID id);

    T findById(ID id);

}
