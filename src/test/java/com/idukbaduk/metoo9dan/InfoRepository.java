package com.idukbaduk.metoo9dan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {

    //@PersistenceContext
    //public Long save(Info info);

    //public Info findById(Long id);
}
