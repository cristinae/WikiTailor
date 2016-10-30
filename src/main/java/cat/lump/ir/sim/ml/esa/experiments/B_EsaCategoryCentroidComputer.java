package cat.lump.ir.sim.ml.esa.experiments;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cat.lump.aq.basics.algebra.vector.VectorStorageAbstract;
import cat.lump.aq.basics.check.CHK;
import cat.lump.aq.basics.check.F;
import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;

/**
 * This class takes a collection of ESA vectors files and a reference of the 
 * articles belonging to a particular domain, previously extracted by a given 
 * model. After computing the centroid of the domain-retrieved articles, 
 * distances against each article and the centroid are computed. 
 * 
 * INPUT
 * <ul>
 * <li /> set of ESA vector files
 * <li /> file with the files belonging to the categories
 * </ul>
 * 
 * OUTPUT
 * - Serialized representation of the centroid vector.
 * (potentially the distance already)
 * 
 * @author albarron
 *
 */
public class B_EsaCategoryCentroidComputer {

  private final String LANGUAGE;
  private final File CATEGORY_FILE;
  
  private final int CATEGORY_ID;
  
  private final int ESA_CARDINALITY = 12539;
//  private int counter;
  
  private Map<String, Set<String>> filesWithCategoryArticles;
  
//  private final String UNIQUE_ID;
   
  private static LumpLogger logger = 
      new LumpLogger (B_EsaCategoryCentroidComputer.class.getSimpleName());
 
  public B_EsaCategoryCentroidComputer(String categoryFile, String esaVectorsPath, String language) {
    CATEGORY_FILE = getCategoryFile(categoryFile);
    CATEGORY_ID = getCategoryIdFromFileName(categoryFile);
    LANGUAGE = language;
//    centroidVector.addInstance(categoryId);
//    counter = 0;
    
  }
  
  public void computeDistances(File pathToFolder) {
    Set<String> articlesInCategory = getArticleIds();
    List<String> vectorFiles =  FileIO.getFilesExt(pathToFolder, ".obj");

    float[] centroid = computeCentroid(vectorFiles, articlesInCategory);
    //The centroid will be computed and stored here

    
    double centroidMagnitude = computeMagnitude(centroid);
    double vectorMagnitud;
//    double sum;
    double angDist;
    // Iterate over the files with the relevant articles to compute distances
    for (Entry<String, Set<String>> entry : filesWithCategoryArticles.entrySet()) {
      //Load all the representations from this file.
      VectorStorageAbstract vss = 
          (VectorStorageAbstract) FileIO.readObject(new File(entry.getKey()));
      
      // Load all the relevant ids from this file
      for (String id : entry.getValue()) {
        vectorMagnitud = computeMagnitude(vss.getValues(id));
//        sum = 0;
        angDist =  Math.acos(    
              dotProduct(centroid, vss.getValues(id)) /
              ( centroidMagnitude * vectorMagnitud )
              );
        System.out.format("dist(%s, centroid) = %f%n", id, angDist);
      }
    }
  }
  
  /**
   * Compute the centroid of a set of vectors in the given category.
   * 
   * @param vectorFiles list with all the necessary vector files
   * @param articlesInCategory  set of articles belonging to a domain
   * @return
   */
  private float[] computeCentroid(List<String> vectorFiles, Set<String> articlesInDomain) {
    float[] centroid= new float[ESA_CARDINALITY];
    int counter = 0;
    //Dictionary with the objects we need to load for the distances
    filesWithCategoryArticles = new HashMap<String, Set<String>>();
//    System.out.println(articlesInDomain);
    // Iterate over all the vector files
    for (String vFile : vectorFiles) {
      VectorStorageAbstract vss = (VectorStorageAbstract) FileIO.readObject(new File(vFile));
      //System.out.println(vss.getCardinality());
      // Load all the current ids.
      Set<String> currentIds = new HashSet<String>();
      currentIds.addAll(vss.getIds());
//      System.out.println("AND THE CURRENT ONES:");
//      System.out.println(currentIds);
      // Retain only the relevant ids for this category
      currentIds.retainAll(articlesInDomain);
      if (currentIds.isEmpty()) {
        logger.info(String.format("No domain ID in file %s", vFile));
        // If none of the IDs here is relevant, discard it and continue
        continue;
      }
      // Store the pointer to the relevant IDs for the current file for the distances step 
      filesWithCategoryArticles.put(vFile, currentIds);
      // Accumulate the sum for all the relevant vectors in the centroid
      for (String id : currentIds) {
        counter ++;
        for (Map.Entry<Integer, Float> entry : vss.getValues(id).entrySet()) {
//          System.out.println(entry.getKey());
          centroid[entry.getKey()] += entry.getValue();
        }
      }
    }
    
    CHK.CHECK(counter!=0, "No single ID belonging to the domain was found in the vectors");
    
    logger.info(
        String.format("Number of articles found for domain %d: %d (%d considered as input)", 
            CATEGORY_ID, counter, articlesInDomain.size()));
    
    
    
    //Divide the centroid by the number of considered instances
    for (int i=0; i < centroid.length ; i++) {
      centroid[i] /= counter;
    }
    return centroid;
  }

