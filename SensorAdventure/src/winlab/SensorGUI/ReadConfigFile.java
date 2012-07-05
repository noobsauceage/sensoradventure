/**
 * 
 */
package winlab.SensorGUI;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author malathidharmalingam
 * @version 1.0.0
 */
public class ReadConfigFile { 
 
	ConfigItems configitems;
	static ArrayList<ConfigItems> readintomemory = 
		  new ArrayList<ConfigItems>();
 
	private boolean ValidateFile(String filename) {
		boolean exists = (new File(filename)).exists();
		return exists;
	}
 
	private void parseFile(String filename) {
		try {
 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {
				private boolean bmic_sampling_rate = false;
				private boolean bmic_channel_input = false;
				private boolean bmic_channel_audio = false;
				private boolean bgps_provider = false;
				private boolean bgps_loggingrate= false;
				private boolean bother_lograte = false;

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang
				 * .String, java.lang.String, java.lang.String,
				 * org.xml.sax.Attributes)
				 */
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
 
					if (qName.equalsIgnoreCase("mic_sampling_rate")) {
						bmic_sampling_rate = true;
					}

					if (qName.equalsIgnoreCase("mic_channel_input")) {
						bmic_channel_input = true;
					}

					if (qName.equalsIgnoreCase("mic_channel_audio")) {
						bmic_channel_audio = true;
					}

					if (qName.equalsIgnoreCase("gps_provider")) {
						bgps_provider = true;
					}

					if (qName.equalsIgnoreCase("gps_loggingrate")) {
						bgps_loggingrate = true;
					}

					if (qName.equalsIgnoreCase("other_lograte")) {
						bother_lograte = true;
					}

			 
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String
				 * , java.lang.String, java.lang.String)
				 */
				public void endElement(String uri, String localName,
						String qName) throws SAXException {

				}

				public void characters(char ch[], int start, int length)
						throws SAXException {
			 

					if (bmic_sampling_rate) {
						String micsampling = new String(ch, start, length);
						configitems.setMicsampling(micsampling);
						bmic_sampling_rate = false;
					}

					if (bmic_channel_input) {
						String micchannel = new String(ch, start, length);
						configitems.setMicchannel(micchannel);
						bmic_channel_input= false;
					}

					if (bmic_channel_audio) {
						String micchannel = new String(ch, start, length);
						configitems.setMicchannel(micchannel);
						bmic_channel_audio = false;
					}

					if (bgps_provider) {
						String provider = new String(ch, start, length);
						configitems.setProvider(provider);
						bgps_provider = false;
					}

					if (bgps_loggingrate) {
						String lograte = new String(ch, start, length);
						configitems.setLograte(lograte);
						bgps_loggingrate = false;
					}

					if (bother_lograte) {
						String otherlograte = new String(ch, start, length);
						configitems.setOtherlograte(otherlograte);
						bother_lograte= false;
					}
				}

			};
			saxParser.parse(filename, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}
 
}