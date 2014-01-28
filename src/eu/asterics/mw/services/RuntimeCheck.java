package eu.asterics.mw.services;

public class RuntimeCheck implements IRuntimeCheck {

	@Override
	public boolean isAndroid() {
		return System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik");
	}

}
