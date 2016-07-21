package cat.lump.aq.basics.algebra.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cat.lump.aq.basics.check.CHK;

/**
 * A storage for a collection of instances and their associated dense vectors.
 * 
 * The dimensions of the dense representation have to be known at construction 
 * time. 
 *
 * New values can be added in multiple ways including:
 * <ul>
 * <li> one instance--one value
 * <li> one instance--multiple values
 * <li> multiple instances--one value (same value for all)
 * <li> multiple instances--multiple values
 * </ul>
 * 
 * Accessing the dataset has also multiple options:
 * <ol>
 * <li> one specific value for one specific instance TODO
 * <li> all the values for a specific instance
 * <li> all the values for all the dataset
 * </ol>
 * 
 * In the case of 2, the vector can be accessed as an array of Float[]. In 
 * the three cases, the values are available as Map<String, Float>
 * 
 * @author albarron
 *
 * TODO best way to check the integrity of the data before returning it 
 *(e.g., all the values in the dense representation have been assigned)
 */
public class VectorStorageDense extends VectorStorageAbstract {

  /**Internal storage for all the dense features in the dataset  */
  private List<Float[]> allValues;
  
  /** The dimension of the vectors */
  private short dimension;
  
//  /**
//   * Dictionary with the names of the features in the dense representation. 
//   * The position in the list is the position of the feature in the vectors. 
//   * Assigned at invocation time.
//   */
//  private Map<String, Short> mapOfFeatures;

  /**
   * Initialise all the internal data structures to store the instances with
   * their dense representation. 
   * 
   * @param listOfFeatures Names of the features in the dense representation.
   */
  public VectorStorageDense(short dimension) {
    super();
    CHK.CHECK(dimension > 0, "The dimension cannot be lower than 0");
    allValues = new ArrayList<Float[]> ();
    this.dimension = dimension;
  }
  
  /**
   * Add a value to the vector storage. If the index is higher than expected,  
   * it triggers an error. 
   * 
   * @param instanceId unique id of the instanceId
   * @param featureName unique name of the feature
   * @param value of the computed feature
   */
  public void add(String instanceId, int ind, float value) {
    CHK.CHECK(ind < dimension, 
        String.format("Value %d is not in the vector's range", ind));
    
    //Add a new instance if I don't have it beforehand.
    if (! instanceExists(instanceId)) {
      addInstance(instanceId);
    }    
    checkPossibleOverriding(instanceId, ind);
    allValues.get(
        mapOfInstances.get(instanceId))[ind] = value;
  }

  /** Print all the instances and features */
  public void display() {
    
    //Print the features representation
    System.out.println("\nDENSE REPRESENTATION\n");
    
    //Print the header
    System.out.print("ID\t");
    for (short i = 0 ; i < dimension; i++) {
      System.out.format("%s\t\t", i);
    }
    System.out.println();
    
    //Print all the instances
    for (Entry<String, Integer> entry : mapOfInstances.entrySet()) {
      System.out.format("%s\t", entry.getKey());
      for (int i = 0; i < vectorDimension(); i++) {
        //System.out.println(allFeatures.get(entry.getValue())[i]);
        System.out.format("%f\t", allValues.get(entry.getValue())[i]);      
      }
      System.out.println();
    }
  }

  /**
   * @return length of the representation vector
   */
  public short vectorDimension() {
    return dimension;
  }

//  /**
//   * @return  List of instances with dense features. The features are stored 
//   *          in a map
//   */
//  public List<Map<String, Float>> getDenseFeatures() {
//    List<Map<String, Float>> features = new ArrayList<Map<String, Float>>();
//    for (int i = 0; i < mapOfInstances.size() ; i++) {
//      features.add(getDenseFeatures(i));
//    }
//    return features;
//  }
  
  /**
   * @return Entire list of instance representations. Features are stored in arrays
   */
  public List<Float[]> getFeaturesArray() {
    return allValues;
  }
  
//  /**
//   * @return All the dense vector representations without identifiers
//   */
//  public List<Float[]> getDenseFeaturesWithoutIdentifiers() {
//    return denseFeatures;
//  }
  
