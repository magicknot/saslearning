package pt.ulisboa.tecnico.saslearning.domain;

public class QualityAttribute extends QualityAttribute_Base {
    
    public QualityAttribute() {
        super();
    }
    
    @Override
    public void removeScenario() {
    	setScenario(null);
    }
}