  private double computeMagnitude(Map<Integer, Float> sparseVector) {
    double magn = 0;
    for (float value : sparseVector.values()) {
      magn += value * value;
    }
    return Math.sqrt(magn);
  }
  
  private double computeMagnitude(float[] vector) {
    double magn = 0;
    for (int i = 0; i < vector.length ; i++) {
      magn += vector[i] * vector[i];
    }
    return Math.sqrt(magn);
  }
  
  /**Computes the dot product between an array of float (dense vector) and a Map 
   * of float values (sparse representation). The map keys must be equivalent to 
   * the array's dimensions.
   * @param v1 dense vector (array of floats)
   * @param v2 sparse vector (map of int-float)
   * @return  dotproduct(v1, v2)
   */
  private static double dotProduct(float[] v1, Map<Integer, Float> v2) {
    CHK.CHECK(v2.size() <= v1.length, 
        "The sparse vector cannot have higher cardinality than the dense one");
    double dp = 0;
    
    for (int i : v2.keySet()) {
      CHK.CHECK(i >= 0 && i < v1.length, "The index of the sparse representation is wrong");
      dp += v1[i] * v2.get(i);
    }  
    return dp;
  }
  
  
  
  
  private File getCategoryFile(String categoryFile) {
    File f  = new File(categoryFile);
    F.CAN_READ(f, "I cannot read the category file");
    return f;
  }
  
//  
//  public void sum(Map<Integer, Float> values) {
//    counter ++;
//    for (int id : values.keySet()) {      
//      centroidVector.sum(CATEGORY_ID, id, values.get(id)); 
//    }
//  }
//  
  private static CommandLine getOptions(String[] args) {
    
    Options options = new Options();
    CommandLineParser parser = new BasicParser();
    HelpFormatter formatter = new HelpFormatter();
    options.addOption("f", "folder", true, 
        "Path to the obj ESA vectors folder");
    options.addOption("c", "catfiles", true, 
        "File with the categorie's IDs");
    options.addOption("l", "language", true,
        "Two-letters code for the language");
    CommandLine cLine = null;
    try {     
      cLine = parser.parse( options, args );
    } catch( ParseException exp ) {
      logger.error( "Unexpected exception:" + exp.getMessage() );     
    } 
    
    if (cLine == null || ! (cLine.hasOption("f") && cLine.hasOption("c"))) {
       logger.error("Please, provide the necessary parametets (f and c)");
       //formatter.printHelp(widthFormatter, command, header, options, footer, true)
       formatter.printHelp(B_SparseVectorsDistance.class.getSimpleName(),options);
       System.exit(1);
     }
    
    if(!cLine.hasOption("l")) {
      logger.error("Please, provide the language code");
      //formatter.printHelp(widthFormatter, command, header, options, footer, true)
      formatter.printHelp(B_SparseVectorsDistance.class.getSimpleName(),options);
      System.exit(1);
    }
    
    return cLine;
  }
  
  private int getCategoryIdFromFileName(String fileName) {
    int leftBound = fileName.indexOf(".") + 1;
    int rightBound = fileName.indexOf(".", leftBound);
    return Integer.parseInt(fileName.substring(leftBound, rightBound) );
    
  }
  
  private Set<String> getArticleIds () {
    //TODO if we only use this file for the ids, it shouldn't be a constant
    String[] sIds = null;
    try {
      sIds = FileIO.fileToLines(CATEGORY_FILE);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Set<String> iIds = new HashSet<String>();
    for (int i=0; i < sIds.length ; i++) {
      iIds.add(String.format("%s.%s", sIds[i],LANGUAGE));
    }
    return iIds;
  }
  
  
  public static void main(String[] args) {
    CommandLine cLine = getOptions(args);
//    int category = getCategoryIdFromFileName(cLine.getOptionValue("c"));
    B_EsaCategoryCentroidComputer eccc = new B_EsaCategoryCentroidComputer(
        cLine.getOptionValue("c"), cLine.getOptionValue("f"), cLine.getOptionValue("l"));
    eccc.computeDistances(new File(cLine.getOptionValue("f")));
    
    
    
    
    
  }
  
}