  /**
   * @param id  instance id
   * @return  The dense vector representation for the given id
   */
  public Float[] getFeaturesArray(String id) {
    CHK.CHECK(checkInstanceExists(id), 
        String.format("No instance with id %s exists", id));
    return getFeaturesArray(mapOfInstances.get(id));
  }

  public Float[] getFeaturesArray(int ind) {
    CHK.CHECK(checkInstanceIndexExist(ind), 
        String.format("No instance in index %d exists", ind));
    return allValues.get(ind);
  }

//  /**
//   * @param ind index of the instance in the dataset
//   * @return  Map with the feature representation for the given instance
//   */
//  public Map<String, Float> getFeatures(int ind) {
//    CHK.CHECK(checkInstanceIndexExist(ind), 
//        String.format("No instance in index %d exists", ind));
//
//    Map<String, Float> instanceFeatures = new HashMap<String, Float>();
//    
//    for (short entry : mapOfFeatures) {
//    instanceFeatures.put(
//        entry, allFeatures.get(ind)[entry]);
//    }
//    return instanceFeatures;
//  }

//  /**
//   * @param id for the instance
//   * @return Map with the dense feature representation for the given instance 
//   */
//  public Map<String, Float> getDenseFeatures (String id) {
//    CHK.CHECK(checkInstanceExists(id), 
//        String.format("No instance with id %s exists", id));
//   return getDenseFeatures(mapOfInstances.get(id));
//  }
//  
//  /**
//   * @param ind index of the instance in the dataset
//   * @return  Map with the dense feature representation for the given instance 
//   */
//  public Map<String, Float> getDenseFeatures (int ind) {
//    CHK.CHECK(checkInstanceIndexExist(ind), 
//        String.format("No instance in index %d exists", ind));
//    
//    Map<String, Float> instanceFeatures = new HashMap<String, Float>();
//    
//    for (Entry<String, Short> entry : mapOfFeatures.entrySet()) {
//      instanceFeatures.put(
//          entry.getKey(), denseFeatures.get(ind)[entry.getValue()]);
//    }
//    return instanceFeatures;
//  }
  


//  /**
//   * @param ind index of the instance in the dataset
//   * @return  TreeMap with the dense feature representation for the given instance 
//   */
//  public TreeMap<Short, Float> getSortedDenseFeatures (int ind) {
//    CHK.CHECK(checkInstanceIndexExist(ind), 
//        String.format("No instance in index %d exists", ind));
//    
//    TreeMap<Short, Float> instanceFeatures = new TreeMap<Short, Float>();
//    
//    for (Entry<String, Short> entry : mapOfFeatures.entrySet()) {
//      instanceFeatures.put(
//          entry.getValue(), denseFeatures.get(ind)[entry.getValue()]);
//    }
//    return instanceFeatures;
//  }

  /**
   * Adds a new instance to the collection.
   * 
   *  As a result, a new row of dense and sparse vectors is added. The dense
   *  vector is initialised to <code>Float.MIN_VALUE<code> for further 
   *  verification (e.g., avoid overriding or forgetting to set a value).
   *  
   *  The program stops if an already existing instance is added again.
   *  
   * @param instanceId
   */
  private void addInstance(String instanceId) {
    if (checkInstanceExists(instanceId)) {
      logger.error(String.format("[ERROR] Instance %s already exists", instanceId));
      System.exit(1);
    } else {
      mapOfInstances.put(instanceId, maxIndexInstances ++);
      Float[] d = new Float[dimension];
      Arrays.fill(d, Float.MIN_VALUE);
      allValues.add(d);
    }
  }
  

  
  /**
   * Stops the program if a value had been assigned to the dense instance for this 
   * specific feature.
   * 
   * @param instance
   * @param featureName
   */
  private void checkPossibleOverriding(String instance, int ind) {
             
    CHK.CHECK(
        allValues.get(mapOfInstances.get(instance))
              [ind] == Float.MIN_VALUE,
        String.format("Instance %s has been assigned the dense feature %d already! %f", 
            instance, ind,allValues.get(mapOfInstances.get(instance))[ind])
            );
  }

@Override
public Map<Integer, Float> getValues(int i) {
	// TODO Auto-generated method stub
	return null;
}
}
