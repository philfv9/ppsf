package algorithmmanager.descriptions;

import java.io.IOException;

import algorithmmanager.DescriptionOfAlgorithm;
import algorithmmanager.DescriptionOfParameter;

/**
 * This class describes the Apriori algorithm parameters. 
 * It is designed to be used by the graphical and command line interface.
 * 
 * @see AlgoApriori
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoSifIdf extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoSifIdf(){
		
	}

	@Override
	public String getName() {
		return "SIF_IDF";
	}

	@Override
	public String getAlgorithmCategory() {
		return "PPDM";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.ikelab.net/ppmf/";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputDatabaseFile, String outputFile) throws IOException {
		double min_sup = getParamAsDouble(parameters[0]);

		// Applying the Apriori algorithm, optimized version
		algorithms.PPDM.sGA2DT.AlgoSGA2DT algorithm = new algorithms.PPDM.sGA2DT.AlgoSGA2DT();
		
		double sen_per = getParamAsDouble(parameters[1]);

		algorithm.runAlgorithm(min_sup, sen_per, inputDatabaseFile, outputFile);
		
		
		algorithm.printStats();
	}
	
	@Override
	public void runPPMFAlgorithm(String[] parameters, String inputDatabaseFile, String inputSensitiveFile, String outputFile) throws IOException {
		double min_sup = getParamAsDouble(parameters[0]);

		// Applying the Apriori algorithm, optimized version
		algorithms.PPDM.SIF_IDF.AlgoSifIdf algorithm = new algorithms.PPDM.SIF_IDF.AlgoSifIdf();
		
		double sen_per = getParamAsDouble(parameters[1]);

		algorithm.runAlgorithm(min_sup, sen_per,inputDatabaseFile, inputSensitiveFile, outputFile);
		
		
		algorithm.printStats();
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
		parameters[0] = new DescriptionOfParameter("Minsup (%)", "(e.g. 0.02 or 2%)", Double.class, false);
		parameters[1] = new DescriptionOfParameter("Sensitive percentage (%)", "(e.g. 0.25 or 25%)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "A man";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[]{"Database of instances","Transaction database", "Simple transaction database"};
	}

	@Override
	public String[] getOutputFileTypes() {
		return new String[]{"Patterns", "Frequent patterns", "Frequent itemsets"};
	}
	
}

