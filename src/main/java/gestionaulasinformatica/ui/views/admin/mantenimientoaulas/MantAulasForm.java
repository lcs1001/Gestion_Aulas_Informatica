package gestionaulasinformatica.ui.views.admin.mantenimientoaulas;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import gestionaulasinformatica.backend.entity.Aula;
import gestionaulasinformatica.backend.entity.PropietarioAula;
import gestionaulasinformatica.backend.service.AulaService;
import gestionaulasinformatica.ui.Comunes;
import gestionaulasinformatica.ui.Mensajes;

/**
 * Clase que contiene el formulario del Mantenimiento de Aulas.
 * 
 * @author Lisa
 *
 */
public class MantAulasForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MantAulasForm.class.getName());

	private AulaService aulaService;
	private Comunes comunes;
	private List<PropietarioAula> lstPropietarios;
	private List<PropietarioAula> lstCentros;

	protected TextField nombreAula;
	protected NumberField capacidad;
	protected NumberField numOrdenadores;
	protected ComboBox<PropietarioAula> ubicacionCentro;
	protected ComboBox<PropietarioAula> propietarioAula;

	private Button btnGuardar;
	protected Button btnEliminar;
	private Button btnCancelar;

	private Binder<Aula> binder;
	private Aula aula;
	
	private Boolean editar;

	/**
	 * Constructor de la clase.
	 * 
	 * @param propietarios Lista con todos los propietarios de aulas que hay en la
	 *                     BD
	 * @param centros      Lista con todos los centros que hay en la BD
	 */
	public MantAulasForm(AulaService aulaService, Comunes comunes, List<PropietarioAula> propietarios,
			List<PropietarioAula> centros) {
		try {
			addClassName("mant-aulas-form");

			this.aulaService = aulaService;
			this.comunes = comunes;
			this.lstPropietarios = propietarios;
			this.lstCentros = centros;
			this.editar = false;

			setResponsiveSteps(new ResponsiveStep("25em", 1), new ResponsiveStep("25em", 2),
					new ResponsiveStep("25em", 3), new ResponsiveStep("25em", 4));

			configurarCamposFormulario();

			binder = new BeanValidationBinder<>(Aula.class);
			binder.bindInstanceFields(this);

			add(nombreAula, 2);
			add(capacidad, numOrdenadores);
			add(ubicacionCentro, 2);
			add(propietarioAula, 2);
			add(getFormToolbar(), 4);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que configura los campos del formulario.
	 */
	private void configurarCamposFormulario() {
		try {
			nombreAula = new TextField("Nombre del aula");
			nombreAula.setPlaceholder("Nombre del aula");
			nombreAula.setClearButtonVisible(true);

			capacidad = new NumberField("Capacidad");
			capacidad.setMin(0);
			capacidad.setHasControls(true);

			numOrdenadores = new NumberField("Número de ordenadores");
			numOrdenadores.setMin(0);
			numOrdenadores.setHasControls(true);

			ubicacionCentro = new ComboBox<>("Centro");
			ubicacionCentro.setPlaceholder("Seleccione");
			ubicacionCentro.setItems(lstCentros);
			ubicacionCentro.setItemLabelGenerator(PropietarioAula::getNombrePropietarioAula);
			ubicacionCentro.setRequired(true);
			ubicacionCentro.setRequiredIndicatorVisible(true);

			propietarioAula = new ComboBox<>("Propietario del aula");
			propietarioAula.setPlaceholder("Seleccione");
			propietarioAula.setItems(lstPropietarios);
			propietarioAula.setItemLabelGenerator(PropietarioAula::getNombrePropietarioAula);
			propietarioAula.setRequired(true);
			propietarioAula.setRequiredIndicatorVisible(true);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que crea el layout de botones para guardar, eliminar o cerrar el
	 * editor.
	 * 
	 * @return Layout de botones
	 */
	private HorizontalLayout getFormToolbar() {
		HorizontalLayout formToolbar;

		try {
			btnGuardar = new Button("Guardar");
			btnGuardar.setIcon(new Icon(VaadinIcon.CHECK));
			btnGuardar.addClickListener(click -> validarGuardar());
			btnGuardar.addClickShortcut(Key.ENTER); // Se guarda al pulsar Enter en el teclado
			btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

			btnEliminar = new Button("Eliminar");
			btnEliminar.setIcon(new Icon(VaadinIcon.TRASH));
			btnEliminar.addClickListener(click -> fireEvent(new DeleteEvent(this, aula)));
			btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

			btnCancelar = new Button("Cancelar");
			btnCancelar.setIcon(new Icon(VaadinIcon.CLOSE));
			btnCancelar.addClickListener(click -> fireEvent(new CloseEvent(this)));
			btnCancelar.addClickShortcut(Key.ESCAPE); // Se cierra al pulsar ESC en el teclado
			btnCancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);

			binder.addStatusChangeListener(evt -> btnGuardar.setEnabled(binder.isValid()));

			formToolbar = new HorizontalLayout(btnGuardar, btnEliminar, btnCancelar);
			formToolbar.addClassName("toolbar");

			return formToolbar;

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que establece el aula actual del binder.
	 * 
	 * @param aula Aula actual
	 */
	public void setAula(Aula aula, Boolean editar) {
		try {
			this.aula = aula;
			this.editar = editar;
			binder.readBean(aula);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Función que comprueba que el aula que se quiere guardar es válida.
	 * 
	 * @return Si el aula es válida o no
	 */
	private Boolean validarAula() {
		Boolean valida = true;
		Aula aulaBuscar;
		try {			
			aulaBuscar = aulaService.findByNombreAulaIgnoreCaseAndUbicacionCentro(nombreAula.getValue(),
					ubicacionCentro.getValue());
			
			// Si existe un aula con el nombre introducido en el centro seleccionado (y no
			// se trata de una modificación de aula)
			if ((aulaBuscar != null) & !editar) {
				comunes.mostrarNotificacion(Mensajes.MSG_AULA_CENTRO_EXISTENTE.getMensaje(), 3000,
						NotificationVariant.LUMO_ERROR);
				valida = false;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}

		return valida;
	}

	/**
	 * Función que valida el aula y la guarda (si es válido).
	 */
	private void validarGuardar() {
		try {
			if (validarAula()) {
				binder.writeBean(aula);
				fireEvent(new SaveEvent(this, aula));
			}
		} catch (ValidationException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Clase estática y abstracta para definir los eventos del formulario de
	 * Mantenimiento de Aulas.
	 * 
	 * @author Lisa
	 *
	 */
	public static abstract class MantAulasFormEvent extends ComponentEvent<MantAulasForm> {
		private static final long serialVersionUID = 1L;
		private Aula aula;

		/**
		 * Constructor de la clase.
		 * 
		 * @param source Origen
		 * @param aula   Aula
		 */
		protected MantAulasFormEvent(MantAulasForm source, Aula aula) {
			super(source, false);
			this.aula = aula;
		}

		/**
		 * Función que devuelve el aula asociada a la clase.
		 * 
		 * @return Aula asociada a la clase
		 */
		public Aula getAula() {
			return aula;
		}
	}

	/**
	 * Clase estática para definir el evento "Guardar" del formulario de
	 * Mantenimiento de Aulas.
	 * 
	 * @author Lisa
	 *
	 */
	public static class SaveEvent extends MantAulasFormEvent {
		private static final long serialVersionUID = 1L;

		SaveEvent(MantAulasForm source, Aula aula) {
			super(source, aula);
		}
	}

	/**
	 * Clase estática para definir el evento "Eliminar" del formulario de
	 * Mantenimiento de Aulas.
	 * 
	 * @author Lisa
	 *
	 */
	public static class DeleteEvent extends MantAulasFormEvent {
		private static final long serialVersionUID = 1L;

		DeleteEvent(MantAulasForm source, Aula aula) {
			super(source, aula);
		}

	}

	/**
	 * Clase estática para definir el evento "Cerrar" del formulario de
	 * Mantenimiento de Aulas.
	 * 
	 * @author Lisa
	 *
	 */
	public static class CloseEvent extends MantAulasFormEvent {
		private static final long serialVersionUID = 1L;

		CloseEvent(MantAulasForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}

}
