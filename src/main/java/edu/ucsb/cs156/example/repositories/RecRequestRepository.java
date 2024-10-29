package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.RecRequest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The RecRequestRepository is a repository for RecRequest entities
 */
@Repository
public interface RecRequestRepository extends CrudRepository<RecRequest, Long> {
}
