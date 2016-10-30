package cat.lump.ir.sim.ml.esa.experiments;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class B_SparseVectorsDistance {
	
	private static LumpLogger logger = 
			new LumpLogger (B_SparseVectorsDistance.class.getSimpleName());
	
	private static final String DEFAULT_OUTPUT_FILE_SUFFIX = "distances.txt";
	
	private static String inputFileA;
	
	private static String inputFileB;
	
//	private static String outputFile;
	
	
	public void compute() {
	   if (inputFileA.equals(inputFileB)) {
//	      VectorStorageSparse x =  (VectorStorageSparse) FileIO.readObject(new File(inputFileA));
	      //System.out.println(x.size());
//	      System.out.println(x.allValues.size());
//	      x
//	      for (TreeMap<Integer, Float> y : x.allValues) {
//	        System.out.println(y.ky);
//	      }
	      compute((VectorStorageAbstract) FileIO.readObject(new File(inputFileA)));
	    } else {
	      compute((VectorStorageSparse) FileIO.readObject(new File(inputFileA)),
	          (VectorStorageSparse) FileIO.readObject(new File(inputFileB)));
	    }

	}

	public static void compute(VectorStorageAbstract vss) {
    //System.out.println(vss.size());
    List<String> ids = vss.getIds();
    double angDist;
    StringBuffer sb = new StringBuffer();
    //Compute magnitudes
    Map<String, Double> magnitudes = computeMagnitudes (vss);
    for (int i = 0; i < ids.size() -1 ; i++) {
      
      for (int j = i; j < ids.size() ; j++) {
        sb.append(ids.get(i))
          .append(" ")
          .append(ids.get(j))
          .append(" ");
        angDist = 
            Math.acos(
                dotProduct(vss.getValues(ids.get(i)), vss.getValues(ids.get(j)) ) /
                (magnitudes.get(ids.get(i)) * magnitudes.get(ids.get(j)))
            );
        sb.append(angDist)
          .append("\n");
         
      }
      System.out.println(sb.toString());
      sb.delete(0, sb.length());
    }
  }

  /**
   * Compute angular distances between all the pairs of vectors within the 
   * sparse representations vss1 and vss2
   * @param vss1
   * @param vss2
   * TODO test
   */
  public static void compute(VectorStorageSparse vss1, VectorStorageSparse vss2) {
    List<String> ids1 = vss1.getIds();
    List<String> ids2 = vss2.getIds();    
    double angDist;
  //Compute magnitudes
    Map<String, Double> magnitudes = computeMagnitudes (vss1);
    magnitudes.putAll(computeMagnitudes(vss2));
    StringBuffer sb = new StringBuffer();
    
    for (int i = 0; i < ids1.size() ; i++) {
      for (int j = 0; j < ids2.size() ; j++) {
        sb.append(ids1.get(i))
        .append(" ")
        .append(ids2.get(j))
        .append(" ");
      //compute angular distance
        angDist = 
            Math.acos(
                dotProduct(vss1.getValues(ids1.get(i)), vss2.getValues(ids2.get(j)) ) /
                (magnitudes.get(ids1.get(i)) * magnitudes.get(ids2.get(j)))
            );
        sb.append(angDist)
        .append("\n");
      }
      System.out.println(sb.toString());
      sb.delete(0, sb.length());
    }
    
  }
	


  @Deprecated
  public static void computeDeprecated() {
    if (inputFileA.equals(inputFileB)) {
//      VectorStorageSparse x =  (VectorStorageSparse) FileIO.readObject(new File(inputFileA));
      //System.out.println(x.size());
//      System.out.println(x.allValues.size());
//      x
//      for (TreeMap<Integer, Float> y : x.allValues) {
//        System.out.println(y.ky);
//      }
      compute((VectorStorageAbstract) FileIO.readObject(new File(inputFileA)));
    } else {
      compute((VectorStorageSparse) FileIO.readObject(new File(inputFileA)),
          (VectorStorageSparse) FileIO.readObject(new File(inputFileB)));
    }
  }	
	
	@Deprecated
	public static void computeDeprecated(VectorStorageAbstract vss) {
		//System.out.println(vss.size());
		List<String> ids = vss.getIds();
		
		for (int i = 0; i < ids.size() -1 ; i++) {
			for (int j = i; j < ids.size() ; j++) {
				System.out.format("%s\t%s\t%f%n", ids.get(i),ids.get(j), 
				
				distance(vss.getValues(ids.get(i)), vss.getValues(ids.get(j))));//compute distance
			}
		}
	}
	
	@Deprecated
	public static void computeDeprecated(VectorStorageSparse vss1, VectorStorageSparse vss2) {
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
	
	
	 private static Map<String, Double> computeMagnitudes(VectorStorageAbstract vss) {
	    Map<String, Double> magnitudes = new HashMap<String, Double> ();
	    double magn;
	    for (String s : vss.getIds()) {
	      magn = 0;
	      for (float val : vss.getValues(s).values()) {
	        magn += val * val; 
	      }
	      magnitudes.put(s, Math.sqrt(magn));
	    }
	    
	    return magnitudes;
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
	 
	/**
	 * Compute the dot product between two sparse representations.
   *  
   * @param v1
   * @param v2
   * @return dotproduct(v1, v2)
   * TODO test this method; I guess it should be part of the sparse and
   * dense representation classes.
	 */
	private static double dotProduct(Map<Integer, Float> v1, Map<Integer, Float> v2) {
	  double dp = 0;
	  Set<Integer> intersection  = new HashSet<Integer>();
	  
	  intersection.addAll(v1.keySet());
	  intersection.retainAll(v2.keySet());
	  
    for (int i : intersection) {
        dp += v1.get(i) * v2.get(i);
    }  
	  return dp;
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
//		outputFile = outFile;
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
			formatter.printHelp(B_SparseVectorsDistance.class.getSimpleName(),options);
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
		B_SparseVectorsDistance svd = new B_SparseVectorsDistance();
		svd.setup(args);
		svd.compute();
	}
	
	
}
