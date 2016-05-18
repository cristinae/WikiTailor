package cat.lump.aq.textextraction.wikipedia.categories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.aq.basics.log.LumpLogger;
import cat.lump.aq.textextraction.wikipedia.cli.WikipediaCliCategoryDepth;


/**
 * Class that automatises the process of selecting how deep within the category tree
 * one must go to extract articles from a given domain. The selection is done 
 * according to the number of category titles that contain at least a word of the
 * domain keywords
 * <br/> 
 * @author cristina
 *
 */
public class CategoryDepth {

	private final File input;
	private final double percentage;
	private final int maxDepth;
	private final int minDepth;

	private static LumpLogger logger = 
			new LumpLogger (CategoryDepth.class.getSimpleName());

	
	public CategoryDepth(File input, double percentage, int minDepth, int maxDepth){
		this.input = input;
		this.percentage = percentage;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
	}

	
	/**
	 * Main method. It can be called as a tool to select the adequate depth within 
	 * the category tree for every domain (category)
	 * <br/>
	 * @param args
	 */
	public static void main(String[] args) {
		WikipediaCliCategoryDepth cli = new WikipediaCliCategoryDepth();
		
		cli.parseArguments(args);
	    File input = cli.getStatsFile();
	    double percentage = cli.getPercentage();
		int minDepth = cli.getminDepth();
		int maxDepth = cli.getmaxDepth();
		
		CategoryDepth cd = new CategoryDepth(input, percentage, minDepth, maxDepth);		
		int depth = cd.searchDepthSplines(input, percentage);
				
		logger.info("Percentage " + percentage + " is found at depth " + depth);

	}

	
	/**
	 * Looks for the depth that corresponds to the percentage of categories with keywords
	 * required. 
	 * Method: linear interpolation.
	 * <br/>
	 * @param in
	 *         Input file
	 * @param percentage
	 *         Required percentage of categories with keywords 
	 * @return bestDepth
	 *          
	 */
	private int searchDepthLinear(File in, double percentage) {
	    
		Scanner s = null;
		try {
			s = new Scanner(input).useLocale(Locale.ENGLISH);
			//s.useDelimiter(System.getProperty("line.separator")); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.errorEnd("The file " +in+ " is not found");
		}
	    
	    int level = 0;
	    double percent = 1;
	    double prevPercent = 1;
	    int bestDepth = 0;
	    while (s.hasNext()) {
	    	level = s.nextInt();
	    	percent = s.nextDouble();
	    	if (percent <= percentage) {
	    		break;
	    	}	    	
	    	prevPercent = percent;
	    	s.nextLine();
	    }
	    // Linear interpolation, looks for the closest level
	    double mean = (prevPercent+percent)/2;
	    if (percent >= prevPercent && percentage < mean) {
    		bestDepth = level-1;	    	
	    } else if (percent >= prevPercent && percentage >= mean) {
    		bestDepth = level;
	    } else if (percent < prevPercent && percentage >= mean) {
    		bestDepth = level-1;
	    } else if (percent < prevPercent && percentage < mean) {
    		bestDepth = level;
	    }
	    
	    checkbestDepth(bestDepth);
	    
		return bestDepth;
		
	}

	
	
