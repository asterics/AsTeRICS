package eu.asterics.mw.webservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import eu.asterics.mw.webservice.serverUtils.ServerRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(ServerRepository.PATH_REST)
public class ApplicationConfig extends Application {
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(RestServer.class));
	}
}
