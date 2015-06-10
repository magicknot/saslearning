package pt.ulisboa.tecnico.saslearning.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.saslearning.domain.Annotation;
import pt.ulisboa.tecnico.saslearning.domain.Document;
import pt.ulisboa.tecnico.saslearning.domain.Scenario;
import pt.ulisboa.tecnico.saslearning.domain.SrcOfStimulus;
import pt.ulisboa.tecnico.saslearning.jsonsupport.AnnotationJ;

@Controller
public class DomainEntitiesController {
	
	@RequestMapping(value="/addSyntax/{docId}")
	public String syntaxManager(Model m, @PathVariable String docId){
		Set<Scenario> scenarios = getDocumentScenarios(docId);
		m.addAttribute("scenarios", scenarios);
		m.addAttribute("docId", docId);
		return "syntaxManager";
	}
	
	@RequestMapping(value="/createScenario/{docId}")
	public RedirectView addScenarioToDocument(@PathVariable String docId){
		addScenario(docId);
		RedirectView rv = new RedirectView("/addSyntax/" + docId);
		return rv;
		
	}
	
	//GO TO SCENARIO MANAGER TO CHOOSE ANNOTATIONS TO LINK
	@RequestMapping(value="/linkAnnotation/{docId}/Scenario/{scenId}")
	public String scenarioManager(Model m, @PathVariable String docId, @PathVariable String scenId){
		Scenario scen = getScenarioById(scenId);
		m.addAttribute("scen", scen);
		m.addAttribute("annotations", getAnnotationsByTag("Scenario", docId));
		m.addAttribute("docId", docId);
		return "scenarioManager";
	}
	
	@RequestMapping(value="/linkAnnotation/{docId}/{scenId}/{annId}")
	public RedirectView linkAnnotation(@PathVariable String docId, @PathVariable String scenId, @PathVariable String annId){
		linkAnnotationToScenario(scenId, annId);
		RedirectView rv = new RedirectView("/linkAnnotation/" + docId + "/Scenario/"+ scenId);
		return rv;
	}
	
	@RequestMapping(value="/addSrcOfStimulus/{docId}/{scenarioId}")
	public RedirectView addSourceOfStimulus(@PathVariable String scenarioId, @PathVariable String docId){
		addSrcOfStimulus(scenarioId);
		RedirectView rv = new RedirectView("/addSyntax/" + docId);
		return rv;
	}
	
	@RequestMapping(value="/unlinkFromScenario/{docId}/{scenarioId}/{annotationId}")
	public RedirectView unlinkAnnotationFromScenario(@PathVariable String docId, @PathVariable String scenarioId, @PathVariable String annotationId) {
		removeAnnotationFromScenario(scenarioId, annotationId);
		RedirectView rv = new RedirectView("/linkAnnotation/"+docId+"/Scenario/" + scenarioId);
		return rv;
	}
	
	@RequestMapping(value="/removeScenario/{docId}/{scenarioId}")
	public RedirectView removeScenario(@PathVariable String docId, @PathVariable String scenarioId) {
		removeScenario(scenarioId);
		RedirectView rv = new RedirectView("/addSyntax/" + docId);
		return rv;
	}
	
	@RequestMapping(value="/removeSrcOfStimulus/{docId}/{srcId}")
	public RedirectView removeSrcOfStimulus(@PathVariable String docId, @PathVariable String srcId) {
		removeSrcOfStimulus(srcId);
		RedirectView rv = new RedirectView("/addSyntax/" + docId);
		return rv;
	}
	
	@RequestMapping(value="/linkAnnotation/{docId}/SrcOfStimulus/{srcId}")
	public String srcStimulusManager(@PathVariable String docId, @PathVariable String srcId, Model m) {
		m.addAttribute("docId", docId);
		m.addAttribute("elem", getSrcOfStimulusById(srcId));
		m.addAttribute("annotations", getAnnotationsByTag("Source Of Stimulus", docId));
		return "scenarioElementsManager";
	}
	
	@RequestMapping(value="/linkAnnotation/{docId}/Source of Stimulus/{srcId}/{annId}")
	public RedirectView linkToSrcStimulus(@PathVariable String docId, @PathVariable String srcId, @PathVariable String annId) {
		linkAnnotationToSrcStimulus(srcId, annId);
		RedirectView rv = new RedirectView("/linkAnnotation/" + docId + "/SrcOfStimulus/" + srcId);
		return rv;
	}
	
	@RequestMapping(value="/unlinkAnnotation/Source of Stimulus/{docId}/{srcId}/{annId}")
	public RedirectView unlinkAnnotationFromSrcStimulux(@PathVariable String docId, @PathVariable String srcId, @PathVariable String annId) {
		removeAnnotationFromSrcStimulus(srcId, annId);
		RedirectView rv = new RedirectView("/linkAnnotation/"+docId+"/SrcOfStimulus/" + srcId);
		return rv;
	}

