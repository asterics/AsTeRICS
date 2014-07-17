package eu.asterics.mw.webservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(WebServiceEngine.REST_PATH)
public class ApplicationConfig extends Application {
	public Set<Class<?>> getClasses() {
		return new HashSet<Class<?>>(Arrays.asList(SimpleRESTPojo.class,StartModel.class,StopModel.class));
	}
}
