package io.fabric8.quickstarts.camel.db.repo.crmp;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.fabric8.quickstarts.camel.db.model.crmp.CrmViewQuery;


/**
 * The Class EhubPropertiesDAO.
 */
@Repository
public interface CrmViewQueriesRepository extends CrudRepository<CrmViewQuery, Long> {

  @Override
  void delete(CrmViewQuery deleted);

  @Override
  List<CrmViewQuery> findAll();

  @Override
  CrmViewQuery findOne(Long id);

  @Override
  CrmViewQuery save(CrmViewQuery persisted);

  public CrmViewQuery findByName(String name);

  public CrmViewQuery findBySorAndName(String sor, String name);
  
  public CrmViewQuery findBySorAndNameIgnoreCase(String sor, String name);

  @Transactional
  @Modifying
  @Query("delete from CrmViewQuery o where o.sor = :sor and o.name=:name")
  public int deleteBySorAndName(@Param("sor") String sor, @Param("name") String name);

  @Query("select new io.fabric8.quickstarts.camel.db.model.crmp.CrmViewQuery(o.id, o.name) from CrmViewQuery o")
  List<CrmViewQuery> findNames();

  List<CrmViewQuery> findBySor(String sor);

  @Query("select new io.fabric8.quickstarts.camel.db.model.crmp.CrmViewQuery(o.id, o.name) from CrmViewQuery o where o.sor=:sor")
  List<CrmViewQuery> findNamesBySor(@Param("sor") String sor);

  @Query("select new io.fabric8.quickstarts.camel.db.model.crmp.CrmViewQuery(o.id, o.name, o.sor) from CrmViewQuery o")
  List<CrmViewQuery> findAllSor();

  @Query("select o from CrmViewQuery o WHERE o.sor=:sor and o.name in (:names) ")
  List<CrmViewQuery> findBySorAndNames(@Param("sor") String sor, @Param("names") List<String> names);
}
