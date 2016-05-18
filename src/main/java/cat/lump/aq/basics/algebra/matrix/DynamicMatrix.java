/**
 * Created on 26 Apr 2012<br><br>
 * Software being developed by lbarron
 */

package cat.lump.aq.basics.algebra.matrix;

import java.io.Serializable;

/**
 * Abstract class for the creation of dynamic matrices of different types <E>.
 * @author albarron
 */
public abstract class DynamicMatrix implements Serializable{

	/**If an object is saved */
	private static final long serialVersionUID = -5584320123623536940L;

	/**the size of the matrix (which changes depending on
	 * the inserted elements */
	protected int size = 0; 
	
	/**dimension of the matrix. It is defined once and
	 * cannot change*/
	protected int dimension;
	
	public abstract Object get(int position);	
	
	/**	@return a copy of the entire matrix */
	public abstract Object getMatrix();
	
	
}
