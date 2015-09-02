package pt.ulisboa.tecnico.saslearning.viewtypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.saslearning.domain.Annotation;
import pt.ulisboa.tecnico.saslearning.domain.Document;
import pt.ulisboa.tecnico.saslearning.domain.Module;
import pt.ulisboa.tecnico.saslearning.jsonsupport.AnnotationJ;

import com.google.gson.Gson;

@Controller
public class ModuleController {

	@RequestMapping(value = "setParentModal")
	public String getModal() {
		return "setParentModal";
	}

	@RequestMapping(value = "/addAnnotationToModuleTemplate/{docId}/{annotationId}")
	public String addAnnotationModal(@PathVariable String docId,
			@PathVariable String annotationId, Model m) {
		Document d = FenixFramework.getDomainObject(docId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		Gson g = new Gson();
		AnnotationJ ann = g.fromJson(a.getAnnotation(), AnnotationJ.class);
		Set<Module> modules = d.getModuleSet();
		m.addAttribute("modules", new HashSet<Module>(modules));
		m.addAttribute("annotation", ann);
		m.addAttribute("annotData", a);
		m.addAttribute("docId", docId);
		return "addAnnotationModuleModal";
	}

	@RequestMapping(value = "/linkToModule/{docId}/{annotationId}/{moduleId}")
	public RedirectView addAnnotationToModule(@PathVariable String docId,
			@PathVariable String annotationId, @PathVariable String moduleId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		addAnnotationToModule(mod, a);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId + "#" + annotationId);
		return rv;
	}

	@Atomic(mode = TxMode.WRITE)
	private void addAnnotationToModule(Module mod, Annotation a) {
		mod.addAnnotation(a);
		a.updateConnection(mod.getExternalId());
	}

	@RequestMapping(value = "/addNewModule/{docId}/{annotationId}/{moduleName}")
	public RedirectView addNewModuleViewType(@PathVariable String docId,
			@PathVariable String annotationId, @PathVariable String moduleName,
			@RequestParam String move) {
		Document d = FenixFramework.getDomainObject(docId);
		addModuleToDocument(d, moduleName);
		RedirectView rv = new RedirectView();
		if (move.equals("yes")) {
			rv.setUrl("/moveAnnotationModule/" + docId + "/" + annotationId);
		} else {
			rv.setUrl("/addAnnotationToModuleTemplate/" + docId + "/"
					+ annotationId);
		}
		return rv;
	}

	@Atomic(mode = TxMode.WRITE)
	private void addModuleToDocument(Document d, String moduleName) {
		Module m = new Module();
		m.setIdentifier("Module");
		m.setName(moduleName);
		d.addModule(m);
	}

	@RequestMapping(value = "/viewModule/{docId}/{moduleId}")
	public String viewModuleTemplate(Model m, @PathVariable String docId,
			@PathVariable String moduleId) {
		m.addAttribute("docId", docId);
		Module mod = FenixFramework.getDomainObject(moduleId);
		Document d = FenixFramework.getDomainObject(docId);
		m.addAttribute("module", mod);
		m.addAttribute("modules", d.getModuleSet());
		m.addAttribute("uses", new UsedModules());
		return "moduleTemplate";
	}

	@RequestMapping(value = "/removeModule/{docId}/{moduleId}")
	public RedirectView removeModule(@PathVariable String docId,
			@PathVariable String moduleId) {
		Document d = FenixFramework.getDomainObject(docId);
		Module mod = FenixFramework.getDomainObject(moduleId);
		removeModuleFromDocument(d, mod);
		RedirectView rv = new RedirectView("/selectDoc/" + docId);
		return rv;
	}

