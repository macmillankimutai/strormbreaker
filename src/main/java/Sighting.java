import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;
import java.text.DateFormat;

public class Sighting {
  private int id;
  private String location;
  private String rangerName;
  private Timestamp date_sighted;

  public Sighting(String location, String rangerName){
    this.location = location;
    this.rangerName = rangerName;
  }

  public int getId(){
    return id;
  }

  public String getLocation(){
    return location;
  }

  public String getRangerName() {
    return rangerName;
  }

  public Timestamp getDateSighted() {
    return date_sighted;
  }

  public String getFormattedDate() {
    return DateFormat.getDateTimeInstance().format(date_sighted);
  }

  @Override
  public boolean equals(Object otherSighting){
    if (!(otherSighting instanceof Sighting)) {
      return false;
    } else {
      Sighting newSighting = (Sighting) otherSighting;
      return this.id == newSighting.id && this.location.equals(newSighting.location) && this.rangerName.equals(newSighting.rangerName);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO sightings (location, rangerName, date_sighted) VALUES (:location, :rangerName, now())";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("location", this.location)
        .addParameter("rangerName", this.rangerName)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Sighting> all() {
    String sql = "SELECT * FROM sightings";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Sighting.class);
    }
  }

  public static List<Sighting> allByDate() {
    String sql = "SELECT * FROM sightings ORDER BY date_sighted DESC";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Sighting.class);
    }
  }

  public static List<Sighting> mostRecent() {
    String sql = "SELECT * FROM sightings WHERE date_sighted BETWEEN now() - interval '24 hours' AND now() ORDER BY date_sighted DESC LIMIT 5";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Sighting.class);
    }
  }

  public static Sighting find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM sightings where id=:id";
      Sighting sighting = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Sighting.class);
      if(sighting == null){
        throw new IndexOutOfBoundsException("I'm sorry, I think this sighting does not exist");
      }
      return sighting;
    }
  }

  public void delete(){
    try(Connection con = DB.sql2o.open()){
      String sql = "DELETE FROM sightings WHERE id=:id";
      con.createQuery(sql).addParameter("id", this.id).executeUpdate();
      String joinSql = "DELETE FROM animals_sightings WHERE sighting_id=:id";
      con.createQuery(joinSql).addParameter("id", this.id).executeUpdate();
    }
  }

  public void addAnimal(Animal animal){
    try(Connection con = DB.sql2o.open()){
      String sql = "INSERT INTO animals_sightings (sighting_id, animal_id) VALUES(:sighting_id, :animal_id)";
      con.createQuery(sql)
        .addParameter("sighting_id", this.id)
        .addParameter("animal_id", animal.getId())
        .executeUpdate();
    }
  }

  public List<RegularAnimal> getRegularAnimals(){
    try(Connection con = DB.sql2o.open()){
      String sql = "SELECT animals.* FROM animals JOIN animals_sightings ON (animals.id = animals_sightings.animal_id) WHERE animals_sightings.sighting_id = :id AND animals.endangered=false";
      return con.createQuery(sql).addParameter("id", this.id).throwOnMappingFailure(false).executeAndFetch(RegularAnimal.class);
    }
  }

  public List<EndangeredAnimal> getEndangeredAnimals(){
    try(Connection con = DB.sql2o.open()){
      String sql = "SELECT animals.* FROM animals JOIN animals_sightings ON (animals.id = animals_sightings.animal_id) WHERE animals_sightings.sighting_id = :id AND animals.endangered=true";
      return con.createQuery(sql).addParameter("id", this.id).throwOnMappingFailure(false).executeAndFetch(EndangeredAnimal.class);
    }
  }
}
