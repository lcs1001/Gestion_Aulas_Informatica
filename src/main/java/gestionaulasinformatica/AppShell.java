package gestionaulasinformatica;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Gestión de Aulas de Informática", shortName = "Gestión Aulas Informática")
public class AppShell implements AppShellConfigurator {
	private static final long serialVersionUID = 1L;
}