package cat.lump.ir.retrievalmodels.similarity;

import cat.lump.aq.basics.algebra.vector.Vector;

public interface SimilarityMeasure {

public double compute(Vector v1, Vector v2);
	
}
