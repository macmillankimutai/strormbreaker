import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class EndangeredAnimalTest{
  EndangeredAnimal testEndangeredAnimal;
  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testEndangeredAnimal = new EndangeredAnimal("fluffy", "panda", "healthy", "young");
  }

  @Test
  public void animals_instantiatesCorrectly_true() {
    assertTrue(testEndangeredAnimal instanceof EndangeredAnimal);
  }

  @Test
  public void name_instantiatesCorrectly_true() {
    assertEquals("fluffy", testEndangeredAnimal.getName());
  }

  @Test
  public void species_instantiatesCorrectly_true() {
    assertEquals("panda", testEndangeredAnimal.getSpecies());
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    EndangeredAnimal testEndangeredAnimal2 = new EndangeredAnimal("fluffy", "panda", "healthy", "young");
    assertTrue(testEndangeredAnimal.equals(testEndangeredAnimal2));
  }

  @Test
  public void save_insertsEndangeredAnimalIntoDatabase_EndangeredAnimal() {
    testEndangeredAnimal.save();
    EndangeredAnimal testEndangeredAnimal2 = null;
    try(Connection con = DB.sql2o.open()){
      testEndangeredAnimal2 = con.createQuery("SELECT * FROM animals WHERE name='fluffy'")
      .throwOnMappingFailure(false)
      .executeAndFetchFirst(EndangeredAnimal.class);
    }
    assertTrue(testEndangeredAnimal2.equals(testEndangeredAnimal));
  }

  @Test
  public void all_returnsAllInstancesOfPerson_true() {
    testEndangeredAnimal.save();
    EndangeredAnimal testEndangeredAnimal2 = new EndangeredAnimal("scratchy", "red wolf", "sick", "old");
    testEndangeredAnimal2.save();
    assertEquals(true, EndangeredAnimal.all().get(0).equals(testEndangeredAnimal));
    assertEquals(true, EndangeredAnimal.all().get(1).equals(testEndangeredAnimal2));
  }

  @Test
  public void save_assignsIdToEndangeredAnimal() {
    testEndangeredAnimal.save();
    EndangeredAnimal testEndangeredAnimal2 = EndangeredAnimal.all().get(0);
    assertEquals(testEndangeredAnimal.getId(), testEndangeredAnimal2.getId());
  }

  @Test
  public void find_returnsEndangeredAnimalWithSameId_secondEndangeredAnimal() {
    testEndangeredAnimal.save();
    EndangeredAnimal testEndangeredAnimal2 = new EndangeredAnimal("scratchy","red wolf", "sick", "old");
    testEndangeredAnimal2.save();
    assertEquals(EndangeredAnimal.find(testEndangeredAnimal2.getId()), testEndangeredAnimal2);
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void find_throwsExceptionIfAnimalNotFound() {
    EndangeredAnimal.find(1);
  }

  @Test
  public void findbyName_returnsEndangeredAnimalWithSameName_secondEndangeredAnimal() {
    testEndangeredAnimal.save();
    EndangeredAnimal testEndangeredAnimal2 = new EndangeredAnimal("scratchy","red wolf", "sick", "adult");
    testEndangeredAnimal2.save();
    assertEquals(EndangeredAnimal.findByName(testEndangeredAnimal2.getName()), testEndangeredAnimal2);
  }

  @Test
  public void delete_deletesEntryInDatabase_0(){
    testEndangeredAnimal.save();
    testEndangeredAnimal.delete();
    assertEquals(0, EndangeredAnimal.all().size());
  }

  @Test
  public void delete_deletesSightingAssociations(){
    testEndangeredAnimal.save();
    Sighting sighting = new Sighting("Zone A", "Mac");
    sighting.addAnimal(testEndangeredAnimal);
    sighting.save();
    testEndangeredAnimal.delete();
    assertEquals(0, sighting.getEndangeredAnimals().size());
  }

  @Test
  public void getSightings_returnsAllSightings_int(){
    testEndangeredAnimal.save();
    Sighting testSighting = new Sighting("Zone A", "Mac");
    testSighting.save();
    testSighting.addAnimal(testEndangeredAnimal);
    List savedSightings = testEndangeredAnimal.getSightings();
    assertEquals(1, savedSightings.size());
    assertTrue(savedSightings.contains(testSighting));
  }
}
