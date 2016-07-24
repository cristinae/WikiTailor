package cat.lump.aq.basics.algebra.vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.log.LumpLogger;


/**
 * Abstract implementation of a vector storage based on the one we use in Iyas. 
 * It is currently extended by a dense and a sparse representation.
 * 
 * TODO Make ir serializable to store the object
 * 
 *  
 * @author albarron
 * @since Jul 2016
 */
public abstract class VectorStorageAbstract {

	protected static LumpLogger logger = 
			new LumpLogger (VectorStorageAbstract.class.getSimpleName());



	/** Dictionary with the names of the instances */
	//TODO might be a list or a Map with short on both sides
	protected Map<String, Integer> mapOfInstances;

	/** Keeps track of the max current index available in the instances */
	protected int maxIndexInstances;

	public VectorStorageAbstract() {    

		mapOfInstances = new LinkedHashMap<String, Integer>();
		maxIndexInstances = 0;
	}



	/** 
	 * Add a value to the given instance 
	 * @param instanceId  identifier of the instance
	 * @param index for the value
	 * @param value 
	 */
	public abstract void add(String instanceId, int index, float value);

	/** Print all the instances and values */
	public abstract void display();

	/**
	 * @param i index of the instance
	 * @return  Map with all the values for the given instance 
	 */
	public abstract Map<Integer, Float> getValues(int i);

	/**
	 * Add to every instance all the values. If the <code>index</code> is not 
	 * included among the existing ones, it is added as a sparse representation
	 * (this is only true for the sparse repr., of course).
	 * 
	 * @param instanceIds ids of the instances to be updated
	 * @param index for the values to be assigned
	 * @param values
	 */
	public void add(String[] instanceIds, short index, float[] values) {
		CHK.CHECK(instanceIds.length == values.length, 
				"The number of instances and values should match");
		for (int i = 0; i < values.length ; i++) {
			add(instanceIds[i], index, values[i]);
		}
	}


	/**
	 * Update all the values in the given instance.
	 * 
	 * @param instanceId  id of the instances to be updated
	 * @param indexes  name of the feature to be assigned
	 * @param values 
	 */
	public void add(String instanceId, short[] indexes, float[] values) {
		CHK.CHECK(indexes.length == values.length, 
				"The number of indexes and values should match");
		for (int i = 0; i < values.length ; i++) {
			add(instanceId, indexes[i], values[i]);
		}
	}

	/**
	 * Update the values in all the given instances. If the 
	 * <code>index</code> is not included among the dense features, it is 
	 * added as a sparse feature.
	 * 
	 * @param instanceIds ids of the instances to be updated
	 * @param indexes	for the values
	 * @param values 
	 */
	public void add(String[] instanceIds, short[] indexes, float[][] values) {
		CHK.CHECK(instanceIds.length == values.length, 
				"The number of instances and values vectors should match");
		CHK.CHECK(indexes.length == values[0].length, 
				"The number of features and the size of the vector should match");
		for (int i = 0; i < values.length ; i++) {
			add(instanceIds[i], indexes, values[i]);      
		}
	}

	/**
	 * @param id
	 * @return true if the instance is included in the storage
	 */
	public Boolean checkInstanceExists(String id) {
		return mapOfInstances.containsKey(id);
	}

	/**
	 * @param index
	 * @return true if the index exists in the storage
	 */
	public Boolean checkInstanceIndexExist(int index) {
		return (index < mapOfInstances.size() && index >= 0);
	}

	/**
	 * @return number of instances stored
	 */
	public int size() {
		return maxIndexInstances + 1;
	}

	//  /**
	//   * @return a list with the names of the features
	//   */
	//  public Set<Short> getIndexes() {
	//    return setOfValues;
	//  }

	/**
	 * @return  ids of all the instances in the storage
	 */
	public List<String> getIds() {
		List<String> ids = new ArrayList<String>();
		//TODO apparently this key set is ordered, as it is in the map. DOUBLE CHECK
		for (String id : mapOfInstances.keySet()) {
			ids.add(id);
		}
		return ids;
	}

	/**
	 * @return Entire list of instances representations. The values are stored 
	 *          in a map 
	 */
	public List<Map<Integer, Float>> getValues() {
		//TODO this doesn't have to be an internal map (probably)
		List<Map<Integer, Float>> features = new ArrayList<Map<Integer, Float>>();
		for (int i = 0; i < mapOfInstances.size() ; i++) {
			features.add(getValues(i));
		}
		return features;  
	}

	/**
	 * @param id for the instance
	 * @return Map with the values for the given instance 
	 */
	public Map<Integer, Float> getValues(String id) {
		CHK.CHECK(checkInstanceExists(id), 
				String.format("No instance with id %s exists", id));  
		return getValues(mapOfInstances.get(id));
	}

	/**
	 * Check if the instance with the given id exists already.
	 * @param instanceId
	 * @return
	 */
	protected boolean instanceExists(String instanceId) {
		return mapOfInstances.containsKey(instanceId);
	}

}
