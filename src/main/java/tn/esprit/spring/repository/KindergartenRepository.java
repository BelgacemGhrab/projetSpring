package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.spring.entity.Kindergarten;

@Repository
public interface KindergartenRepository extends  JpaRepository<Kindergarten, Long> {
	@Query(value = "SELECT count(pk.parents_id) from parent_kindergarten pk WHERE pk.kindergarten_id= ?1" , nativeQuery =true)
	public int countparent(Long idk);
}
