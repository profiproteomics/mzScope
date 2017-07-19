package fr.profi.mzscope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MGFReader {

	private static final Logger logger = LoggerFactory.getLogger(MGFReader.class);

	private int currentLine = 0;
	private List<MSMSSpectrum> spectrumList;

	/**
	 * Reads the MS/MS spectra contained in the specified file
	 * 
	 * @param f
	 *           : the file containing MS/MS spectra in Mascot Generic Format
	 * @return the list of MS/MS spectra.
	 * @throws InvalidMGFFormatException
	 */
	public List<MSMSSpectrum> read(File f) throws InvalidMGFFormatException {
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new InvalidMGFFormatException(e.getMessage());
		}

		InputStreamReader reader = new InputStreamReader(fis);
		return read(reader);
	}

	public List<MSMSSpectrum> read(InputStreamReader reader) throws InvalidMGFFormatException {
            spectrumList = new ArrayList<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(reader);
			String nextLine = nextLine(bufferedReader);
			while (nextLine != null) {
				if (nextLine.startsWith(MGFConstants.START_QUERY)) {
					MSMSSpectrum spectrum = readSpectrum(bufferedReader);
					this.endSpectrum(this, spectrum);
				} else if (nextLine.startsWith(MGFConstants.START_COMMENT)) {
					this.comment(this, nextLine.substring(1));
				}
				nextLine = nextLine(bufferedReader);
			}
		} catch (IOException ioe) {
			throw new InvalidMGFFormatException(ioe.getMessage());
		}
            return spectrumList;
	}

	private String nextLine(BufferedReader reader) throws IOException {
		String nextLine = reader.readLine();
		if (nextLine != null) {
			nextLine = nextLine.trim();
			currentLine++;
			if (nextLine.length() == 0)
				return nextLine(reader);
		}
		return nextLine;
	}

	/**
	 * Read a spectra description from lines, starting at index i. The new
	 * spectra will be added to spectrum list. The index of the end of the
	 * spectra description will be returned.
	 * 
	 * @param spectrum
	 * @param lines
	 * @param i
	 * @return
	 * @throws InvalidMGFFormatException
	 */
	private MSMSSpectrum readSpectrum(BufferedReader reader) throws InvalidMGFFormatException, IOException {

		// Spectra informations
		double parentMass = -1;
		double parentIntensity = 0;
		int parentCharge = 0;
		double parentRetTime = 0;
		String title = null;
		String scans = null;
		List<double[]> allPeaks = new ArrayList<double[]>();

		String nextLine = reader.readLine();
		currentLine++;
		// Go through spectra descrition until END_QUERY found.
		boolean exitWithNoEnd = false;
		while ((nextLine != null) && !nextLine.startsWith(MGFConstants.END_QUERY)) {

			// Tag Values if any
			String value = null;

			// ** Get Tag and Value strings
			String tag = nextLine;
			int separatorIndex = nextLine.indexOf(MGFConstants.VALUE_SEPARATOR);
			if (separatorIndex > 1) {
				tag = nextLine.substring(0, separatorIndex);
				value = nextLine.substring(separatorIndex + 1);
			}

			// Blank line !
			if (tag == null) {
				nextLine = nextLine(reader);
				if (nextLine == null) {
					exitWithNoEnd = true;
					break;
				}
				continue;
			}

			// Test if current line is a TAG/VALUE line or a peak line
			List<String> l = Arrays.asList(MGFConstants.MSMS_QUERY_TAGS);
			if (l.contains(tag)) {
				// logger.debug("TAG = "+tag);
				// ******************************
				// ******* MSMS ion search TAG line

				// ---- PARENT MASS AND INTENSITY TAG
				if (MGFConstants.PARENT_MASS_I.equals(tag)) {
					double[] values = getMassAndIntensity(value);
					parentMass = values[0];
					parentIntensity = values[1];
				} // END PARENT MASS AND INTENSITY TAG

				// ---- PARENT CHARGE TAG
				if (MGFConstants.PARENT_CHARGE.equals(tag)) {
					parentCharge = getCharge(value);
				}// END PARENT CHARGE TAG

				// ---- TITLE TAG
				if (MGFConstants.TITLE.equals(tag)) {
					if (value != null) {
						title = value;
					}
				}// END TITLE TAG

				// ---- RET_TIME TAG
				if (MGFConstants.RETENTION_TIME.equals(tag)) {
					parentRetTime = getRetentionTime(value);
				}// END RET_TIME TAG

				// ---- SCANS TAG
				if (MGFConstants.SCANS.equals(tag)) {
					if (value != null) {
						scans = value;
					}
				}// END RET_TIME TAG

				// --- OTHERS TAGS are not read !

			} else {
				// ******************************
				// ******* MSMS peaks value line
				double[] peak = getPeakValue(nextLine);
				allPeaks.add(peak);
			}

			// Go to nextLine
			nextLine = nextLine(reader);
			if (nextLine == null) {
				exitWithNoEnd = true;
				break;
			}

		}// END Go through all lines

		// Spectra description was not complete !
		if (exitWithNoEnd) {
			Object[] args = { currentLine };
			String msg = MessageFormat.format("No END ION found for query [line:{0}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		// Mandatory Peptide Mass value was not specified
		if (parentMass == -1) {
			Object[] args = { currentLine };
			String msg = MessageFormat.format("No mass specified for query [line:{0}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		// Create new Spectra
		// logger.debug("Create spectrum ");
		MSMSSpectrum spectra = new MSMSSpectrum(parentMass, parentIntensity, parentCharge, parentRetTime);
		// logger.debug(" add Annotation title "+title);
		if (title != null) {
			spectra.setAnnotation(MGFConstants.ANNOTATION_TITLE, title);
		}
		if (scans != null)
			spectra.setAnnotation(MGFConstants.SCANS, scans);

		// logger.debug("Add peaks, nbr = "+allPeaks.size());
		for (int nbrPeaks = 0; nbrPeaks < allPeaks.size(); nbrPeaks++) {
			double[] aPeak = allPeaks.get(nbrPeaks);
			spectra.addPeak(aPeak[0], aPeak[1]);
		}

		return spectra;
	}

	private double[] getPeakValue(String nextLine) throws InvalidMGFFormatException {
		double[] values = new double[2];
		StringTokenizer tokenizer = new StringTokenizer(nextLine);
		if (tokenizer.countTokens() != 2) {
			Object[] args = { currentLine };
			String msg = MessageFormat.format("Mass and intensity values must be specified for each peak or end of spectrum not detected [line:{0}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		try {
			values[0] = Double.parseDouble(tokenizer.nextToken());
			values[1] = Double.parseDouble(tokenizer.nextToken());
		} catch (NumberFormatException nfe) {
			Object[] args = { currentLine };
			String msg = MessageFormat.format("Mass and intensity values must be specified for each peak or end of spectrum not detected [line:{0}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		return values;
	}

	private double getRetentionTime(String value) {
		double retTime = 0;
		if (value != null) {
			try {
				retTime = Double.parseDouble(value);
			} catch (NumberFormatException nfe) {

				// try a[[-b][,c[-d]]] format
				List<String> values = new ArrayList<String>();
				List<Double> result = new ArrayList<Double>();

				int commaIndex = value.indexOf(",");
				if (commaIndex != -1) {
					values.add(value.substring(0, commaIndex));
					values.add(value.substring(commaIndex + 1, value.length()));
				} else
					values.add(value);

				for (int i = 0; i < values.size(); i++) {
					String nextValue = (String) values.get(i);
					int dashIndex = nextValue.indexOf("-");
					if (dashIndex != -1) { // Split value
						try {
							String firstDbl = nextValue.substring(0, dashIndex);
							double first = Double.parseDouble(firstDbl);
							String secDbl = nextValue.substring(dashIndex + 1, nextValue.length());
							double sec = Double.parseDouble(secDbl);

							double avg = (first + sec) / 2.0;
							result.add(avg);
						} catch (NumberFormatException nfe1) {
							// Try with next value !
						}
					} else { // Only one value
						try {
							double dbl = Double.parseDouble(nextValue);
							result.add(dbl);
						} catch (NumberFormatException nfe1) {
							// Try with next value !
						}
					}
				}

				if (!result.isEmpty()) {
					int nbrDbl = 0;
					for (; nbrDbl < result.size(); nbrDbl++) {
						retTime = retTime + result.get(nbrDbl);
					}

					retTime = retTime / nbrDbl;

					MessageFormat numberFormat = new MessageFormat("{0,number,0.0000} ", Locale.ENGLISH);
					Object[] args = { retTime };
					String retTimeStr = numberFormat.format(args);
					retTime = Double.parseDouble(retTimeStr);

				} else {

					Object[] args = { "retention time", currentLine };
					String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", args);
					logger.warn(msg);
				}
			}
		}
		return retTime;
	}

	private double[] getMassAndIntensity(String value) throws InvalidMGFFormatException {
		double[] ret = new double[2];

		if (value == null) {
			Object[] args = { currentLine };
			String msg = MessageFormat.format("No mass specified for query [line:{0}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		StringTokenizer tokenizer = new StringTokenizer(value);
		String firstVal = null;
		if (tokenizer.hasMoreTokens()) {
			firstVal = tokenizer.nextToken();
		}

		try {
			ret[0] = Double.parseDouble(firstVal);
		} catch (NumberFormatException nfe) {
			Object[] args = { "mass", currentLine };
			String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", args);
			throw new InvalidMGFFormatException(msg);
		}

		if (tokenizer.hasMoreTokens()) {
			String iVal = tokenizer.nextToken();
			try {
				ret[1] = Double.parseDouble(iVal);
			} catch (NumberFormatException nfe) {
				Object[] args = { "intensity", currentLine };
				String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", args);
				logger.warn(msg);
			}
		}

		return ret;
	}

	private int getCharge(String value) {
		int charge = 0;
		if (value == null)
			return charge;

		if (value.indexOf("+") != -1)
			value = value.substring(0, value.indexOf("+"));
		// VD TODO : Case or 'Charge = 2+ and 3+' !!!!
		try {
			charge = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			Object[] args = { "charge", currentLine };
			String msg = MessageFormat.format("Invalid {0} specified for query [line:{1}]", args);
			logger.warn(msg);
		}
		return charge;
	}

	public void endSpectrum(Object source, MSMSSpectrum spectrum) {
		if (spectrumList != null)
			spectrumList.add(spectrum);
	}
	
	public void comment(Object source, String text) {	}
	
}
