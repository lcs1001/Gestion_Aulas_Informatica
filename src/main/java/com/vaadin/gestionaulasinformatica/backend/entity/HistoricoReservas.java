package com.vaadin.gestionaulasinformatica.backend.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entidad que identifica a la tabla HistoricoReserva de la base de datos.
 * 
 * @author Lisa
 *
 */
@Entity
@Table(name = "historico_reservas", schema = "public")
public class HistoricoReservas implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private HistoricoReservasPK idOperacionHR;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_operacion")
	private Date fechaOperacion;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "responsable_operacion", referencedColumnName = "id_propietario_aula", insertable = false, updatable = false)
	private PropietarioAula responsableOperacion;

	/**
	 * Función que devuelve el id de la operación del histórico de reservas.
	 * 
	 * @return ID de la operación del histórico de reservas
	 */
	public HistoricoReservasPK getIdOperacionHR() {
		return this.idOperacionHR;
	}

	/**
	 * Función que establece el id de la operación del histórico de reservas.
	 * 
	 * @param reserva       Reserva sobre la que se ha realizado la operación
	 * @param tipoOperacion Tipo de operación que se ha realizado
	 */
	public void setIdOperacionHR(Reserva reserva, TipoOperacionHR tipoOperacion) {
		this.idOperacionHR.setReserva(reserva);
		this.idOperacionHR.setTipoOperacion(tipoOperacion);
	}

	/**
	 * Función que devuelve la fecha en que se ha realizado la operación.
	 * 
	 * @return Fecha en que se ha realizado la operación
	 */
	public Date getFechaOperacion() {
		return this.fechaOperacion;
	}

	/**
	 * Función que establece la fecha en que se ha realizado la operación.
	 * 
	 * @param fecha Fecha en que se ha realizado la operación
	 */
	public void setFechaOperacion(Date fecha) {
		this.fechaOperacion = fecha;
	}

	/**
	 * Función que devuelve el responsable de la operación que se ha realizado.
	 * 
	 * @return Responsable de la operación que se ha realizado
	 */
	public PropietarioAula getResponsableOperacion() {
		return this.responsableOperacion;
	}

	/**
	 * Función que establece el responsable de la operación que se ha realizado.
	 * 
	 * @param responsableOperacion Responsable de la operación que se ha realizado
	 */
	public void setResponsableOperacion(PropietarioAula responsableOperacion) {
		this.responsableOperacion = responsableOperacion;

	}
}