package cat.lump.ir.sim.cl.len;

import java.io.File;
import java.io.IOException;

import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;

/**
 * A class to estimate the length factor between two texts according to
 * previously learnt parameters.
 * 
 * @see cat.talp.lump.sim.lenMod.LengthModel
 * 
 * @author albarron
 * @version 0.3
 * 
 */
public class LengthModelEstimate {

	/** Source file; one sentence per line */
	private File srcFile;

	/** Target file; one sentence per line */
	private File trgFile;

	/**Source sentences */
	private String[] srcText = null;
	
	
	/**Target sentences */
	private String[] trgText = null;
	
	/** Mean for the distribution */
	private double mu;

	/** Sigma for the distribution */
	private double sigma;

	@SuppressWarnings("unused")
	private Boolean verbose = false;
		
	private double[] estimations;


	public double[] estimate() {
		try {
//			srcText = FileIO.fileToString(srcFile).split("\n");
//			trgText = FileIO.fileToString(trgFile).split("\n");
			srcText = FileIO.fileToLines(srcFile);
			trgText = FileIO.fileToLines(trgFile);
		} catch (IOException e) {			
			e.printStackTrace();
		}

		estimations = new double[srcText.length];
		
		CHK.CHECK(srcText.length == trgText.length, 
				"Source and target must have the same number of lines");

		for (int i = 0; i < srcText.length; i++){
			estimations[i]=lengthFactor(srcText[i].length(), trgText[i].length());
		}
		return estimations;
	}
		
		
	/**Display estimation, source and target sentences */
	public void displayVerbose(){	
		CHK.CHECK(estimations != null, "Before displaying, you have to estimate");
		
		for (int i = 0; i < srcText.length; i++) {
			System.out.println(String.format("%s\t%s\t%s", 
					estimations[i],	srcText[i],	trgText[i]));
		}
	}
		
	/**Display only estimation */
	public void display(){
		CHK.CHECK(estimations != null, 
				"Before displaying, you have to estimate");
		for (int i = 0; i < srcText.length; i++) {
				System.out.println(estimations[i]);
		}
	}

	/**
	 * Estimates the length factors of the sentences among them.
	 * 
	 * @return A matrix of length factors. Each row is a source sentence and
	 *         each column a target one.
	 */
	public double[][] estimateMatrix() {
		String[] srcText = null;
		String[] trgText = null;
		try {
			srcText = FileIO.fileToLines(srcFile);
			trgText = FileIO.fileToLines(trgFile);
//			srcText = FileIO.fileToString(srcFile).split("\n");
//			trgText = FileIO.fileToString(trgFile).split("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[][] matrix = new double[srcText.length][trgText.length];

		for (int i = 0; i < srcText.length; i++) {
			for (int j = 0; j < trgText.length; j++) {
				matrix[i][j] = lengthFactor(srcText[i].length(),
									trgText[j].length());
			}
		}
		return matrix;
	}

	
	/**
	 * Whether the output should include source and target texts or only the 
	 * length factor.
	 * <br>
	 * Use either displayVerbose() or display() instead
	 */
	@Deprecated
	public void setVerbose(Boolean verb) {
		verbose = verb;
	}

	/**
	 * Set the input files to estimate the model from
	 * 
	 * @param srcFile
	 * @param trgFile
	 */
	public void setFiles(File srcFile, File trgFile) {		
		CHK.CHECK(srcFile.exists(), 
			"ERROR: I cannot read the source file " + srcFile);
		CHK.CHECK(trgFile.exists(),
			"ERROR: I cannot read the target file " + trgFile);			
		
		this.srcFile = srcFile;
		this.trgFile = trgFile;
	}

	/**
	 * Set values for mu and sigma
	 * 
	 * @param mu  mean for the distribution
	 * @param sigma  for the distribution
	 */
	public void setMuSigma(double mu, double sigma) {
		CHK.CHECK_NOT_NULL(mu);
		CHK.CHECK_NOT_NULL(sigma);
		this.mu = mu;
		this.sigma = sigma;
	}	
	
	/**
	 * Compute the length factor
	 * 
	 * @param srcLen Length of the source text
	 * @param trgLen Length of the target text
	 * @return Length factor between source and target text
	 */
	public double lengthFactor(double srcLen, double trgLen) {
		double inner = ((trgLen / srcLen) - mu) / sigma;
		double pot = -0.5 * Math.pow(inner, 2);
		return Math.exp(pot);
	}

	// public static double lengthFactor(double lenS, double lenT, double mu,
	// double sigma){
	// /* double lenS = count_characters(sFile);
	// double lenT = count_characters(tFile);*/
	// double inner = ((lenS / lenT)-mu)/sigma;
	// double pot = -0.5 * Math.pow(inner, 2);
	// double lf = Math.exp(pot);
	// return lf;
	// }
	
	// TODO It seems to be for the analysis for entire documents.
	// /**
	// * Counts the characters in the text currentFile
	// * @param currentFile A text file
	// * @return Length of the file in characters
	// * @throws IOException
	// */
	// private static double count_characters(File currentFile) throws
	// IOException{
	// String text = FileIO.fileToString(currentFile);
	// return (double) text.length();
	// }

}