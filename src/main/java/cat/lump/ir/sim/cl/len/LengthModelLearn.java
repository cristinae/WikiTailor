package cat.lump.ir.sim.cl.len;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import cat.lump.aq.basics.check.CHK;

/**
 * Class to learn the parameters of the length model from a parallel 
 * corpus (two files). 
 * 
 * @see cat.lump.sim.cl.len.LengthModel
 * @author albarron
 *
 */
public class LengthModelLearn  {

	private File srcFile;
	private File trgFile;
	
	private double mu;
	private double sigma;

	/**Invoke without setting the source and target files
	 * (it's going to be done by the calling class*/
	protected LengthModelLearn(){		
	}
	
	/**
	 * Invocation with the source and target files
	 * @param srcFile
	 * @param trgFile
	 */
	public LengthModelLearn(File srcFile, File trgFile){
		setFiles(srcFile, trgFile);		
	}
	
	/**Esimate the mu and sigma parameters for the parallel corpus provided */
	public void learn() {
		List<Double> x = new ArrayList<Double>();
		
		List<Integer> srcLen = computeLengths(srcFile);
		List<Integer> trgLen = computeLengths(trgFile);

		CHK.CHECK(srcLen.size() == trgLen.size(), 
				"Source and target must have the same number of instances");
		
		for (int i =0; i < srcLen.size(); i++){
			if (! (srcLen.get(i) == 0 || trgLen.get(i) == 0)){
				//Division of the length
				x.add((double) trgLen.get(i) / srcLen.get(i) );										
			}
		}		

		Apfloat Mu = computeMean(x);		
		Apfloat Sigma = computeDesv(x, Mu.doubleValue());
		mu = Mu.doubleValue();
		sigma = Sigma.doubleValue();
		System.out.println("Number of pair files considered: "+x.size());
		System.out.println("Mean= "+Mu+" Standard deviation= "+Sigma);		
	} 
	
	
	//TODO method for the document-level computation
//	public void learn() throws IOException {
//		double lenS;
//		double lenT;
//		List<Double> x = new ArrayList<Double>();
//		
//		String[] src_text = FileIO.fileToString(src_file).split("\n");
//		String[] trg_text = FileIO.fileToString(trg_file).split("\n");
//		
//		if (src_text.length != trg_text.length){
//			Reporter.report("Source and target must have the same number of instances");
//			System.exit(-1);
//		}		
//		
//		for (int i =0; i < src_text.length; i++){
//			lenS = src_text[i].replaceAll("\\s+", " ").length();
//			lenT = trg_text[i].replaceAll("\\s+", " ").length();
//			x.add(lenT / lenS );	//Division of the length
//			Reporter.reportIfMod("Currently processing instance " + String.valueOf(i), 
//							i, 500);			
//		}		
//
//		Apfloat Mu = computeMean(x);
//		Apfloat Sigma = computeDesv(x, Mu.doubleValue());
//		Reporter.report("Number of pair files considered: "+x.size());
//		System.out.println("Mean= "+Mu+" Standard deviation= "+Sigma);		
//	}

	
	
	/**Opens the file and computes lengths for every line within
	 * @param f input file
	 * @return list of lengths (integers)
	 */
	public List<Integer> computeLengths(File f) {
		List<Integer> lengths = new ArrayList<Integer>();
		LineIterator it = null;
		
		try {
			it = FileUtils.lineIterator(f, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (it.hasNext())				
				lengths.add(it.nextLine().replaceAll("\\s+", " ")
											.length());			
		} finally {
			it.close();
		}
		return lengths;
	}
	
	/**
	 * Set the two input files
	 * @param srcFile
	 * @param trgFile
	 */
	public void setFiles(File srcFile, File trgFile){
		CHK.CHECK_NOT_NULL(srcFile);
		CHK.CHECK_NOT_NULL(trgFile);
		this.srcFile = srcFile;
		this.trgFile = trgFile;
		//TODO change to log4j
		System.out.println("Source file: " + srcFile);
		System.out.println("Target file: " + trgFile);
	}
	
	public double getMu(){
		return mu;
	}
	
	
	public double getSigma(){
		return sigma;
	}
	
	/**
	 * Calculates the mean of the double values in valuesList
	 * @param valuesList	A list containing double elements
	 * @return				Mean of the elements in valuesList
	 */
	private Apfloat computeMean(List<Double> valuesList){
		Apfloat sum = new Apfloat(0);
		Apfloat count= new Apfloat(0);
		Apfloat constant = new Apfloat(1);
			
		for (Double value : valuesList){
			count = count.add(constant);
			sum = sum.add(new Apfloat(value));
		}
		return sum.divide(count);
	}
	
	/**
	 * Calculates standard deviation of elements in valuesList given 
	 * the mean
	 * @param valuesList	A list containing double elements
	 * @param mean			The previously calculated mean of valuesList
	 * @return				Standard deviation
	 */
	private Apfloat computeDesv(List<Double> valuesList, double mean){
		Apfloat sum = new Apfloat(0);
				
		for (Double value : valuesList)			
			sum = sum.add(new Apfloat( Math.pow(value - mean, 2) )) ;

		Apfloat desv = ApfloatMath.sqrt(
								sum.divide(new Apfloat(valuesList.size())));	
		return desv;		
	}
	
}