package pt.ulisboa.tecnico.saslearning.domain;

public class Tactic extends Tactic_Base {
    
    public Tactic() {
        super();
    }
    
    @Override
    public void removeScenario() {
    	setScenario(null);
    }
    
    @Override
    public Scenario getEnclosingScenario() {
    	return getScenario();
    }

    
}
