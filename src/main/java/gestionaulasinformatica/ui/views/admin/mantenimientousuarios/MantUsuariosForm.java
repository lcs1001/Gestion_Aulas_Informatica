package gestionaulasinformatica.ui.views.admin.mantenimientousuarios;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;

import gestionaulasinformatica.backend.data.Rol;
import gestionaulasinformatica.backend.entity.Usuario;

public class MantUsuariosForm extends FormLayout {
	private static final long serialVersionUID = 1L;

	protected TextField nombreUsuario;
	protected TextField apellidosUsuario;
	protected EmailField correoUsuario;
	protected PasswordField contrasena;
	protected TextField telefonoUsuario;
	protected ComboBox<String> rolUsuario;
	protected Checkbox chkBloqueado;

	private Button btnGuardar;
	protected Button btnEliminar;
	private Button btnCancelar;

	private Binder<Usuario> binder;
	private Usuario usuario;

	/**
	 * Constructor de la clase.
	 */
	public MantUsuariosForm(PasswordEncoder passwordEncoder) {
		try {
			addClassName("mant-usuarios-form");

			setResponsiveSteps(new ResponsiveStep("25em", 1), new ResponsiveStep("25em", 2),
					new ResponsiveStep("25em", 3), new ResponsiveStep("25em", 4));

			configurarCamposFormulario();

			binder = new BeanValidationBinder<>(Usuario.class);
			binder.bindInstanceFields(this);
			binder.bind(chkBloqueado, "bloqueado");
			binder.forField(contrasena)
					.withValidator(cont -> cont.matches("^(|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,})$"),
							"Debe contener 6 o más caracteres, incluyendo dígitos, letras minúsculas y mayúsculas")
					.bind(user -> contrasena.getEmptyValue(), (user, cont) -> {
						if (!contrasena.getEmptyValue().equals(cont)) {
							user.setContrasenaHash(passwordEncoder.encode(cont));
						}
					});

			add(nombreUsuario, 2);
			add(apellidosUsuario, 2);
			add(correoUsuario, 2);
			add(contrasena, 2);
			add(telefonoUsuario, 2);
			add(rolUsuario, chkBloqueado);
			add(getFormToolbar(), 4);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que configura los campos del formulario.
	 */
	private void configurarCamposFormulario() {
		try {
			nombreUsuario = new TextField("Nombre del usuario");
			nombreUsuario.setClearButtonVisible(true);

			apellidosUsuario = new TextField("Apellidos del usuario");
			apellidosUsuario.setClearButtonVisible(true);

			correoUsuario = new EmailField("Correo del usuario");
			correoUsuario.setClearButtonVisible(true);

			contrasena = new PasswordField("Contraseña");

			telefonoUsuario = new TextField("Teléfono del usuario");
			telefonoUsuario.setClearButtonVisible(true);
			telefonoUsuario.setMinLength(9);
			telefonoUsuario.setMaxLength(9);

			rolUsuario = new ComboBox<String>("Rol del usuario");

			ListDataProvider<String> rolProvider = DataProvider.ofItems(Rol.getAllRoles());
			rolUsuario.setItemLabelGenerator(s -> s != null ? s : "");
			rolUsuario.setDataProvider(rolProvider);

			chkBloqueado = new Checkbox("Bloqueado");
			chkBloqueado.addClickListener(e -> bloquearUsuario());

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
	private HorizontalLayout getFormToolbar() {
		HorizontalLayout formToolbar;

		try {

			btnGuardar = new Button("Guardar");
			btnGuardar.addClickListener(click -> validarGuardar());
			btnGuardar.addClickShortcut(Key.ENTER); // Se guarda al pulsar Enter en el teclado
			btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

			btnEliminar = new Button("Eliminar");
			btnEliminar.addClickListener(click -> fireEvent(new DeleteEvent(this, usuario)));
			btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

			btnCancelar = new Button("Cancelar");
			btnCancelar.addClickListener(click -> fireEvent(new CloseEvent(this)));
			btnCancelar.addClickShortcut(Key.ESCAPE); // Se cierra al pulsar ESC en el teclado
			btnCancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);

			binder.addStatusChangeListener(evt -> btnGuardar.setEnabled(binder.isValid()));

			formToolbar = new HorizontalLayout(btnGuardar, btnEliminar, btnCancelar);
			formToolbar.addClassName("toolbar");

			return formToolbar;

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que bloquea o desbloquea al usuario cuando se marca o desmarca el
	 * check, mostrando un mensaje de confirmación en un cuadro de diálogo.
	 */
	private void bloquearUsuario() {
		Dialog confirmacion;
		String mensajeConfirmacion;
		Button btnConfirmar;
		Button btnCancelar;
		Boolean bloqueado;
		try {
			if (!chkBloqueado.isEmpty()) {
				mensajeConfirmacion = "¿Seguro que quiere desbloquear al usuario " + usuario.getNombreApellidosUsuario() + "?";
				bloqueado = false;
			} else {
				mensajeConfirmacion = "¿Seguro que quiere bloquear al usuario " + usuario.getNombreApellidosUsuario() + "?";
				bloqueado = true;
			}
			
			confirmacion = new Dialog(new Label(mensajeConfirmacion));
			confirmacion.setCloseOnEsc(false);
			confirmacion.setCloseOnOutsideClick(false);

			btnConfirmar = new Button("Confirmar", event -> {
				usuario.setBloqueado(bloqueado);
				confirmacion.close();
			});
			btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			btnConfirmar.addClassName("margin-20");

			btnCancelar = new Button("Cancelar", event -> {
				//TODO : qué hacer si se cancela la opció
				confirmacion.close();
			});
			btnCancelar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

			confirmacion.add(btnConfirmar, btnCancelar);

			confirmacion.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Función que establece el usuario actual del binder.
	 * 
	 * @param usuario Usuario actual
	 */
	public void setUsuario(Usuario usuario) {
		try {
			this.usuario = usuario;
			binder.readBean(usuario);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Función que valida el usuario y lo guarda.
	 */
	private void validarGuardar() {
		try {
			binder.writeBean(usuario);
			fireEvent(new SaveEvent(this, usuario));

		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clase estática y abstracta para definir los eventos del formulario de
	 * Mantenimiento de Usuarios.
	 * 
	 * @author Lisa
	 *
	 */
	public static abstract class MantUsuarioFormEvent extends ComponentEvent<MantUsuariosForm> {
		private static final long serialVersionUID = 1L;
		private Usuario usuario;

		/**
		 * Constructor de la clase.
		 * 
		 * @param source  Origen
		 * @param usuario Usuario
		 */
		protected MantUsuarioFormEvent(MantUsuariosForm source, Usuario usuario) {
			super(source, false);
			this.usuario = usuario;
		}

		/**
		 * Función que devuelve el usuario asociado a la clase.
		 * 
		 * @return Usuario asociado a la clase
		 */
		public Usuario getUsuario() {
			return usuario;
		}
	}

	/**
	 * Clase estática para definir el evento "Guardar" del formulario de
	 * Mantenimiento de Usuarios.
	 * 
	 * @author Lisa
	 *
	 */
	public static class SaveEvent extends MantUsuarioFormEvent {
		private static final long serialVersionUID = 1L;

		SaveEvent(MantUsuariosForm source, Usuario usuario) {
			super(source, usuario);
		}
	}

	/**
	 * Clase estática para definir el evento "Eliminar" del formulario de
	 * Mantenimiento de Usuarios.
	 * 
	 * @author Lisa
	 *
	 */
	public static class DeleteEvent extends MantUsuarioFormEvent {
		private static final long serialVersionUID = 1L;

		DeleteEvent(MantUsuariosForm source, Usuario usuario) {
			super(source, usuario);
		}

	}

	/**
	 * Clase estática para definir el evento "Cerrar" del formulario de
	 * Mantenimiento de Usuarios.
	 * 
	 * @author Lisa
	 *
	 */
	public static class CloseEvent extends MantUsuarioFormEvent {
		private static final long serialVersionUID = 1L;

		CloseEvent(MantUsuariosForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
