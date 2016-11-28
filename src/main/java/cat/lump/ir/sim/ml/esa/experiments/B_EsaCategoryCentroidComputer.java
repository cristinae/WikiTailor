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
 * This class takes a language, a collection of ESA vectors files, a list with 
 * the articles belonging to a particular domain, previously extracted by a 
 * given model. After computing the centroid of the domain-retrieved articles, 
 * distances against each article and the centroid are computed. 
 * 
 * INPUT
 * <ul>
 * <li /> language in ISO 639-1 code
 * <li /> set of ESA vector files
 * <li /> file with the files belonging to the categories
 * </ul>
 * 
 * OUTPUT
 * - Distance between each of the article's representations and the centroid 
 * of the domain
 * 
 * @author albarron
 * @since October, 2016
 *
 */
public class B_EsaCategoryCentroidComputer {

  /** File with the identifiers of the articles belonging to a given domain. */
  private final File DOMAIN_FILE;
  
  /** Identifier of the present domain, as extracted from the domain file. */
  private final int DOMAIN_ID;

  /** 
   * Cardinality of the ESA representations (subject to be variable in the 
   * future)
   */
  private final int ESA_CARDINALITY = 12539;

  
  /**
   * Keeps the ISO 639-1 code of the language; necessary because the 
   * identifiers of the vectors include the language code.   
   */
  private final String LANGUAGE;
  
  /**
   * Keeps track of the serialised ESA representation files that contain at 
   * least one article id from the domain 
   */     
  private static final LumpLogger LOGGER = 
      new LumpLogger (B_EsaCategoryCentroidComputer.class.getSimpleName());
 
  /**
   * Sets up the class with the file with the domain ids, loads the files with 
   * the serialised vectors and sets the language. 
   * 
   * @param domainFile 
   *              file with the IDs of the articles in the domain
   * @param esaVectorsPath  
   *              path to the serialised obj vector files. 
   * @param language
   *              ISO 639-1 language code
   */
  public B_EsaCategoryCentroidComputer(
                String domainFile, String esaVectorsPath, String language) {
    
    DOMAIN_FILE = getDomainFile(domainFile);
    DOMAIN_ID = getDomainIdFromFileName(domainFile);
    LANGUAGE = getLanguage(language);
  }
  