	//-------------------------------------------------------------------------------------------------------------------
	@Atomic(mode=TxMode.WRITE)
	private void linkAnnotationToSrcStimulus(String srcId, String annId){
		SrcOfStimulus s = FenixFramework.getDomainObject(srcId);
		Annotation a = FenixFramework.getDomainObject(annId);
		s.addAnnotation(a);
	}
	
	@Atomic(mode=TxMode.READ)
	private SrcOfStimulus getSrcOfStimulusById(String srcId) {
		SrcOfStimulus src = FenixFramework.getDomainObject(srcId);
		List<AnnotationJ> srcAnnotations = getAnnotationsFromSet(src.getAnnotationSet());
		src.setAnnotations(srcAnnotations);
		return src;
	}
	
	@Atomic(mode = TxMode.WRITE)
	private void removeSrcOfStimulus(String srcId) {
		SrcOfStimulus src = FenixFramework.getDomainObject(srcId);
		src.delete();
	}
	
	@Atomic(mode = TxMode.WRITE)
	private void removeScenario(String scenarioId) {
		Scenario s = FenixFramework.getDomainObject(scenarioId);
		s.delete();
	}
	
	@Atomic(mode=TxMode.WRITE)
	private void removeAnnotationFromScenario(String scenarioId, String annotationId) {
		Scenario s = FenixFramework.getDomainObject(scenarioId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		s.removeAnnotation(a);
		a.setScenario(null);
	}
	
	@Atomic(mode=TxMode.WRITE)
	private void removeAnnotationFromSrcStimulus(String srcId, String annotationId) {
		SrcOfStimulus s = FenixFramework.getDomainObject(srcId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		s.removeAnnotation(a);
		a.setSrcOfStimulus(null);
	}
	
	@Atomic(mode=TxMode.READ)
	private Set<Scenario> getDocumentScenarios(String docId){
		Document d = FenixFramework.getDomainObject(docId);
		Set<Scenario> scenarios = d.getScenarioSet();
		for(Scenario s : scenarios){
			s.setAnnotations(getAnnotationsFromSet(s.getAnnotationSet()));
		}
		return scenarios;
	}
	

	@Atomic(mode=TxMode.WRITE)
	private void linkAnnotationToScenario(String scenId, String annId){
		Scenario s = FenixFramework.getDomainObject(scenId);
		Annotation a = FenixFramework.getDomainObject(annId);
		s.addAnnotation(a);
	}
	
	@Atomic(mode=TxMode.READ)
	private List<AnnotationJ> getAnnotationsByTag(String tag, String docId){
		List<AnnotationJ> annotations = new ArrayList<AnnotationJ>();
		Document d = FenixFramework.getDomainObject(docId);
		Gson gson = new Gson();
		for(Annotation a : d.getAnnotationSet()){
			if(a.getTag().equals(tag)){
				AnnotationJ ann = gson.fromJson(a.getAnnotation(), AnnotationJ.class);
				annotations.add(ann);
			}
		}
		return annotations;
	}
	
	@Atomic(mode=TxMode.READ)
	private Scenario getScenarioById(String scenId) {
		Scenario s = FenixFramework.getDomainObject(scenId);
		List<AnnotationJ> scenAnnotations = getAnnotationsFromSet(s.getAnnotationSet());
		s.setAnnotations(scenAnnotations);
		return s;
	}

	@Atomic(mode=TxMode.WRITE)
	private void addSrcOfStimulus(String scenarioId){
		Scenario s = FenixFramework.getDomainObject(scenarioId);
		if(s.getSrcOfStimulus() == null) {
			SrcOfStimulus src = new SrcOfStimulus();
			src.setName("Source of Stimulus");
			s.setSrcOfStimulus(src);
		}
	}
	
	@Atomic(mode=TxMode.WRITE)
	private void addScenario(String docId){
		Document d = FenixFramework.getDomainObject(docId);
		Scenario s = new Scenario();
		s.setName("Scenario");
		d.addScenario(s);
	}
	
	@Atomic(mode=TxMode.READ)
	private List<AnnotationJ> getAnnotationsFromSet(Set<Annotation> anns){
		Gson gson = new Gson();
		List<AnnotationJ> annotations = new ArrayList<AnnotationJ>(); 
		for(Annotation a : anns){
			AnnotationJ ann = gson.fromJson(a.getAnnotation(), AnnotationJ.class);
			annotations.add(ann);
		}
		return annotations;
	}
}
