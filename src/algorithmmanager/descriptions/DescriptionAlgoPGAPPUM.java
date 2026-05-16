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
public class DescriptionAlgoPGAPPUM extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoPGAPPUM(){
		
	}

	@Override
	public String getName() {
		return "pGAPPUM";
	}

	@Override
	public String getAlgorithmCategory() {
		return "PPUM";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.ikelab.net/ppmf/";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputDatabaseFile, String outputFile) throws IOException {

	}
	
	@Override
	public void runPPMFAlgorithm(String[] parameters, String inputDatabaseFile, String inputSensitiveFile, String outputFile) throws IOException {
		double min_sup = getParamAsDouble(parameters[0]);

		// Applying the Apriori algorithm, optimized version
		algorithms.PPUM.pGAPPUM.AlgoPGAPPUM algorithm = new algorithms.PPUM.pGAPPUM.AlgoPGAPPUM();
		
		double sen_per = getParamAsDouble(parameters[1]);
		double[] w = new double[3];
		w[0] = getParamAsDouble(parameters[2]);
		w[1] = getParamAsDouble(parameters[3]);
		w[2] = getParamAsDouble(parameters[4]);

		algorithm.runAlgorithm(min_sup, sen_per, w, inputDatabaseFile, inputSensitiveFile, outputFile);
		
		
		algorithm.printStats();
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[5];
		parameters[0] = new DescriptionOfParameter("Upper_min (%)", "(e.g. 0.265 or 26.5%)", Double.class, false);
		parameters[1] = new DescriptionOfParameter("Sensitive percentage (%)", "(e.g. 0.015 or 1.5%)", Double.class, false);
		parameters[2] = new DescriptionOfParameter("w1 (%)", "(e.g. 0.9 or 90%)", Double.class, false);
		parameters[3] = new DescriptionOfParameter("w2 (%)", "(e.g. 0.05 or 5%)", Double.class, false);
		parameters[4] = new DescriptionOfParameter("w3 (%)", "(e.g. 0.05 or 5%)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Someone";
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

