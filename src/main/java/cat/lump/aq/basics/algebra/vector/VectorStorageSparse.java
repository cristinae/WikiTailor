package cat.lump.aq.basics.algebra.vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import cat.lump.aq.basics.check.CHK;



/**
 * A storage for a collection of instances and their associated sparse vectors
 * 
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
 * In the three cases, the values are available as 
 * Map<Short, Double>.
 * 
 * @author albarron
 *
 * TODO best way to check the integrity of the data before returning it 
 *
 */
public class VectorStorageSparse extends VectorStorageAbstract{

	/** Dictionary with the names of the features in the representation. */
	protected Set<Integer> indexOfValues;

	/** Internal storage for all the sparse values in the dataset.  */
	private List<TreeMap<Integer, Float>> allValues;

	//  /**
	//   * Dictionary with the names of the features in the sparse representation. 
//   * It is dynamic as the sparse features are unknown in advance.
//   */
//  private Map<String, Short> mapOfFeatures;
  
  /** Keeps track of the max current index available in the sparse representation */
//  private short maxIndexValues;

  /**
   * Initialise all the internal data structures to store the instances with
   * their sparse representation. 
   */
  public VectorStorageSparse() {
    super();
    indexOfValues = new TreeSet<Integer>();
    allValues = new ArrayList<TreeMap<Integer, Float>>();
//    maxIndexValues = 0;
  }
  
  /**
   * Add a value to the <code>instanceId</code>. 
   * 
   * @param instanceId unique id of the instanceId
   * @param index for the value
   * @param value
   */
  public void add(String instanceId, int index, float value) {
    //Add a new instance if I don't have it beforehand.
    if (! instanceExists(instanceId)) {
      addInstance(instanceId);
    }
    
    //Add a new value if I don't have it beforehand.
    if (! indexOfValues.contains(index)) {
      indexOfValues.add(index);
    }
    checkPossibleOverriding(instanceId, index);
    allValues.get(mapOfInstances.get(instanceId))
    		.put(index, value);
  }
  
  
  public void add(String instanceId, float[] values) {
	for (int i = 0; i < values.length; i++) {
		if (values[i] != 0) {
			add(instanceId, i, values[i]);
		}
	}
  }
  
  /**
   * Add all the instances and values from a dense representation. 
   * @param denseValues dense storage.
   */
  public void add(VectorStorageDense denseValues) {
    for (String id : denseValues.getIds()) {      
      for (Map.Entry<Integer, Float> values : denseValues.getFeatures(id).entrySet()) {
        add(id, values.getKey(), values.getValue());
      }
    }
  }
  
  //TODO do this for the abstract
  public void add(VectorStorageSparse sparseValues) {
    for (String id : sparseValues.getIds()) {      
      for (Map.Entry<Integer, Float> values : sparseValues.getFeatures(id).entrySet()) {
        add(id, values.getKey(), values.getValue());
      }
    }
  }
  
  /* (non-Javadoc)
   * @see qa.qcri.iyas.features.FeatureStorageAbstract#display()
   */
  @Override
  public void display() {

  //Print the sparse representation
    System.out.println("\nSPARSE REPRESENTATION\n");
    
  //Print the header
    System.out.println("ID\tVALUES");
  //Print all the instances
    for (Entry<String, Integer> instance : mapOfInstances.entrySet()) {
      System.out.format("%s\t\t", instance.getKey());
      for (int value : indexOfValues) {
        if (allValues.get(instance.getValue()).containsKey(value)) {
          System.out.format("%d:%f\t", 
              value, 
              allValues.get(instance.getValue()).get(value)
          );
        }
      }
      System.out.println();
    }
  }

//  /**
//   * @return  List of instances with sparse features. The features are stored 
//   *          in a map 
//   */
//  public List<Map<String, Double>> getSparseFeatures() {
//    List<Map<String, Double>> features = new ArrayList<Map<String, Double>>();
//    for (int i = 0; i < mapOfInstances.size() ; i++) {
//      features.add(getSparseFeatures(i));
//    }
//    return features;
//  }

//  /**
//   * @param id for the instance
//   * @return Map with the sparse feature representation for the given instance 
//   */
//  public Map<String, Double> getSparseFeatures (String id) {
//    CHK.CHECK(checkInstanceExists(id), 
//        String.format("No instance with id %s exists", id));
//    return getSparseFeatures(mapOfInstances.get(id));
//  }
  
  /**
   * @param ind index of the instance in the dataset
   * @return  Map with the sparse representation for the given instance 
   */
  public Map<Integer, Float> getValues(int ind) {
    CHK.CHECK(checkInstanceIndexExist(ind), 
        String.format("No instance in index %d exists", ind));
    
    Map<Integer, Float> instanceValues = new HashMap<Integer, Float>();
    
    for (int entry : indexOfValues) {
      if (allValues.get(ind).containsKey(entry)) {
        instanceValues.put(
            entry, allValues.get(ind).get(entry));
      }
    }
    return instanceValues;
  }
  
//  /**
//   * @param ind index of the instance in the dataset
//   * @return  Map with all (dense+sparse) feature representation for the given instance
//   */
//  public Map<String, Double> getFeatures(int ind) {
//    CHK.CHECK(checkInstanceIndexExist(ind), 
//        String.format("No instance in index %d exists", ind));
//    return getSparseFeatures(ind);
//  }

//  /**
//   * @param ind  index of the instance in the dataset
//   * @return treemap with the <short_key, value> for an instance
//   */
//  public TreeMap<Short, Double> getSortedFeatures(int ind) {
//    CHK.CHECK(checkInstanceIndexExist(ind), 
//        String.format("No instance in index %d exists", ind));
//    return allFeatures.get(ind);
//  }
  
  public double[] getVector(int ind) {
	  CHK.CHECK(checkInstanceIndexExist(ind), "No instance wuth this index exists");
	  double[] values = new double[indexOfValues.size()];
	  for (short i = 0; i < indexOfValues.size(); i++) {
		  values[i] = allValues.get(ind).get(i);
	  }
	  return values;
  }

  /**
   * Adds a new instance to the collection.
   * 
   *  As a result, a new row of dense and sparse vectors is added. The dense
   *  vector is initialised to <code>Double.MIN_VALUE<code> for further 
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
      allValues.add(new TreeMap<Integer, Float>());
    }
  }
  
  /**
   * Stops the program if a value had been assigned to the sparse instance for this 
   * specific feature.
   * 
   * @param instance
   * @param featureName
   */
  private void checkPossibleOverriding(String instance, int featureName) {
    CHK.CHECK(
        ! allValues.get(mapOfInstances.get(instance))
              .containsKey(featureName),
        String.format("Instance %s has been assigned the sparse feature %s already!", 
            instance, featureName));
  }


}