	/**
	 * Looks for the depth that corresponds to the percentage of categories with keywords
	 * required. 
	 * Method: interpolation by splines {@link org.apache.commons.math3.analysis.interpolation.SplineInterpolator}
	 * and afterwards inversion by exploring the function.
     * This seems adequate for this problem where eventually we need to round for
     * discrete (integer) values. For a more precise method implement an algorithm 
     * for inverse interpolation.
     * <br/>
	 * @param in
	 *         Input file
	 * @param percentage
	 *         Required percentage of categories with keywords 
	 * @return bestDepth
	 *          
	 */
	private int searchDepthSplines(File in, double percentage) {
	    
		Scanner s = null;
		try {
			s = new Scanner(input).useLocale(Locale.ENGLISH);
			//s.useDelimiter(System.getProperty("line.separator")); 			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.errorEnd("The file " +in+ " is not found");
		}
		
		int numLines = 0;
		int bestDepth = 0; 
		try {
			numLines = FileIO.fileCountLines(in);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("Error in counting lines in " +in+ " (@searchDepthSplines)");
		}

	    // With few points we cannot interpolate
	    if (numLines <= 2){
			logger.warn("No need of interpolation, keeping the last value");	  
			return bestDepth=numLines;
	    }
	    
	    double[] xLevel = new double[numLines];
	    double[] yPercs = new double[numLines];
	    int i = 0;
	    while (s.hasNext()) {
	    	xLevel[i] = (double)s.nextInt();
	    	yPercs[i] = s.nextDouble();
	    	s.nextLine();
	    	i++;
	    }
	    SplineInterpolator si = new SplineInterpolator();  
	    UnivariateFunction interpolatedFunc = si.interpolate(xLevel, yPercs);

	    // We need x(y), and not y(x) given by the splines fit
	    double interpolatedDepth = 
	    		exploreRoot(interpolatedFunc, percentage, xLevel[0], xLevel[i-1]);
	    bestDepth = (int) Math.floor(interpolatedDepth+0.5);
	    
	    // To avoid errors later
	    if (bestDepth == maxDepth) {
	    	bestDepth = maxDepth-1;
	    }
	    // Anyway warn the user if the value is too high/low
	    checkbestDepth(bestDepth);
	    
		return bestDepth;
		
	}

    /**
     * Given a univariate {@code func} and a Y value, the method explores the 
     * x-y space to find the first value of X with a func(X) that lies within the 
     * (Y-0.01,Y+0.01) interval. 
     * This seems adequate for this problem where at the end we need to round for
     * discrete (integer) values. For a more precise method implement an algorithm 
     * for finding roots.
     * In order to obtain a significant amount of corpus a depth>3 is demanded
     * <br/>
     * @param func
     * @param percentage
     * @param min
     * @param max
     * @return pointX
     * 			double with the X value that fulfills the demand
     */
	private double exploreRoot(UnivariateFunction func, double y, 
			double min, double max) {

		double numIntervals = 10000;
		double intervalX = (max-min)/numIntervals; 
		double uncertantyY = 0.01;		
		double pointX = 0;
		// We need to know the maximum of the function in case our threshold is above. In this case, the output
		// should be this maximum
		double maxFuncX = pointX + min;
		double maxFuncY = func.value(maxFuncX);
		
		for (int j=0; j<numIntervals-1; j++) {  
			pointX = min+intervalX*j;
			if(pointX > maxDepth) break;      // This shouldn't happen
			double interpolatedY = func.value(pointX);
			if (interpolatedY >= maxFuncY){
			    maxFuncX = pointX;
				maxFuncY = interpolatedY;				
			}
			//logger.warn("X: " + pointX + "   Y: " + interpolatedY);
			if ( interpolatedY <= (y+uncertantyY) 
				   &&  interpolatedY >= (y-uncertantyY)
				   &&  pointX > minDepth){    // This is and adhoc constraint in order not to obtain too few levels
				break;
			}
		}

		if (maxFuncY < y) {
			pointX = maxFuncX;
		} 
		return pointX;
	}


	/**
	 * Warnings for non-standard results
     * <br/>
	 * @param bestDepth
	 */
	private void checkbestDepth(int bestDepth) {
		if (bestDepth == 0) {
			logger.errorEnd("No value has been found. Look at " + input);
		} else if (bestDepth < 3) {
			logger.warn("Depth seems too small, check file "+ input+ " manually");
		} else if (bestDepth > 15) {
			logger.warn("Depth seems too large, check file "+ input+ " manually");			
		}
	}

	
	/**
	 * Getters
	 * @return
	 */

	public int getDepthLinear() {
		return searchDepthLinear(input, percentage);
	}

	public int getDepthSplines() {
		return searchDepthSplines(input, percentage);
	}

}