	// ::
	@RequestMapping(value = "/unlinkFromModule/{docId}/{annotationId}/{moduleId}")
	public RedirectView unlinkAnnotationFromModule(@PathVariable String docId,
			@PathVariable String annotationId, @PathVariable String moduleId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		removeAnnotationFromModule(mod, a);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId);
		return rv;
	}

	@RequestMapping(value = "/moveAnnotationModule/{docId}/{annotationId}")
	public String moveAnnotationModal(Model m, @PathVariable String docId,
			@PathVariable String annotationId) {
		Document d = FenixFramework.getDomainObject(docId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		Gson g = new Gson();
		AnnotationJ ann = g.fromJson(a.getAnnotation(), AnnotationJ.class);
		m.addAttribute("modules", d.getModuleSet());
		m.addAttribute("annotation", ann);
		m.addAttribute("annotData", a);
		m.addAttribute("docId", docId);
		return "moveAnnotationModuleModal";
	}

	@RequestMapping(value = "/moveToModule/{docId}/{annotationId}/{moduleId}")
	public RedirectView moveAnnotation(@PathVariable String docId,
			@PathVariable String annotationId, @PathVariable String moduleId) {
		Module nmod = FenixFramework.getDomainObject(moduleId);
		Annotation a = FenixFramework.getDomainObject(annotationId);
		moveAnnotationToModule(a, nmod);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId + "#" + annotationId);
		return rv;
	}

	@RequestMapping(value = "/setModuleText/{docId}/{moduleId}", method = RequestMethod.POST)
	public RedirectView setModuleText(@RequestParam String text,
			@PathVariable String docId, @PathVariable String moduleId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		updateText(mod, text);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId + "#" + mod.getName());
		return rv;
	}

	@RequestMapping(value = "/setModuleParent/{docId}/{moduleId}/{parentId}")
	public RedirectView addModuleParent(@PathVariable String moduleId,
			@PathVariable String parentId, @PathVariable String docId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		Module parent = FenixFramework.getDomainObject(parentId);
		addParent(mod, parent);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId);
		return rv;
	}

	@RequestMapping(value = "/setModuleUses/{docId}/{moduleId}", method = RequestMethod.POST)
	public RedirectView addModuleUses(@PathVariable String moduleId,
			@PathVariable String docId, @ModelAttribute UsedModules modules) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		addUse(mod, modules.getUsed());
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId);
		return rv;
	}

	@RequestMapping(value = "/removeModuleParent/{docId}/{moduleId}/{parentId}")
	public RedirectView removeModuleParent(@PathVariable String docId,
			@PathVariable String moduleId, @PathVariable String parentId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		Module parent = FenixFramework.getDomainObject(parentId);
		removeModuleParent(mod, parent);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId);
		return rv;
	}

	@RequestMapping(value = "/removeModuleUse/{docId}/{moduleId}/{usedId}")
	public RedirectView removeModuleUses(@PathVariable String docId,
			@PathVariable String moduleId, @PathVariable String usedId) {
		Module mod = FenixFramework.getDomainObject(moduleId);
		Module used = FenixFramework.getDomainObject(usedId);
		removeModuleUse(mod, used);
		RedirectView rv = new RedirectView("/viewModule/" + docId + "/"
				+ moduleId);
		return rv;
	}

	@Atomic(mode=TxMode.WRITE)
	private void removeModuleUse(Module mod, Module used) {
		mod.removeUses(used);
	}
	
	@Atomic(mode=TxMode.WRITE)
	private void removeModuleParent(Module mod, Module parent) {
		parent.removeChild(mod);
		mod.setParent(null);
	}

	@Atomic(mode = TxMode.WRITE)
	private void addUse(Module mod, List<String> list) {
		for (String id : list) {
			Module m = FenixFramework.getDomainObject(id);
			mod.addUses(m);
		}
	}

	@Atomic(mode = TxMode.WRITE)
	private void addParent(Module mod, Module parent) {
		mod.setParent(parent);
	}

	@Atomic(mode = TxMode.WRITE)
	private void setModuleText(Module m, String text) {
		m.setText(text);
	}

	@Atomic(mode = TxMode.WRITE)
	private void updateText(Module mod, String text) {
		mod.setText(text);
	}

	@Atomic(mode = TxMode.WRITE)
	private void moveAnnotationToModule(Annotation a, Module mod) {
		Module old = a.getModule();
		removeAnnotationFromModule(old, a);
		addAnnotationToModule(mod, a);
	}

	@Atomic(mode = TxMode.WRITE)
	private void removeAnnotationFromModule(Module mod, Annotation a) {
		a.updateConnection(null);
		mod.removeAnnotation(a);
		a.setModule(null);
	}

	@Atomic(mode = TxMode.WRITE)
	private void removeModuleFromDocument(Document d, Module mod) {
		d.removeModule(mod);
		mod.delete();
	}

	@Atomic(mode = TxMode.WRITE)
	private void updateAnnotation(String connectedId, Annotation a) {
		Gson g = new Gson();
		AnnotationJ ann = g.fromJson(a.getAnnotation(), AnnotationJ.class);
		ann.setConnectedId(connectedId);
		String json = g.toJson(ann);
		a.setAnnotation(json);
	}
}
