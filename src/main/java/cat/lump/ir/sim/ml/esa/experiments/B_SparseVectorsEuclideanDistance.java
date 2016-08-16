package cat.lump.ir.sim.ml.esa.experiments;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.algebra.vector.VectorStorageAbstract;
import cat.lump.aq.basics.algebra.vector.VectorStorageSparse;
import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.experiments.CorrelationsxCategory;

/**
 * This class takes as input a (pair of) vector obj file and computes 
 * the distance between all the pairs. 
 * 
 * INPUT:
 * - obj file with sparse vectors
 * - [second obj file with sparse vectors] 
 * - [output file] 
 * 
 * OUTPUT
 * - txt file with the distances
 *  
 * @author alberto
 * @Since August, 2016
 * 
 */
public class B_SparseVectorsEuclideanDistance {
	
	private static LumpLogger logger = 
			new LumpLogger (B_SparseVectorsEuclideanDistance.class.getSimpleName());
	
	private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "distances.txt";
	
	private static String inputFileA;
	
	private static String inputFileB;
	
	private static String outputFile;
	
	
	public static void compute() {
		if (inputFileA.equals(inputFileB)) {
			VectorStorageSparse x =  (VectorStorageSparse) FileIO.readObject(new File(inputFileA));
			//System.out.println(x.size());
//			System.out.println(x.allValues.size());
//			x
//			for (TreeMap<Integer, Float> y : x.allValues) {
//				System.out.println(y.ky);
//			}
			compute((VectorStorageAbstract) FileIO.readObject(new File(inputFileA)));
		} else {
			compute((VectorStorageSparse) FileIO.readObject(new File(inputFileA)),
					(VectorStorageSparse) FileIO.readObject(new File(inputFileB)));
		}
	}
	
	public static void compute(VectorStorageAbstract vss) {
		//System.out.println(vss.size());
		List<String> ids = vss.getIds();
		
		for (int i = 0; i < ids.size() -1 ; i++) {
			for (int j = i; j < ids.size() ; j++) {
				System.out.format("%s\t%s\t%f%n", ids.get(i),ids.get(j), 
				
				distance(vss.getValues(ids.get(i)), vss.getValues(ids.get(j))));//compute distance
			}
		}
	}
	
	public static void compute(VectorStorageSparse vss1, VectorStorageSparse vss2) {
		List<String> ids1 = vss1.getIds();
		List<String> ids2 = vss2.getIds();		
		for (int i = 0; i < ids1.size() ; i++) {
			for (int j = 0; j < ids2.size() ; j++) {
				System.out.format("%s\t%s\t%f%n", ids1.get(i),ids2.get(j), 
						
						distance(vss1.getValues(ids1.get(i)), vss2.getValues(ids2.get(j))));//compute distance
						//compute distance
			}
		}
		
	}
	
	private static double distance(Map<Integer, Float> v1, Map<Integer, Float> v2) {
		Set<Integer> indexOnly1 = new HashSet<Integer>();
		Set<Integer> indexOnly2 = new HashSet<Integer>();
		Set<Integer> indexBoth  = new HashSet<Integer>();
		
		indexOnly1.addAll(v1.keySet());
		indexOnly1.removeAll(v2.keySet());
		
		indexOnly2.addAll(v2.keySet());
		indexOnly2.removeAll(v1.keySet());
		
		indexBoth.addAll(v1.keySet());
		indexBoth.retainAll(v2.keySet());
				
		double sum =0;
		
		//squares for inds only in v1
		for (int i : indexOnly1) {
			sum += v1.get(i) * v1.get(i);			
		}
		
		//squares for inds only in v2
		for (int i : indexOnly2) {
			sum += v2.get(i) * v2.get(i);			
		}
		
		//squares for inds in both; difference
		for (int i : indexBoth) {
			sum += Math.pow(v1.get(i) - v2.get(i),2);
		}
		
		return Math.sqrt(sum);
	}
	
	
	private void setInputFileA(String file) {
		CHK.CHECK_NOT_NULL(file);
		CHK.CHECK(new File(file).exists(), "I cannot read input file " + file);
		inputFileA = file;
	}
	
	private void setInputFileB(String file) {
		CHK.CHECK_NOT_NULL(file);
		CHK.CHECK(new File(file).exists(), "I cannot read input file " + file);
		inputFileB = file;
	}
	
	private static void setOutputFile(String outFile) {
		if (new File(outFile).exists()) {
			logger.error(String.format("Output file %s already exists", outFile));
			System.exit(1);		
		}
		outputFile = outFile;
	}
	
	private void setup(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		int widthFormatter = 88;		
		
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("a", "filea", true, 
					"First object file with sparse representations (computed with A_EsaRepresentationComputer)");		
		options.addOption("b", "fileb", true, 
			        "Seconf object file with sparse representations (optional)");		
		options.addOption("h", "help", false, "This help");
		
		options.addOption("o", "outputFile", true,
			        "Output file, where the distances are going to be dumped");

		CommandLine cLine = null;
		
		try {			
		    cLine = parser.parse( options, args );
		} catch( ParseException exp ) {
			logger.error( "Unexpected exception:" + exp.getMessage() );			
		}	
		
		if (cLine == null || ! (cLine.hasOption("a"))	
		   ) {
			logger.error("Please, provide the necessary parametets (at least a)");
			//formatter.printHelp(widthFormatter, command, header, options, footer, true)
			formatter.printHelp(B_SparseVectorsEuclideanDistance.class.getSimpleName(),options);
			System.exit(1);
		}
		
		setInputFileA(cLine.getOptionValue("a"));
		
		if (cLine.hasOption("b")) {
			setInputFileB(cLine.getOptionValue("b"));
		} else {
			//TODO this is spatially wrong. Fix if necessary
			setInputFileB(cLine.getOptionValue("a"));
		}
		
		if (cLine.hasOption("o")) {		
			setOutputFile(cLine.getOptionValue("o"));
		} else {
			File f = new File(inputFileA);
			//The path will be to that of the first input file
			String path = f.getParent();
			if (path == null) {
				path = f.getName() + ".";
			} else {
				path = path + File.separator + f.getName() + ".";
			}
			path = path + new File(inputFileB).getName() + DEFAULT_OUTPUT_FILE_SUFFIX; 
			setOutputFile(path);
		}
	}
	
	public static void main(String[] args) {
		B_SparseVectorsEuclideanDistance svd = new B_SparseVectorsEuclideanDistance();
		svd.setup(args);
		svd.compute();
	}
	
	
}
