package gestionaulasinformatica.ui.views.reservaaulas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import gestionaulasinformatica.backend.entity.Aula;
import gestionaulasinformatica.backend.entity.PropietarioAula;
import gestionaulasinformatica.backend.entity.Reserva;
import gestionaulasinformatica.backend.service.AulaService;
import gestionaulasinformatica.ui.Comunes;

/**
 * Clase que contiene el formulario para reservar aulas.
 * 
 * @author Lisa
 *
 */
public class ReservaAulasForm extends FormLayout {

	private static final long serialVersionUID = 1L;

	private AulaService aulaService;
	private List<PropietarioAula> lstCentros;
	private Comunes comunes;

	protected DatePicker fechaInicio;
	protected DatePicker fechaFin;
	protected TimePicker horaInicio;
	protected TimePicker horaFin;
	protected ComboBox<PropietarioAula> centro;
	protected ComboBox<Aula> aula;
	protected ComboBox<String> diaSemana;
	protected TextField motivo;
	protected TextField aCargoDe;

	protected HorizontalLayout toolbar;
	private Button btnReservar;
	private Button btnLimpiarCampos;
	private Checkbox chkReservaRango;

	private Binder<Reserva> binder;
	private Reserva reserva;

	/**
	 * Constructor de la clase.
	 * 
	 * @param aulaService Service de JPA de la entidad Aula
	 * @param centros     Lista de centros que se muestra en el desplegable de
	 *                    centros
	 * @param comunes     Objeto de la clase Comunes para acceder a las funciones
	 *                    comunes
	 */
	public ReservaAulasForm(AulaService aulaService, List<PropietarioAula> centros, Comunes comunes) {
		try {
			addClassName("reserva-aulas-form");

			this.aulaService = aulaService;
			this.lstCentros = centros;
			this.comunes = comunes;

			setResponsiveSteps(new ResponsiveStep("25em", 1), new ResponsiveStep("25em", 2),
					new ResponsiveStep("25em", 3), new ResponsiveStep("25em", 4));

			configurarCampos();
			configurarToolbar();

			binder = new BeanValidationBinder<>(Reserva.class);
			binder.bindInstanceFields(this);

			add(fechaInicio, horaInicio);
			add(centro, 2);
			add(fechaFin, horaFin);
			add(aula, 2);
			add(diaSemana, 2);
			add(motivo, 2);
			add(aCargoDe, 2);
			add(toolbar, 4);

			// Se deshabilitan los campos "Fecha fin" y "Día de la semana" hasta que se
			// marca la reserva por rango de fechas
			fechaFin.setEnabled(false);
			diaSemana.setEnabled(false);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que configura los campos del formulario.
	 */
	private void configurarCampos() {
		try {
			fechaInicio = new DatePicker("Fecha");
			fechaInicio.setMin(LocalDate.now()); // Como mínimo debe ser la fecha actual
			fechaInicio.setPlaceholder("dd/MM/yyyy");
			fechaInicio.setLocale(comunes.getLocaleES()); // Formato dd/M/yyyy
			fechaInicio.setClearButtonVisible(true);

			fechaFin = new DatePicker("Fecha fin");
			fechaFin.setMin(fechaInicio.getValue()); // Como mínimo debe ser la fecha desde la que se ha filtrado
			fechaFin.setPlaceholder("dd/MM/yyyy");
			fechaFin.setLocale(comunes.getLocaleES()); // Formato dd/M/yyyy
			fechaFin.setClearButtonVisible(true);

			horaInicio = new TimePicker("Hora inicio");
			horaInicio.setMinTime(LocalTime.now());
			horaInicio.setPlaceholder("hh:mm");
			horaInicio.setLocale(comunes.getLocaleES());
			horaInicio.setClearButtonVisible(true);

			horaFin = new TimePicker("Hora fin");
			horaFin.setPlaceholder("hh:mm");
			horaFin.setLocale(comunes.getLocaleES());
			horaFin.setClearButtonVisible(true);

			centro = new ComboBox<PropietarioAula>("Centro");
			centro.setPlaceholder("Seleccione");
			centro.setItems(lstCentros);
			centro.setItemLabelGenerator(PropietarioAula::getNombrePropietarioAula);
			centro.addValueChangeListener(e -> cargarAulasCentro());

			aula = new ComboBox<Aula>("Aula");
			aula.setPlaceholder("Seleccione");
			aula.setItemLabelGenerator(Aula::getNombreAula);

			diaSemana = new ComboBox<String>("Día de la semana");
			diaSemana.setPlaceholder("Seleccione");
			diaSemana.setItems(comunes.getDiasSemana());

			motivo = new TextField("Motivo");
			motivo.setPlaceholder("Motivo de la reserva");
			motivo.setMaxLength(50);
			motivo.setClearButtonVisible(true);

			aCargoDe = new TextField("A cargo de");
			aCargoDe.setPlaceholder("Persona a cargo de la reserva");
			aCargoDe.setMaxLength(50);
			aCargoDe.setClearButtonVisible(true);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que configura el toolbar que contiene: un botón para generar la reserva,
	 * un botón para limpiar todos los campos y un checkbox para indicar si se trata
	 * de una reserva por rango de fechas.
	 * 
	 * @return Toolbar
	 */
	private void configurarToolbar() {
		try {
			btnReservar = new Button("Reservar", click -> validarGuardar());
			btnReservar.addClickShortcut(Key.ENTER); // Se guarda al pulsar Enter en el teclado
			btnReservar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

			btnLimpiarCampos = new Button("", click -> limpiarCampos());
			btnLimpiarCampos.setIcon(new Icon(VaadinIcon.CLOSE));
			btnLimpiarCampos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			btnLimpiarCampos.getElement().setProperty("title", "Limpiar campos");

			chkReservaRango = new Checkbox("Reserva por Rango de Fechas");
			chkReservaRango.addClickListener(e -> reservaRango());

			toolbar = new HorizontalLayout(btnReservar, chkReservaRango);
			toolbar.addClassName("toolbar");

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Carga las aulas del centro seleccionado en el desplegable de aulas.
	 */
	private void cargarAulasCentro() {
		List<Aula> lstAulas;
		try {
			if (!centro.isEmpty()) {
				lstAulas = aulaService.findAll(centro.getValue());
				aula.setItems(lstAulas);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que muestra u oculta los campos de las reservas de rango (fecha fin y
	 * día de la semana) dependiendo de si se ha seleccionado el checkbox o no.
	 */
	private void reservaRango() {
		try {
			// Si no está seleccionado
			if (chkReservaRango.isEmpty()) {
				fechaInicio.setLabel("Fecha");
				fechaFin.setEnabled(false);
				diaSemana.setEnabled(false);
			} else {
				fechaInicio.setLabel("Fecha inicio");
				fechaFin.setEnabled(true);
				diaSemana.setEnabled(true);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que limpia todos los campos.
	 */
	protected void limpiarCampos() {
		try {
			fechaInicio.clear();
			fechaFin.clear();
			horaInicio.clear();
			horaFin.clear();
			centro.clear();
			aula.clear();
			diaSemana.clear();
			motivo.clear();
			aCargoDe.clear();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que establece la reserva actual del binder.
	 * 
	 * @param reserva Reserva actual
	 */
	public void setReserva(Reserva reserva) {
		try {
			this.reserva = reserva;
			binder.readBean(reserva);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que valida la reserva y la guarda (si es válido).
	 */
	private void validarGuardar() {
		try {
			binder.writeBean(reserva);
			fireEvent(new SaveEvent(this, reserva));

		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clase estática y abstracta para definir los eventos del formulario de Gestión
	 * de Reservas.
	 * 
	 * @author Lisa
	 *
	 */
	public static abstract class ReservaAulasFormEvent extends ComponentEvent<ReservaAulasForm> {
		private static final long serialVersionUID = 1L;
		private Reserva reserva;

		/**
		 * Constructor de la clase.
		 * 
		 * @param source  Origen
		 * @param reserva Reserva
		 */
		protected ReservaAulasFormEvent(ReservaAulasForm source, Reserva reserva) {
			super(source, false);
			this.reserva = reserva;
		}

		/**
		 * Función que devuelve la reserva asociada a la clase.
		 * 
		 * @return Reserva asociada a la clase
		 */
		public Reserva getReserva() {
			return reserva;
		}
	}

	/**
	 * Clase estática para definir el evento "Reservar" del formulario de Reserva de
	 * Aulas.
	 * 
	 * @author Lisa
	 *
	 */
	public static class SaveEvent extends ReservaAulasFormEvent {
		private static final long serialVersionUID = 1L;

		SaveEvent(ReservaAulasForm source, Reserva reserva) {
			super(source, reserva);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
