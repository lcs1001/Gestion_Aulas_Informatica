package gestionaulasinformatica.ui.views.responsable.gestionreservas;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.timepicker.TimePicker;

import gestionaulasinformatica.ui.Comunes;

/**
 * Clase que contiene el formulario para gestionar las reservas.
 * 
 * @author Lisa
 *
 */
public class GestionReservasBusquedaForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(GestionReservasBusquedaForm.class.getName());

	private Comunes comunes;

	protected DatePicker fechaDesde;
	protected DatePicker fechaHasta;
	protected TimePicker horaDesde;
	protected TimePicker horaHasta;
	protected ComboBox<String> diaSemana;

	/**
	 * Constructor de la clase.
	 * 
	 * @param comunes Objeto Comunes para tener acceso a las funciones comunes
	 */
	public GestionReservasBusquedaForm(Comunes comunes) {
		try {
			addClassName("gestion-reservas-busqueda-form");

			this.comunes = comunes;

			setResponsiveSteps(new ResponsiveStep("25em", 1), new ResponsiveStep("25em", 2),
					new ResponsiveStep("25em", 3), new ResponsiveStep("25em", 4));

			configurarFiltros();

			add(fechaDesde, horaDesde);
			add(diaSemana, 2);
			add(fechaHasta, horaHasta);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que configura los campos de filtrado.
	 */
	private void configurarFiltros() {
		try {
			fechaDesde = new DatePicker("Fecha desde");
			fechaDesde.setValue(LocalDate.now()); // Por defecto la fecha actual
			fechaDesde.setMin(LocalDate.now()); // Como mínimo la fecha actual
			fechaDesde.setLocale(comunes.getLocaleES()); // Formato dd/M/yyyy

			fechaHasta = new DatePicker("Fecha hasta");
			fechaHasta.setMin(fechaDesde.getValue()); // Como mínimo debe ser la fecha desde la que se ha filtrado
			fechaHasta.setPlaceholder("dd/MM/yyyy");
			fechaHasta.setLocale(comunes.getLocaleES()); // Formato dd/M/yyyy
			fechaHasta.setClearButtonVisible(true);

			horaDesde = new TimePicker("Hora desde");
			horaDesde.setPlaceholder("hh:mm");
			horaDesde.setLocale(comunes.getLocaleES());
			horaDesde.setClearButtonVisible(true);

			horaHasta = new TimePicker("Hora hasta");
			horaHasta.setPlaceholder("hh:mm");
			horaHasta.setLocale(comunes.getLocaleES());
			horaHasta.setClearButtonVisible(true);

			diaSemana = new ComboBox<String>("Día de la semana");
			diaSemana.setPlaceholder("Seleccione");
			diaSemana.setItems(comunes.getDiasSemana());
			diaSemana.setClearButtonVisible(true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que limpia todos los filtros aplicados, elimina sus valores o
	 * establece los de por defecto.
	 */
	protected void limpiarFiltros() {
		try {
			fechaDesde.setValue(LocalDate.now());
			fechaHasta.clear();
			horaDesde.clear();
			horaHasta.clear();
			diaSemana.clear();
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}
}
