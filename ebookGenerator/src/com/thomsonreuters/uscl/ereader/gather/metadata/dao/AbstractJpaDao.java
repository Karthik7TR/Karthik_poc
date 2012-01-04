/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.metadata.dao;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

	


public abstract class AbstractJpaDao<T extends Object> implements JpaDao<T>  {
		
		private int defaultMaxResults = DEFAULT_MAX_RESULTS;


		/**
		 * The {@link EntityManager} which is used by all query manipulation and execution in this DAO.
		 * 
		 * @return the {@link EntityManager}
		 */
		public abstract EntityManager getEntityManager();

		/*
		 * (non-Javadoc)
		 *
		 */
		public abstract Set<Class<?>> getTypes();

		/*
		 * (non-Javadoc)
		 *
		 */
		@Transactional
		public T store(T toStore) {
			if (canBeMerged(toStore)) {
				return merge(toStore);
			}
			else {
				return persist(toStore);
			}
		}
		
		/*
		 * (non-Javadoc)
		 *
		 */
		@Transactional
		public T merge(T toMerge) {
			return getEntityManager().merge(toMerge);
		}
		
		/*
		 * (non-Javadoc)
		 *
		 */
		@Transactional
		public T persist(T toPersist) {
			getEntityManager().persist(toPersist);
			return toPersist;
		}

		/*
		 * (non-Javadoc)
		 * 
		 */
		@Transactional
		public void remove(Object toRemove) {
			toRemove = getEntityManager().merge(toRemove);
			getEntityManager().remove(toRemove);
		}

		/*
		 * (non-Javadoc)
		 * 
		 */
		@Transactional
		public void flush() {
			getEntityManager().flush();
		}

		/*
		 * (non-Javadoc)
		 * 
		 */
		@SuppressWarnings("unchecked")
		@Transactional
		public void refresh(Object o) {
			try {
				if (o != null) {
					if (o instanceof java.util.Collection) {
						for (Iterator<?> i = ((Collection<?>) o).iterator(); i.hasNext();) {
							try {
								refresh(i.next());
							} catch (EntityNotFoundException x) {
								// This entity has been deleted - remove it from the collection
								i.remove();
							}
						}
					} else {
						if (getTypes().contains(o.getClass())) {
							getEntityManager().refresh(o);
						}
					}
				}
			} catch (EntityNotFoundException x) {
				// This entity has been deleted
			}
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public void setDefaultMaxResults(int defaultMaxResults) {
			this.defaultMaxResults = defaultMaxResults;
		}

		/*
		 * (non-Javadoc)
		 * 
		 */
		@Transactional
		public int getDefaultMaxResults() {
			return defaultMaxResults;
		}

		/*
		 * (non-Javadoc)
		 
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public T executeQueryByNameSingleResult(String queryName) {
			return (T) executeQueryByNameSingleResult(queryName, (Object[])null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public T executeQueryByNameSingleResult(String queryName, Object... parameters) {
			Query query = createNamedQuery(queryName, DEFAULT_FIRST_RESULT_INDEX, 1, parameters);
			return (T) query.getSingleResult();
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public List<T> executeQueryByName(String queryName) {
			return executeQueryByName(queryName, DEFAULT_FIRST_RESULT_INDEX, getDefaultMaxResults());
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public List<T> executeQueryByName(String queryName, Integer firstResult, Integer maxResults)  {
			return executeQueryByName(queryName, firstResult, maxResults, (Object[])null);
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public List<T> executeQueryByName(String queryName, Object... parameters)  {
			return executeQueryByName(queryName, DEFAULT_FIRST_RESULT_INDEX, getDefaultMaxResults(), parameters);
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public List<T> executeQueryByName(String queryName, Integer firstResult, Integer maxResults, Object... parameters)  {
			Query query = createNamedQuery(queryName, firstResult, maxResults, parameters);
			return query.getResultList();
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public Query createNamedQuery(String queryName, Integer firstResult, Integer maxResults)  {
			return createNamedQuery(queryName, firstResult, maxResults, (Object[])null);
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public Query createNamedQuery(String queryName, Integer firstResult, Integer maxResults, Object... parameters)  {
			Query query = getEntityManager().createNamedQuery(queryName);
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					query.setParameter(i + 1, parameters[i]);
				}
			}
			
			query.setFirstResult(firstResult == null || firstResult < 0 ? DEFAULT_FIRST_RESULT_INDEX : firstResult);
			if (maxResults != null && maxResults > 0)
				query.setMaxResults(maxResults);

			return query;
		}
		
		/*
		 * (non-Javadoc)
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public List<T> executeQuery(String queryString, Integer firstResult, Integer maxResults, Object... parameters)  {
			Query query = createQuery(queryString, firstResult, maxResults, parameters);
			return query.getResultList();
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public List<T> executeQuery(String queryString, Object... parameters)  {
			Query query = createQuery(queryString, DEFAULT_FIRST_RESULT_INDEX, getDefaultMaxResults(), parameters);
			return query.getResultList();
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public Object executeQuerySingleResult(String queryString) {
			return  executeQuerySingleResult(queryString, (Object[])null);
		}

		/*
		 * (non-Javadoc)
		 */
		@Transactional
		@SuppressWarnings("unchecked")
		public T executeQuerySingleResult(String queryString, Object... parameters) {
			Query query = createQuerySingleResult(queryString, parameters);
			return (T) query.getSingleResult();
		}

		
		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public Query createQuery (String queryString, Integer firstResult, Integer maxResults){
			return createQuery(queryString, firstResult, maxResults, (Object[])null);
		}
		
		/**
		 * Creates a query that will return a single result by default
		 * @param queryString
		 * @param parameters
		 * @return
		 */
		public Query createQuerySingleResult (String queryString, Object... parameters) {
			return createQuery(queryString, DEFAULT_FIRST_RESULT_INDEX, 1, parameters);
		}
		
		/*
		 * (non-Javadoc)
		 */
		@Transactional
		public Query createQuery (String queryString, Integer firstResult, Integer maxResults, Object... parameters){
			Query query = getEntityManager().createQuery(queryString);
			if (parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					query.setParameter(i + 1, parameters[i]);
				}
			}

			query.setFirstResult(firstResult == null || firstResult < 0 ? DEFAULT_FIRST_RESULT_INDEX : firstResult);
			if (maxResults != null && maxResults > 0)
				query.setMaxResults(maxResults);

			return query;
		}
		
		/**
		 * Each DAO can decide whether the object passed can be merged using the .merge() method
		 * Generally speaking, Objects whose primary keys are auto generated must be passed to the persist method
		 * in order to have their primary keys fields filled in.
		 * 
		 * @param o
		 * @return
		 */
		public abstract boolean canBeMerged (T o);
	}
