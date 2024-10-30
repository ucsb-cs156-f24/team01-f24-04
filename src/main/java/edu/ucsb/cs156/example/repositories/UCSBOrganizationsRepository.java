package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// public class UCSBOrganizationsRepository {
    
// }

@Repository
public interface UCSBOrganizationsRepository extends CrudRepository<UCSBOrganizations, String> {
 
}