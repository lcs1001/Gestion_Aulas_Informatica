package com.vaadin.gestionaulasinformatica.ui.views.mantenimientopropietarios;

// Imports Vaadin
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

// Imports backend
import com.vaadin.gestionaulasinformatica.backend.entity.PropietarioAula;

/**
 * Clase que contiene el formulario del Mantenimiento de Centros y
 * Departamentos.
 * 
 * @author Lisa
 *
 */
public class MantPropietariosForm extends FormLayout {
	private static final long serialVersionUID = 1L;

	protected TextField idPropAula ;
	protected TextField nombrePropAula ;
	protected TextField nombreResponsable;
	protected TextField apellidosResponsable ;
	protected EmailField correoResponsable;
	protected TextField telefonoResponsable;

	private Button btnGuardar = new Button("Guardar");
	private Button btnEliminar = new Button("Eliminar");
	private Button btnCerrar = new Button("Cancelar");

	private Binder<PropietarioAula> binder = new BeanValidationBinder<>(PropietarioAula.class);

	/**
	 * Constructor de la clase.
	 */
	public MantPropietariosForm() {
		try {
			addClassName("mant-propietarios-form");

			configurarCamposFormulario();

			binder.bindInstanceFields(this);
			binder.bind(idPropAula, "idPropietarioAula");
			binder.bind(nombrePropAula, "nombrePropietarioAula");	

			add(idPropAula, nombrePropAula, nombreResponsable, apellidosResponsable, correoResponsable,
					telefonoResponsable, crearButtonsLayout());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que configura los campos del formulario.
	 */
	private void configurarCamposFormulario() {
		try {
			idPropAula= new TextField("ID del Centro/Departamento");
			nombrePropAula = new TextField("Nombre del Centro/Departamento");
			nombreResponsable = new TextField("Nombre del Responsable");
			apellidosResponsable = new TextField("Apellidos del Responsable");
			correoResponsable = new EmailField("Correo del Responsable");
			
			telefonoResponsable = new TextField("Teléfono del Responsable");
			telefonoResponsable.setMinLength(9);
			telefonoResponsable.setMaxLength(9);
		}catch(Exception e) {
			throw e;
		}		
	}

	/**
	 * Función que establece el propietario actual del binder.
	 * 
	 * @param propietario Propietario actual
	 */
	public void setPropietarioAula(PropietarioAula propietario) {
		try {
			binder.setBean(propietario);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que crea el layout de botones para guardar, eliminar o cerrar el
	 * editor.
	 * 
	 * @return Layout de botones
	 */
	private Component crearButtonsLayout() {
		try {
			btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnCerrar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

			// Se guarda al pulsar Enter en el teclado
			btnGuardar.addClickShortcut(Key.ENTER);
			// Se cierra al pulsar ESC en el teclado
			btnCerrar.addClickShortcut(Key.ESCAPE);

			btnGuardar.addClickListener(click -> validarGuardar());
			btnEliminar.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
			btnCerrar.addClickListener(click -> fireEvent(new CloseEvent(this)));

			binder.addStatusChangeListener(evt -> btnGuardar.setEnabled(binder.isValid()));

			return new HorizontalLayout(btnGuardar, btnEliminar, btnCerrar);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que valida el propietario y lo guarda (si es válido).
	 */
	private void validarGuardar() {
		try {
			if (binder.isValid()) {
				fireEvent(new SaveEvent(this, binder.getBean()));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Clase estática y abstracta para definir los eventos del formulario de
	 * Mantenimiento de Centros y Departamentos.
	 * 
	 * @author Lisa
	 *
	 */
	public static abstract class MantPropietariosAulaFormEvent extends ComponentEvent<MantPropietariosForm> {
		private static final long serialVersionUID = 1L;
		private PropietarioAula propietario;

		/**
		 * Constructor de la clase.
		 * 
		 * @param source      Origen
		 * @param propietario Propietario de aulas
		 */
		protected MantPropietariosAulaFormEvent(MantPropietariosForm source, PropietarioAula propietario) {
			super(source, false);
			this.propietario = propietario;
		}

		/**
		 * Función que devuelve el propietario de aulas asociado a la clase.
		 * 
		 * @return Propietario de aulas asociado a la clase
		 */
		public PropietarioAula getPropietarioAula() {
			return propietario;
		}
	}

	/**
	 * Clase estática para definir el evento "Guardar" del formulario de
	 * Mantenimiento de Centros y Departamentos.
	 * 
	 * @author Lisa
	 *
	 */
	public static class SaveEvent extends MantPropietariosAulaFormEvent {
		private static final long serialVersionUID = 1L;

		SaveEvent(MantPropietariosForm source, PropietarioAula propietario) {
			super(source, propietario);
		}
	}

	/**
	 * Clase estática para definir el evento "Eliminar" del formulario de
	 * Mantenimiento de Centros y Departamentos.
	 * 
	 * @author Lisa
	 *
	 */
	public static class DeleteEvent extends MantPropietariosAulaFormEvent {
		private static final long serialVersionUID = 1L;

		DeleteEvent(MantPropietariosForm source, PropietarioAula propietario) {
			super(source, propietario);
		}

	}

	/**
	 * Clase estática para definir el evento "Cerrar" del formulario de
	 * Mantenimiento de Centros y Departamentos.
	 * 
	 * @author Lisa
	 *
	 */
	public static class CloseEvent extends MantPropietariosAulaFormEvent {
		private static final long serialVersionUID = 1L;

		CloseEvent(MantPropietariosForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}