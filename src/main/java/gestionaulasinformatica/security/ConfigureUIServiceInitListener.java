package gestionaulasinformatica.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import gestionaulasinformatica.ui.views.login.LoginView;

import org.springframework.stereotype.Component;

/**
 * Clase que conecta Spring Security al sistema de navegación de Vaadin para
 * asegurar la aplicación.
 * 
 * La anotación "@Component" registra al oyente (listener), y Vaadin lo recogerá
 * en el inicio.
 * 
 * @author Lisa
 *
 */
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Función que escucha la inicialización de la UI (componente raíz interno en
	 * Vaadin) y agrega un oyente (listener) antes de cada transición de vista (view
	 * - ventana).
	 */
	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiEvent -> {
			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::authenticateNavigation);
		});
	}

	/**
	 * Función que redirige todas las solicitudes al inicio de sesión, si el usuario
	 * no se ha logeado.
	 * 
	 * @param event
	 */
	private void authenticateNavigation(BeforeEnterEvent event) {
		if (!LoginView.class.equals(event.getNavigationTarget()) && !SecurityUtils.isUserLoggedIn()) {
			event.rerouteTo(LoginView.class);
		}
	}
}