package cat.lump.aq.basics.structure.standard;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class to sort a map according to it values. The class was borrowed from
 * http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java, 
 * where the contributor gives explicit permission for re-using.
 * 
 * @author albarron
 *
 */
public class MapUtil {

	
	/**
	 * 
	 * @param map
	 * @return	
	 * 			The map itself, sorted by value
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
    
	/**
	 * 
	 * @param map
	 * @return	
	 * 			The map itself, sorted by value in inverse order
	 */
    public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValueInverse( Map<K, V> map )
    {
    	List<Map.Entry<K, V>> list =
    			new LinkedList<Map.Entry<K, V>>( map.entrySet() );    	
    	Collections.sort( list, new Comparator<Map.Entry<K, V>>()
    			{
    		public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
    		{
    			return (o2.getValue()).compareTo( o1.getValue() );
    		}
    			} );

    	Map<K, V> result = new LinkedHashMap<K, V>();
    	for (Map.Entry<K, V> entry : list)
    	{
    		result.put( entry.getKey(), entry.getValue() );
    	}
    	return result;
    }
}