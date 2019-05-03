package com.github.arielcarrera.cdi.repositories;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.github.arielcarrera.cdi.entities.LogicalDeletion;

/**
 * Interface of a data repository that implements read operations over an entity that extends {@link LogicalDeletion}
 * 
 * Important: If it is used with WritableRepository, it must to be placed first by ambiguity resolution. Use instead {@link ReadableWritableLogicalDeletionRepository}.
 * 
 * @author Ariel Carrera
 *
 * @param <T> Type of the entity
 * @param <ID> Entity's PK
 */
@NoRepositoryBean
public interface LogicalDeletionRepository<T extends LogicalDeletion, ID extends Serializable> extends Repository<T,ID> {

	/**
	 * Find by status property
	 * @param status
	 * @return filtered list of items
	 */
	public List<T> findByStatus(int status);
	
	/**
	 * Find by status with pagination
	 * @param status
	 * @param pageable
	 * @return filtered list of items 
	 */
	public List<T> findByStatus(int status, Pageable pageable);

	/**
	 * Find by a list of statuses 
	 * @param statusList
	 * @return filtered list of items 
	 */
	public List<T> findByStatusIn(Collection<Integer> statusList);
	
	/**
	 * Find by a list of statuses with pagination
	 * @param statusList
	 * @return filtered list of items 
	 */
	public List<T> findByStatusIn(Collection<Integer> statusList, Pageable pageable);
	
	/**
	 * Find by status property not equals the given
	 * @param status
	 * @return filtered list of items
	 */
	public List<T> findByStatusNot(int status);
	
	/**
	 * Find by status property not equals the given with pagination
	 * @param status
	 * @return filtered list of items
	 */
	public List<T> findByStatusNot(int status, Pageable pageable);
	
	/**
	 * Count by status property
	 * @param status
	 * @return list of items
	 */
    public Long countByStatus(int status);
    
	/**
	 * Count results by a list of statuses
	 * @param statusList
	 * @return filtered list of items 
	 */
    public Long countByStatusIn(Collection<Integer> statusList);
    
	/**
	 * Count results by status property not equals the given
	 * @param status
	 * @return filtered list of items
	 */
    public Long countByStatusNot(int status);
    
	/**
	 * Find all normal items
	 * @param status
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusActive(){
		return findByStatus(LogicalDeletion.NORMAL_STATUS);
	}
	
	/**
	 * Find all normal items with pagination
	 * @param status
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusActive(Pageable pageable){
		return findByStatus(LogicalDeletion.NORMAL_STATUS, pageable);
	}
	
	/**
	 * Find all not deleted items
	 * @param status
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusNotDeleted(){
		return findByStatusNot(LogicalDeletion.DELETED_STATUS);
	}
	
	/**
	 * Find all not deleted items with pagination
	 * @param status
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusNotDeleted(Pageable pageable){
		return findByStatusNot(LogicalDeletion.DELETED_STATUS, pageable);
	}
	
	/**
	 * Find all draft items
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusDrafted(){
		return findByStatus(LogicalDeletion.DRAFT_STATUS);
	}
	
	/**
	 * Find all draft items with pagination
	 * @param pageable
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusDrafted(Pageable pageable){
		return findByStatus(LogicalDeletion.DRAFT_STATUS, pageable);
	}
	
	/**
	 * Find all deleted items
	 * @param status
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusDeleted(){
		return findByStatus(LogicalDeletion.DELETED_STATUS);
	}
	
	/**
	 * Find all deleted items with pagination
	 * @param pageable
	 * @return filtered list of items
	 */
	default public List<T> findAllStatusDeleted(Pageable pageable){
		return findByStatus(LogicalDeletion.DELETED_STATUS, pageable);
	}
	
	/**
	 * Count all normal items
	 * @return count
	 */
	default public Long countAllStatusActive(){
		return countByStatus(LogicalDeletion.NORMAL_STATUS);
	}

	/**
	 * Count all not deleted items
	 * @return count
	 */
	default public Long countAllStatusNotDeleted(){
		return countByStatusNot(LogicalDeletion.DELETED_STATUS);
	}
	
	/**
	 * Count all drafted items
	 * @return count
	 */
	default public Long countAllStatusDrafted(){
		return countByStatus(LogicalDeletion.DRAFT_STATUS);
	}
	
	/**
	 * Count all deleted items
	 * @return count
	 */
	default public Long countAllStatusDeleted(){
		return countByStatus(LogicalDeletion.DELETED_STATUS);
	}
	
}
