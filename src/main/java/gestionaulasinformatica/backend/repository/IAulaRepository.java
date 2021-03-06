package gestionaulasinformatica.backend.repository;

// Imports Java
import java.util.List;

// Imports SpringFramework
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import gestionaulasinformatica.backend.entity.Aula;
import gestionaulasinformatica.backend.entity.PropietarioAula;

/**
 * Repositorio para la entidad Aula con clave primaria de tipo AulaPK.
 * 
 * @author Lisa
 *
 */
public interface IAulaRepository extends JpaRepository<Aula, Integer>, JpaSpecificationExecutor<Aula> {
	@Query("SELECT a FROM Aula a "
			+ "WHERE lower(a.propietarioAula.idPropietarioAula) LIKE lower(:filtroPropietarioAula) ORDER BY nombreAula ASC")
	List<Aula> findAllAulasPropietario(@Param("filtroPropietarioAula") PropietarioAula filtroPropietarioAula);
	
	Aula findByNombreAulaIgnoreCaseAndUbicacionCentro(String nombreAula, PropietarioAula ubicacionCentro);
}
