package winlab.SensorGUI;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author malathidharmalingam
 * @version 1.0.0
 */
public class WriteConfigFile {
 
	public void writetofile(ArrayList<ConfigItems> configfileinfo,
			String filename) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("sensor_configuration");
			doc.appendChild(rootElement);

			for (ConfigItems y : configfileinfo) {
				
				Element staff = doc.createElement("allsensors");
				rootElement.appendChild(staff);

				Element mic_sampling_rate = doc.createElement("mic_sampling_rate");
				mic_sampling_rate.appendChild(doc.createTextNode(y.getMicsampling().toString()));
				staff.appendChild(mic_sampling_rate);

				Element mic_channel_input= doc.createElement("mic_channel_input");
				mic_channel_input.appendChild(doc.createTextNode(y.getMicchannel().toString()));
				staff.appendChild(mic_channel_input);
				
				Element mic_channel_audio= doc.createElement("mic_channel_audio");
				mic_channel_audio.appendChild(doc.createTextNode(y.getMicencode().toString()));
				staff.appendChild(mic_channel_audio);

				Element gps_provider= doc.createElement("gps_provider");
				gps_provider.appendChild(doc.createTextNode(y.getProvider().toString()));
				staff.appendChild(gps_provider);
		 
				Element gps_loggingrate= doc.createElement("gps_loggingrate");
				gps_loggingrate.appendChild(doc.createTextNode(y.getLograte().toString()));
				staff.appendChild(gps_loggingrate);
			 
				Element other_lograte= doc.createElement("other_lograte");
				other_lograte.appendChild(doc.createTextNode(y.getOtherlograte().toString()));
				staff.appendChild(other_lograte);
 
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

}