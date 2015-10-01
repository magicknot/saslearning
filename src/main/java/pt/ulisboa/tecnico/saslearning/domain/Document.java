package pt.ulisboa.tecnico.saslearning.domain;

import java.util.Iterator;

public class Document extends Document_Base {
    
    public Document() {
        super();
        
    }
    
    @Override
    public String toString() {
    	return getUrl();
    }
    
    public String getContentsAsString() {
    	String s = new String(getContent());
    	return s;
    }
    
    public void delete(){
    	Iterator<Annotation> itA = getAnnotationSet().iterator();
    	while(itA.hasNext()){
    		Annotation a = itA.next();
    		removeAnnotation(a);
    		a.delete();
    	}
    	
    	Iterator<Scenario> itS = getScenarioSet().iterator();
    	while (itS.hasNext()) {
    		Scenario s = itS.next();
    		removeScenario(s);
    		s.delete();
    		
		}
    	
    	Iterator<Module> itM = getModuleSet().iterator();
    	while (itM.hasNext()) {
    		Module m = itM.next();
    		removeModule(m);
    		m.delete();
    		
		}
    	
    	Iterator<View> itV = getViewSet().iterator();
    	while (itV.hasNext()) {
    		View v = itV.next();
    		removeView(v);
    		v.delete();
    		
		}
    	
    	Iterator<Component> itcomp = getComponentSet().iterator();
    	while (itcomp.hasNext()) {
    		Component c = itcomp.next();
    		removeComponent(c);
    		c.delete();
    		
		}
    	
    	Iterator<Connector> itconn = getConnectorSet().iterator();
    	while (itconn.hasNext()) {
    		Connector c = itconn.next();
    		removeConnector(c);
    		c.delete();
    		
		}
    	setRoot(null);
    	deleteDomainObject();
    }
    
}
