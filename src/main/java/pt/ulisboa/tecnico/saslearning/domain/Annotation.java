package pt.ulisboa.tecnico.saslearning.domain;

public class Annotation extends Annotation_Base {

	public Annotation() {
		super();
	}

	@Override
	public String toString() {
		return getAnnotation();
	}

	public void delete() {
		setDocument(null);
		setOwner(null);
		deleteDomainObject();
		
	}
	
	public boolean isConnected() {
		return getScenario() != null || getScenarioElement() != null;
	}
}
