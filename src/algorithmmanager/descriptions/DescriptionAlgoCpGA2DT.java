package algorithmmanager.descriptions;

import java.io.IOException;

import algorithmmanager.DescriptionOfAlgorithm;
import algorithmmanager.DescriptionOfParameter;
//import ca.pfv.spmf.algorithms.frequentpatterns.apriori.AlgoApriori;
/* This file is copyright (c) 2008-2016 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * This class describes the cpGA2DT algorithm parameters. 
 * It is designed to be used by the graphical interface.
 */
public class DescriptionAlgoCpGA2DT extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoCpGA2DT(){
		
	}

	@Override
	public String getName() {
		return "cpGA2DT";
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
		algorithms.PPDM.cpGA2DT.AlgoCpGA2DT algorithm = new algorithms.PPDM.cpGA2DT.AlgoCpGA2DT();
		
		double sen_per = getParamAsDouble(parameters[1]);
		double[] w = new double[3];
		w[0] = getParamAsDouble(parameters[2]);
		w[1] = getParamAsDouble(parameters[3]);
		w[2] = getParamAsDouble(parameters[4]);

		algorithm.runPPMFAlgorithm(min_sup, sen_per, w, inputDatabaseFile, inputSensitiveFile, outputFile);
		
		
		algorithm.printStats();
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[5];
		parameters[0] = new DescriptionOfParameter("Minsup (%)", "(e.g. 0.9 or 90%)", Double.class, false);
		parameters[1] = new DescriptionOfParameter("Sensitive percentage (%)", "(e.g. 0.01 or 1%)", Double.class, false);
		parameters[2] = new DescriptionOfParameter("w1 (%)", "(e.g. 0.9 or 90%)", Double.class, false);
		parameters[3] = new DescriptionOfParameter("w2 (%)", "(e.g. 0.05 or 5%)", Double.class, false);
		parameters[4] = new DescriptionOfParameter("w3 (%)", "(e.g. 0.05 or 5%)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
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


