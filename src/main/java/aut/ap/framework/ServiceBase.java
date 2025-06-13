package aut.ap.framework;



import jakarta.persistence.EntityGraph;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

import java.util.List;
import java.util.function.Function;

public abstract class ServiceBase<T extends UniEntity> {
    private Class<T> clazz;

    protected ServiceBase(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void persist(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    public void remove(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
        }
    }

    public T fetchById(int id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.get(clazz, id);
        }
    }

    public List<T> fetchAll() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM " + clazz.getSimpleName(), clazz).list();
        }
    }

    public List<T> fetchAll(Function<Session, RootGraph<T>> rootGraphCreator) {
        try (Session session = getSessionFactory().openSession()) {
            EntityGraph<T> graph = rootGraphCreator.apply(session);
            return session.createQuery("FROM " + clazz.getSimpleName(), clazz)
                    .setEntityGraph(graph, GraphSemantic.LOAD)
                    .getResultList();
        }
    }

    protected SessionFactory getSessionFactory() {
        return SingletonSessionFactory.get();
    }
}