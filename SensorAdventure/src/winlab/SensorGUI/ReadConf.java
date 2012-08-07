package winlab.SensorGUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.res.Resources;
import android.os.Environment;

/**
 * @author malathidharmalingam
 * Version 1.0
 * This is a program that reads the configuration file created by
 * the AdvancedSettingsGUI.
 */
public class ReadConf  {
	private static final String mic_sample = "mic_sampling_rate";
	private static final String mic_channel = "mic_channel_input";
	private static final String mic_audio = "mic_channel_audio";
	private static final String gps_provider= "gps_provider";
	private static final String gps_lograte = "gps_loggingrate";
	private static final String other_lograte = "other_lograte";
	private String mic_sample_value;
	private String mic_channel_value;
	private String mic_audio_value;
	private String gps_provider_value;
	private String gps_lograte_value;
	private String other_lograte_value;

	public void parseXML() throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		// get a reference to the file.
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/SensorConfig/Config.txt");

		// create an input stream to be read by the stream reader.
		FileInputStream fis = new FileInputStream(file);

		// set the input for the parser using an InputStreamReader
		xpp.setInput(new InputStreamReader(fis));

		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {

			// set flags for main tags.
			if (eventType == XmlPullParser.START_DOCUMENT) {
				// TODO only parse if the timestamps don't match.
				System.out.println("Start document");

			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				System.out.println("End document");
			} else if (eventType == XmlPullParser.START_TAG) {
				String nodeName = xpp.getName();
				if (nodeName.contentEquals(mic_sample)) {
					setmicsample(xpp.nextText());
				}				
				if (nodeName.contentEquals(mic_channel)) {
					setmicchannel(xpp.nextText());
				}
				if (nodeName.contentEquals(mic_audio)) {
					setmicaudio(xpp.nextText());
				}
				if (nodeName.contentEquals(gps_provider)) {
					setgpsprov(xpp.nextText());
				}
				if (nodeName.contentEquals(gps_lograte)) {
					setgpslog(xpp.nextText());
				}
				if (nodeName.contentEquals(other_lograte)) {
					setotherlog(xpp.nextText());
				}

			} else if (eventType == XmlPullParser.END_TAG) {
				// System.out.println("End tag " + nodeName);
			} else if (eventType == XmlPullParser.TEXT) {
			}
			eventType = xpp.next();
		}

	}


	public void setmicsample(String micsamp) {
		this.mic_sample_value = micsamp;
	}

	public void setmicchannel(String micchan) {
		this.mic_channel_value = micchan;
	}

	public void setmicaudio(String micsaudio) {
		this.mic_audio_value = micsaudio;
	}

	public void setgpsprov(String gpsprov) {
		this.gps_provider_value = gpsprov;
	}

	public void setgpslog(String gpslog) {
		this.gps_lograte_value = gpslog;
	}

	public void setotherlog(String otherlog) {
		this.other_lograte_value = otherlog;
	}

	public String getmicsample() {
		return mic_sample_value;
	}

	public String getmicchannel() {
		return mic_channel_value;
	}

	public String getmicaudio() {
		return mic_audio_value;
	}

	public String getgpsprov() {
		return gps_provider_value;
	}

	public String getgpslog() {
		return gps_lograte_value;
	}

	public String getotherlog() {
		return other_lograte_value;
	}

}