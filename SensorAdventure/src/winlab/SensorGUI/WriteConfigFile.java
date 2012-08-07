package winlab.SensorGUI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author malathidharmalingam
 * Version 1.0
 * This program creates the Configuration file that is used in
 * AdvancedSettingsGUI.
 */
public class  WriteConfigFile  {	
	private String mPreferredNetworkLabels =  "GPS, NETWORK" ;
	private String mloggingrate = "1, 5, 10, 30, 60";
	private String micloggingrate =  "44.1 , 22.05, 16, 11.025" ;
	private String micchannelrate = "MONO, STEREO";
	private String micchannelencoding = "16, 8";
	private String othersamplingrates1 = "1, 5, 10, 30, 60";
	private void initFileBuf(StringBuffer fileBuf) {
		fileBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		fileBuf.append("  <sensor_configuration>\n");
		fileBuf.append("    <sensors>\n");
		fileBuf.append("      <mic_sampling_rate>"+micloggingrate+"</mic_sampling_rate>\n");
		fileBuf.append("      <mic_channel_input>"+micchannelrate+"</mic_channel_input>\n");
		fileBuf.append("      <mic_channel_audio>"+micchannelencoding+"</mic_channel_audio>\n");
		fileBuf.append("      <gps_provider>"+mPreferredNetworkLabels+"</gps_provider>\n");
		fileBuf.append("      <gps_loggingrate>"+mloggingrate+"</gps_loggingrate>\n");
		fileBuf.append("      <other_lograte>"+ othersamplingrates1+"</other_lograte>\n");
	}

	private void closeFileBuf(StringBuffer fileBuf, String beginTimestamp, String endTimestamp) {
		fileBuf.append("    </sensors>\n");
		fileBuf.append("  </sensor_configuration>\n");
	}	

	void doExport() throws IOException {
		StringBuffer fileBuf = new StringBuffer();
		String beginTimestamp = null;
		String endTimestamp = null;
		String gmtTimestamp = null;
		initFileBuf(fileBuf);	 
		endTimestamp = gmtTimestamp;
		closeFileBuf(fileBuf, beginTimestamp, endTimestamp);
		String fileContents = fileBuf.toString();
		File file = new File("/sdcard/SensorConfig/Config.txt");
		FileWriter sdWriter = new FileWriter(file, false);
		sdWriter.write(fileContents);
		sdWriter.close();
	}
}