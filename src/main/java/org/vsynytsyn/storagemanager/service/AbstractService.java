package org.vsynytsyn.storagemanager.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.TransactionSystemException;
import org.vsynytsyn.storagemanager.domain.UserEntity;

/**
 * @param <E>
 * @param <T>
 * @param <D>
 */
public abstract class AbstractService<E, T, D> {

    private final JpaRepository<E, T> repository;
    protected final ModelMapper mapper;


    protected AbstractService(JpaRepository<E, T> repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public Page<E> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }


    public E getOne(T id) {
        return repository.findById(id).orElse(null);
    }


    public E create(E obj, UserEntity currentUser) {
        try {
            return repository.save(obj);
        } catch (TransactionSystemException e){
            System.err.println(e.getMessage());
        }
        return null;
    }


    public E update(E obj, D objDTO, UserEntity currentUser) {
        mapper.map(objDTO, obj);
        return repository.save(obj);
    }


    public boolean delete(E obj, UserEntity currentUser) {
        if (obj == null)
            return false;
        try {
            repository.delete(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