  /**
   * Computes the distances between all the vectors belonging to the given 
   * domain and the domain's centroid. All the obj. files in the pathFolder 
   * will be considered to load the necessary vectors.
   *   
   * @param pathToFolder
   *              Path to the folder with the serialised vector files.
   */
  public void computeDistances(File pathToFolder) {
  
    Set<String> articlesInDomain = getArticleIds();
    List<String> vectorFiles =  FileIO.getFilesRecursively(pathToFolder, ".obj");
    LOGGER.info(                                              
        String.format("Files considered from %s: %d ",        
            pathToFolder.toString(), vectorFiles.size())      
    );
    
    // Dictionary with the objects we need to load for the distances. Passed 
    // to method computeCentroid to be filled while computing the centroid
    Map<String, Set<String>> filesWithDomainArticles = new HashMap<String, Set<String>>();
    float[] centroid = computeCentroid(vectorFiles, articlesInDomain, filesWithDomainArticles);

    double centroidMagnitude = computeMagnitude(centroid);
    double vectorMagnitud;
//    double sum;
    double angDist;
    // Iterate over the files with the relevant articles to compute distances
    for (Entry<String, Set<String>> entry : filesWithDomainArticles.entrySet()) {
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
   * Compute the centroid of a set of vectors in the given category. As a side 
   * effect, it keeps track of the serialised files with at least one domain
   * article representation.
   * 
   * @param vectorFiles 
   *            list with all the necessary vector files
   * @param articlesInDomain  
   *            set of articles belonging to a domain
   * @param filesWithDomainArticles
   *            this is supossed to be an empty HashMap<String, Set<String>>. 
   *            The serialised files with at least one domain article 
   *            representation (and the corresponding IDs) are kept here for 
   *            further use in the calling method. 
   * @return
   *            Vector with the centroid of the domain.    
   */
  private float[] 
  computeCentroid(List<String> vectorFiles, Set<String> articlesInDomain,
      Map<String, Set<String>> filesWithDomainArticles) {

    float[] centroid= new float[ESA_CARDINALITY];
    int counter = 0;
    VectorStorageAbstract vss;
    
    // Iterate over all the vector files
    for (String vectorFile : vectorFiles) {
      vss = (VectorStorageAbstract) FileIO.readObject(new File(vectorFile));
      
      // Load all the current ids and retain only the relevant ones for this domain
      Set<String> currentIds = new HashSet<String>();
      currentIds.addAll(vss.getIds());
      currentIds.retainAll(articlesInDomain);
      
      if (currentIds.isEmpty()) {
        // If none of the IDs here is relevant, discard it and continue
        LOGGER.info(String.format("No domain ID in file %s; skipping", vectorFile));
        continue;
      } else {
        LOGGER.info(String.format("%d IDs loaded from file %s", currentIds.size(), vectorFile));
      }
      
      // Store the pointer to the relevant IDs from the current file for the distances step 
      filesWithDomainArticles.put(vectorFile, currentIds);
      // Accumulate the sum for all the relevant vectors in the centroid
      counter += currentIds.size();
      for (String id : currentIds) {
//        counter ++;
        for (Map.Entry<Integer, Float> dimensionValuePair : vss.getValues(id).entrySet()) {
          centroid[dimensionValuePair.getKey()] += dimensionValuePair.getValue();
        }
      }
    }
    
    CHK.CHECK(counter!=0, 
        String.format("No ID belonging to domain %s was found in the vectors", DOMAIN_ID)
    );
    
    LOGGER.info(
        String.format(
            "Number of articles found for domain %d: %d (of %d included in the input)", 
            DOMAIN_ID, counter, articlesInDomain.size())
    );

    //Divide by the number of instances to compute the centroid
    for (int i=0; i < centroid.length ; i++) {
      centroid[i] /= counter;
    }
    return centroid;
  }

  /**
   * Compute the magnitude of the given vector. Crashes if the vector is empty.
   * 
   * @param sparseVector
   *          map of values in each dimension.
   * @return
   *          magnitude of the vector.
   */
  private double computeMagnitude(Map<Integer, Float> sparseVector) {
    CHK.CHECK(! sparseVector.isEmpty(), "The vector should not be empty");
    double magnitude = 0;
    for (float value : sparseVector.values()) {
      magnitude += value * value;
    }
    magnitude = Math.sqrt(magnitude);
    return magnitude;
  }
  
  /**
   * Compute the magnitude of the given vector. Crashes if the vector is null.
   * 
   * @param vector
   *           
   * @return
   *         magnitude of the vector. 
   */   
  private double computeMagnitude(float[] vector) {
    CHK.CHECK(vector != null, "The vector should not be null");
    double magnitude = 0;
    for (int i = 0; i < vector.length ; i++) {
      magnitude += vector[i] * vector[i];
    }
    magnitude = Math.sqrt(magnitude);
    return magnitude;
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
  
  /**
   * Loads the ids from the domain file and attach them the languagce code. 
   * @return
   *        set of ids attached to the language code (e.g., el.5342)
   */
  private Set<String> getArticleIds () {
    //TODO if we only use DOMAIN_FILE for the ids in this method, it shouldn't be a constant
    String[] sIds = null;
    try {
      sIds = FileIO.fileToLines(DOMAIN_FILE);
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
  
  /**
   * Checks that the domain file exists and loads it as a file.
   * @param domainFile
   *                full path to the domain's file
   * @return
   *                File 
   */
  private File getDomainFile(String domainFile) {
    File f  = new File(domainFile);
    F.CAN_READ(f, "I cannot read the domain file");
    LOGGER.info(String.format("Domain file %s loaded", domainFile));
    return f;
  }
  
  /**
   * Extracts the id of the current domain from the domain file. The expected 
   * format for the file, regardles of any path, is [lan].[domain_id].*
   *  
   * @param fileName
   *            file name
   * @return
   *            domain id
   */
  private int getDomainIdFromFileName(String fileName) {
    if (fileName.contains(File.separator)) {
      fileName = fileName.substring(fileName.lastIndexOf(File.separator));
    }
    int id = -1;
    try {
      int leftBound = fileName.indexOf(".") + 1;
      int rightBound = fileName.indexOf(".", leftBound);
      id = Integer.parseInt(fileName.substring(leftBound, rightBound) );
    } catch (Exception e) {
      LOGGER.errorEnd(
          "The domain file name does not have the expected format: [lan].[domain_id].*"
      );
    }
    LOGGER.info(String.format("Domain %d set", id));
    
    return id;
  }
  
  private String getLanguage(String language) {
    CHK.CHECK(language.length() == 2, 
        "The language does not stick to the ISO 639-1 standard");
    LOGGER.info(String.format("Language '%s' set", language));
    
    return language;
  }
  

  private static CommandLine getOptions(String[] args) {
    
    Options options = new Options();
    CommandLineParser parser = new BasicParser();
    HelpFormatter formatter = new HelpFormatter();
    options.addOption("d", "inputDir", true, 
        "Path to the obj ESA vectors folder");
    options.addOption("c", "catfiles", true, 
        "File with the categorie's IDs");
    options.addOption("l", "language", true,
        "Two-letters code for the language");
    CommandLine cLine = null;
    try {     
      cLine = parser.parse( options, args );
    } catch( ParseException exp ) {
      LOGGER.error( "Unexpected exception:" + exp.getMessage() );     
    } 
    
    if (cLine == null || ! (cLine.hasOption("d") && cLine.hasOption("c"))) {
       LOGGER.error("Please, provide the necessary parametets (incl. d and c)");
       //formatter.printHelp(widthFormatter, command, header, options, footer, true)
       formatter.printHelp(B_SparseVectorsDistance.class.getSimpleName(),options);
       System.exit(1);
     }
    
    if(!cLine.hasOption("l")) {
      LOGGER.error("Please, provide the language code (-l)");
      //formatter.printHelp(widthFormatter, command, header, options, footer, true)
      formatter.printHelp(B_SparseVectorsDistance.class.getSimpleName(),options);
      System.exit(1);
    }
    return cLine;
  }
  
  /**
   * Receives the parameters for path to the serialised ESA files, domain 
   * articles file, and language and launches the necessary methods to compute 
   * the centroid of the domain and the distances between the domain articles 
   * and such centroid.  
   * 
   * @param args
   */
  public static void main(String[] args) {
    CommandLine cLine = getOptions(args);
    B_EsaCategoryCentroidComputer eccc = new B_EsaCategoryCentroidComputer(
        cLine.getOptionValue("c"), cLine.getOptionValue("d"), cLine.getOptionValue("l"));
    
    eccc.computeDistances(new File(cLine.getOptionValue("d")));
  }
  
}
