import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class RegularAnimalTest{
  RegularAnimal testRegularAnimal;
  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Before
  public void setUp(){
    testRegularAnimal = new RegularAnimal("lion", "cat");
  }

  @Test
  public void animals_instantiatesCorrectly_true() {
    assertTrue(testRegularAnimal instanceof RegularAnimal);
  }

  @Test
  public void name_instantiatesCorrectly_true() {
    assertEquals("cheetah", testRegularAnimal.getName());
  }

  @Test
  public void species_instantiatesCorrectly_true() {
    assertEquals("cheetah", testRegularAnimal.getSpecies());
  }

  @Test
  public void equals_returnsTrueIfPropertiesAreSame_true(){
    RegularAnimal testRegularAnimal2 = new RegularAnimal("lion", "cat");
    assertTrue(testRegularAnimal.equals(testRegularAnimal2));
  }

  @Test
  public void save_insertsRegularAnimalIntoDatabase_RegularAnimal() {
    testRegularAnimal.save();
    RegularAnimal testRegularAnimal2 = null;
    try(Connection con = DB.sql2o.open()){
      testRegularAnimal2 = con.createQuery("SELECT * FROM animals WHERE name='lion'").throwOnMappingFailure(false)
      .executeAndFetchFirst(RegularAnimal.class);
    }
    assertTrue(testRegularAnimal2.equals(testRegularAnimal));
  }

  @Test
  public void save_assignsIdToRegularAnimal() {
    testRegularAnimal.save();
    RegularAnimal testRegularAnimal2 = RegularAnimal.all().get(0);
    assertEquals(testRegularAnimal.getId(), testRegularAnimal2.getId());
  }

  @Test(expected=IndexOutOfBoundsException.class)
  public void find_throwsExceptionIfAnimalNotFound() {
    RegularAnimal.find(1);
  }

  @Test
  public void findbyName_returnsRegularAnimalWithSameName_secondRegularAnimal() {
    testRegularAnimal.save();
    RegularAnimal testRegularAnimal2 = new RegularAnimal("bitey", "squirrel");
    testRegularAnimal2.save();
    assertEquals(RegularAnimal.findByName(testRegularAnimal2.getName()), testRegularAnimal2);
  }

  @Test
  public void delete_deletesEntryInDatabase_0(){
    testRegularAnimal.save();
    testRegularAnimal.delete();
    assertEquals(0, RegularAnimal.all().size());
  }

  @Test
  public void delete_deletesSightingAssociations(){
    testRegularAnimal.save();
    Sighting sighting = new Sighting("ZONE A", "Mac");
    sighting.save();
    sighting.addAnimal(testRegularAnimal);
    testRegularAnimal.delete();
    assertEquals(0, sighting.getRegularAnimals().size());
  }

  @Test
  public void getSightings_returnsAllSightings_int(){
    testRegularAnimal.save();
    Sighting testSighting = new Sighting("ZONE A", "Mac");
    testSighting.save();
    testSighting.addAnimal(testRegularAnimal);
    List savedSightings = testRegularAnimal.getSightings();
    assertEquals(1, savedSightings.size());
    assertTrue(savedSightings.contains(testSighting));
  }
}
