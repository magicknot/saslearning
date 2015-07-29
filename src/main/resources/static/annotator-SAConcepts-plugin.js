Annotator.Plugin.SAConcepts = function (element, options) {
  var plugin = {};
  var concepts;
  plugin.pluginInit = function () {
    console.log("SAConcepts plugin init");
    var ann = this.annotator;
    console.log(ann);
    var opts = {
      type: "GET",
      dataType: "json"
    };
    $.ajax(options.tagsLocation, opts).done(function(data){
      concepts = data;
    });
    this.annotator.editor.addField({
      load: function(field, annotation){
        loadTagSelector(field, annotation, concepts);
      }
    });

    this.annotator.viewer.addField({
      load: function(field, annotation){ 
        loadTagViewer(field, annotation);
      }
    }).addField({
      load: function(field, annotation){
        loadViewerLink(field, annotation, ann, options);
      }
    });

    this.annotator.subscribe("annotationEditorShown", function(editor, annotation){
      removeExtras();
    }).subscribe("annotationEditorSubmit", function(editor, annotation){
      var tag = document.getElementById("tagSelector").value;
      annotation.tag = tag;
    });

  }
  return plugin;
}	

var removeExtras = function(){
  var extra1 = $(".chosen-container > .annotator-controls");
  while(extra1.length > 0){
    extra1[0].remove();
    extra1 = $(".chosen-container > .annotator-controls");
  }

  var extra2 = $(".chosen-drop > .annotator-controls");
  while(extra2.length > 0){
    extra2[0].remove();
    extra2 = $(".chosen-container > .annotator-controls");
  }
}

var loadTagSelector = function (field, annotation, concepts) {
  field.innerHTML="";
  var jqfield = $(field);
  var container = $("<div>");
  container.attr("id", "tagContainer");
  container.attr("style", "width:350px");
  jqfield.append(container);
  var select = $("<select>");
  select.attr("id", "tagSelector");
  select.attr("class", "form-control");
  for(var i in concepts){
    var group = $("<optgroup>");
    group.attr("label", concepts[i].name);
    group.attr("style", "font-weight: 600; color: rgb(115, 115, 115);")
    for (var j = 0; j < concepts[i].tags.length; j++) {
      var opt  = $("<option>");
      opt.append(concepts[i].tags[j]);
      if(annotation.tag != undefined){
        var tag = annotation.tag;
        if(tag == concepts[i].tags[j]){
          opt.attr("selected", "");
        }
      }
      group.append(opt);
    }
    select.append(group);
  }
  container.append(select);
}
var loadTagViewer = function(field, annotation){           
  field.innerHTML = "";
  var x = $("<div>");
  x.attr("class", "annotator-tags");
  var t = $("<span>");
  t.attr("class", "annotator-tag");
  t.append(annotation.tag);
  x.append(t);
  $(field).append(x);
}

var loadViewerLink = function(field, annotation, ann, options){
  if(annotation.tag != undefined){
    var a = $("<a>");
    a.attr("target", "_parent");
    a.append("Add/Remove from Structured Representation");
    a.attr("data-toggle","modal");
    a.attr("data-target","#syntax");
    a.click(function(){
      ann.viewer.hide();
      $(parent.document.getElementById("elements")).load("/templateEditor/"+options.docId+"/"+annotation.id);
    });
    $(field).append(a);
  }
}