package org.amm.ams.dao.implementations;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.amm.ams.dao.interfaces.Dao;
import org.amm.ams.domain.Identifiable;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Required;

public class HibernateJpaDao<T extends Identifiable> implements Dao<T> {

	private final Class<T> persistentClass;
	private EntityManager entityManager;

	// constructors

	@SuppressWarnings("unchecked")
	public HibernateJpaDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public HibernateJpaDao(final Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	// getters and setters

	@Required
	@PersistenceContext
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	// methods

	@Override
	public Long getRowCount() {

		Session session = (Session) entityManager.getDelegate();

		Criteria criteria = session.createCriteria(persistentClass);
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();

		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {

		Session session = (Session) getEntityManager().getDelegate();
		Criteria crit = session.createCriteria(getPersistentClass());

		return crit.list();
	}

	@Override
	public T findById(Serializable id) {
		return this.entityManager.find(persistentClass, id);
	}

	@Override
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	public T create(T entity) {
		this.entityManager.persist(entity);
		return entity;
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	@Override
	public void delete(Long id) {
		entityManager.remove(findById(id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByNamedQuery(final String name, Object[] params) {

		javax.persistence.Query query = getEntityManager().createNamedQuery(
				name);

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}

		final List<T> result = (List<T>) query.getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByNamedQueryAndNamedParams(final String name,
			final Map<String, ? extends Object> params) {

		javax.persistence.Query query = getEntityManager().createNamedQuery(
				name);

		for (final Map.Entry<String, ? extends Object> param : params
				.entrySet()) {
			query.setParameter(param.getKey(), param.getValue());
		}

		final List<T> result = (List<T>) query.getResultList();
		return result;
	}

}
