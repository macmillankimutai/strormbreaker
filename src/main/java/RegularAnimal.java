import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class RegularAnimal extends Animal {

  public RegularAnimal(String name, String species){
    this.name = name;
    this.species = species;
    endangered = false;
  }

  public static List<RegularAnimal> all() {
    String sql = "SELECT * FROM animals WHERE endangered = 'false'";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).throwOnMappingFailure(false).executeAndFetch(RegularAnimal.class);
    }
  }

  public static RegularAnimal find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM animals where id=:id";
      RegularAnimal animal = con.createQuery(sql)
        .addParameter("id", id)
        .throwOnMappingFailure(false)
        .executeAndFetchFirst(RegularAnimal.class);
      if(animal == null){
        throw new IndexOutOfBoundsException("I'm sorry, I think this animal does not exist");
      }
      return animal;
    }
  }

  public static RegularAnimal findByName(String name) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM animals where name=:name";
      RegularAnimal animal = con.createQuery(sql)
        .addParameter("name", name)
        .throwOnMappingFailure(false)
        .executeAndFetchFirst(RegularAnimal.class);
      return animal;
    }
  }
}
