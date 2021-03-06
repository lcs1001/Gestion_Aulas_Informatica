package gestionaulasinformatica.backend.specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import gestionaulasinformatica.backend.entity.PropietarioAula;
import gestionaulasinformatica.backend.entity.Reserva;

/**
 * Clase que contiene las especificaciones para filtrar las reservas.
 * 
 * @author Lisa
 *
 */
public class ReservaSpecification {

	/**
	 * Función que devuelve todas las reservas que cumplen con los filtros
	 * aplicados.
	 * 
	 * @param fechaDesde           Fecha (de inicio) desde la que obtener las
	 *                             reservas
	 * @param fechaHasta           Fecha (de inicio) hasta la que obtener las
	 *                             reservas
	 * @param horaDesde            Hora (de inicio) de la reserva desde la que
	 *                             obtener las reservas
	 * @param horaHasta            Hora (de inicio) de la reserva hasta la que
	 *                             obtener las reservas
	 * @param diaSemana            Día de la semana del que obtener las reservas
	 * @param lstPropietariosAulas Lista de posibles propietarios del aula de la
	 *                             reserva de los que obtener las reservas
	 * 
	 * @return Reservas que cumplen con los filtros aplicados
	 */
	public static Specification<Reserva> findByFilters(LocalDate fechaDesde, LocalDate fechaHasta, LocalTime horaDesde,
			LocalTime horaHasta, String diaSemana, List<PropietarioAula> lstPropietariosAulas) {
		return new Specification<Reserva>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Reserva> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				final List<Predicate> predicates = new ArrayList<>();

				// Se obtienen las reservas realizadas desde una fecha determinada
				if (!StringUtils.isEmpty(fechaDesde)) {
					final Predicate fechaDesdePredicate = cb.greaterThanOrEqualTo(root.get("fecha"), fechaDesde);
					predicates.add(fechaDesdePredicate);
				}

				// Se obtienen las reservas realizadas hasta una fecha determinada
				if (!StringUtils.isEmpty(fechaHasta)) {
					final Predicate fechaHastaPredicate = cb.lessThanOrEqualTo(root.get("fecha"), fechaHasta);
					predicates.add(fechaHastaPredicate);
				}

				// Se obtienen las reservas realizadas a partir de una hora (de inicio) del día
				if (!StringUtils.isEmpty(horaDesde)) {
					final Predicate horaDesdePredicate = cb.greaterThanOrEqualTo(root.get("horaInicio"), horaDesde);
					predicates.add(horaDesdePredicate);
				}

				// Se obtienen las reservas realizadas hasta una hora (de inicio) del día
				if (!StringUtils.isEmpty(horaHasta)) {
					final Predicate horaHastaPredicate = cb.lessThanOrEqualTo(root.get("horaInicio"), horaHasta);
					predicates.add(horaHastaPredicate);
				}

				// Se obtienen las reservas realizadas un determinado día de la semana
				if (!StringUtils.isEmpty(diaSemana)) {
					final Predicate diaSemanaPredicate = cb.equal(root.get("diaSemana"), diaSemana);
					predicates.add(diaSemanaPredicate);
				}

				// Se obtienen las reservas realizadas de un determinado centro o departamento
				// (propietario del aula de la reserva)
//				if (!StringUtils.isEmpty(responsable)) {
//					final Predicate responsablePredicate = cb.equal(root.get("propietarioResponsable"), responsable);
//					predicates.add(responsablePredicate);
//				}

				// Se obtienen las reservas realizadas de el/los centro/s o departamento/s
				// (propietario del aula de la reserva)
				if (!lstPropietariosAulas.isEmpty()) {
					final Predicate propietariosAulaPredicate = root.get("propietarioResponsable")
							.in(lstPropietariosAulas);
					predicates.add(propietariosAulaPredicate);
				}

				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};
	}
}
