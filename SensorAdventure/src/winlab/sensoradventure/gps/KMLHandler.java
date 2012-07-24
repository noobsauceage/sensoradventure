/**
 * 
 */
package winlab.sensoradventure.gps;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
 
/**
 * @author malathidharmalingam
 *
 */
public class KMLHandler extends DefaultHandler {
	private boolean inGeometryCollection = false;
	private boolean inCoordinates = false;
	private String pathCoordinates;

	public String getPathCoordinates() { return pathCoordinates; }

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(inGeometryCollection && inCoordinates)
			pathCoordinates = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(localName.equals("GeometryCollection")) inGeometryCollection = false;
		else if (localName.equals("coordinates")) inCoordinates = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(localName.equals("GeometryCollection")) inGeometryCollection = true;
		else if (localName.equals("coordinates")) inCoordinates = true;
	}
}