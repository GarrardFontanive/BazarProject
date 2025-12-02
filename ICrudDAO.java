package com.classes.DAO;

import java.util.List;

public interface ICrudDAO<T> {

    boolean inserir(T objeto);

    boolean atualizar(T objeto);

    boolean excluir(Object id);

    List<T> listarTodos();
}