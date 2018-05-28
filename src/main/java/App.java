import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    //before filters
    before("/sightings/*", (request, response) -> {
      String rangerName= request.session().attribute("rangerName");
      if(rangerName==null){
        response.redirect("/");
        halt();
      }
    });

    before("/sightings", (request, response) -> {
      String rangerName= request.session().attribute("rangerName");
      if(rangerName==null){
        response.redirect("/");
        halt();
      }
    });

    before("/animals/*", (request, response) -> {
      String rangerName= request.session().attribute("rangerName");
      if(rangerName==null){
        response.redirect("/");
        halt();
      }
    });

    before("/animals", (request, response) -> {
      String rangerName= request.session().attribute("rangerName");
      if(rangerName==null){
        response.redirect("/");
        halt();
      }
    });

    //Index page
    get("/", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("recentSightings", Sighting.mostRecent());
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Log in
    post("/", (request, response) -> {
      String rangerName = request.queryParams("rangerName");
      request.session().attribute("rangerName", rangerName);
      response.redirect("/");
      return null;
    });

    //Log out
    get("/logout", (request, response) -> {
      request.session().removeAttribute("rangerName");
      response.redirect("/");
      return null;
    });

    //Animal list
    get("/animals", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("regular_animals", RegularAnimal.all());
      model.put("endangered_animals", EndangeredAnimal.all());
      model.put("template", "templates/animals.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //New animal form
    get("/animals/new", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("HEALTH_HEALTHY", EndangeredAnimal.HEALTH_HEALTHY);
      model.put("HEALTH_OK", EndangeredAnimal.HEALTH_OK);
      model.put("HEALTH_ILL", EndangeredAnimal.HEALTH_ILL);
      model.put("AGE_BABBY", EndangeredAnimal.AGE_BABBY);
      model.put("AGE_YOUNG", EndangeredAnimal.AGE_YOUNG);
      model.put("AGE_ADULT", EndangeredAnimal.AGE_ADULT);
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("template", "templates/animal-form.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Add new animal - redirects to animal list
    post("/animals/new", (request, response) -> {
      String name = request.queryParams("name");
      String species = request.queryParams("species");
      boolean endangered = request.queryParamsValues("endangered")!=null;
      if(endangered){
        String health = request.queryParams("health");
        String age = request.queryParams("age");
        EndangeredAnimal endangeredAnimal = new EndangeredAnimal(name, species, health, age);
        endangeredAnimal.save();
      } else{
        RegularAnimal regularAnimal = new RegularAnimal(name, species);
        regularAnimal.save();
      }
      response.redirect("/animals");
      return null;
    });

    //individual common(regular) animal pages
    get("/animals/endangered/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      EndangeredAnimal animal = EndangeredAnimal.find(Integer.parseInt(request.params("id")));
      model.put("animal", animal);
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("template", "templates/animal.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //delete endangered animal
    post("/animals/endangered/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      EndangeredAnimal animal = EndangeredAnimal.find(Integer.parseInt(request.params("id")));
      animal.delete();
      response.redirect("/animals");
      return null;
    });

    //individual common(regular) animal pages
    get("/animals/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      RegularAnimal animal = RegularAnimal.find(Integer.parseInt(request.params("id")));
      model.put("animal", animal);
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("template", "templates/animal.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //delete common animals
    post("/animals/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      RegularAnimal animal = RegularAnimal.find(Integer.parseInt(request.params("id")));
      animal.delete();
      response.redirect("/animals");
      return null;
    });

    //all sightings
    get("/sightings", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("sightings", Sighting.allByDate());
      model.put("template", "templates/sightings.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //new sighting form
    get("/sightings/new", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("template", "templates/sighting-form.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //Add new sighting - redirects to sightings list
    post("/sightings/new", (request, response) -> {
      String rangerName = request.session().attribute("rangerName");
      String location = request.queryParams("location");
      Sighting sighting = new Sighting(location, rangerName);
      sighting.save();
      String[] animalArray = request.queryParams("animal-names").split(",");
      Animal animal;
      for(String animalName : animalArray){
        animal = RegularAnimal.findByName(animalName);
        if(animal == null){
          animal = EndangeredAnimal.findByName(animalName);
        }
        if(animal != null){
          sighting.addAnimal(animal);
        } else {
          throw new NullPointerException("I'm sorry, We couldn't find one of your animals. Please add it to the database and update your sighting");
        }
      }
      response.redirect("/sightings");
      return null;
    });

    //individual common(regular) animal pages
    get("/sightings/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Sighting sighting = Sighting.find(Integer.parseInt(request.params("id")));
      model.put("sighting", sighting);
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("template", "templates/sighting.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    //delete sighting
    post("/sightings/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Sighting sighting = Sighting.find(Integer.parseInt(request.params("id")));
      sighting.delete();
      response.redirect("/sightings");
      return null;
    });

    //exception handling
    exception(NullPointerException.class, (exc, request, response) -> {
      response.status(500);
      VelocityTemplateEngine engine = new VelocityTemplateEngine();
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("message", exc.getMessage());
      model.put("template", "templates/notfound.vtl");
      String html = engine.render(new ModelAndView(model, layout));
      response.body(html);
    });

    exception(IndexOutOfBoundsException.class, (exc, request, response) -> {
      response.status(404);
      VelocityTemplateEngine engine = new VelocityTemplateEngine();
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("rangerName", request.session().attribute("rangerName"));
      model.put("message", exc.getMessage());
      model.put("template", "templates/notfound.vtl");
      String html = engine.render(new ModelAndView(model, layout));
      response.body(html);
    });

    exception(NumberFormatException.class, (exc, req, res) -> {
      res.status(404);
      VelocityTemplateEngine engine = new VelocityTemplateEngine();
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("message", "looks like that page doesn't exist!");
      model.put("template", "templates/notfound.vtl");
      String html = engine.render(new ModelAndView(model, layout));
      res.body(html);
    });
  }
}
