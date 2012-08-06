package winlab.contexts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ContextGrabActivity extends Activity {

	// Dictionary of sensor names to sensor type number (the table name in SQL)
	// Or you can use Sensor.TYPE_ACCELEROMETER.toString()
	private final String ACCELEROMETER = "1";
	private final String MAGNETIC = "2";
	private final String ORIENTATION = "3";
	private final String GYROSCOPE = "4";
	private final String LIGHT = "5";
	private final String PRESSURE = "6";
	private final String TEMPERATURE = "7";
	private final String PROXIMITY = "8";
	private final String GRAVITY = "9";
	private final String LINEAR_ACCELERATION = "10";
	private final String ROTATION = "11";
	private final String GPS = "gps";
	private final String BLUETOOTH = "bt";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Example call to the query generator

		/*
		 * Log.d("grab",sqlSelectQuery( ACCELEROMETER, // Select sensor new
		 * double[]{0,0,0}, // Values to compare each field to new
		 * String[]{"<",">",">"}, // The comparator used for each respective
		 * value 100000, // The time window of measurements ">" // Which side of
		 * the time window ));
		 */

		new sendTask().execute();
	}

	private class sendTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			String q1 = sqlSelectQuery(ACCELEROMETER, // Select sensor
					new double[] { 0, 0, 0 }, // Values to compare each field to
					new String[] { "<", ">", ">" }, // The comparator used for
													// each respective value
					0, // The time window of measurements
					">" // Which side of the time window
			);

			Log.d("grab", q1);
			submitQuery(q1);
			return null;
		}
	}

	public String sqlSelectQuery(String sensorType, double[] values,
			String[] valComparators, long howRecent, String timeComparator) {
		String cmd = "SELECT DISTINCT";
		String getCol = "`guid`";
		String location = "FROM";
		String table = "`" + sensorType + "`";
		String where = "WHERE";
		String and = "AND";
		String conditionCmd = where;
		String conditionCol = "`timestamp`";

		List<String> strArr = new ArrayList<String>();
		strArr.addAll(Arrays
				.asList(new String[] { cmd, getCol, location, table }));

		if (values.length == 3) {
			strArr.addAll(Arrays.asList(new String[] { conditionCmd, "`x`",
					valComparators[0], String.valueOf(values[0]) }));
			conditionCmd = and;
			strArr.addAll(Arrays.asList(new String[] { conditionCmd, "`y`",
					valComparators[1], String.valueOf(values[1]) }));
			strArr.addAll(Arrays.asList(new String[] { conditionCmd, "`z`",
					valComparators[2], String.valueOf(values[2]) }));
		} else if (values.length == 1) {
			strArr.addAll(Arrays.asList(new String[] { conditionCmd, "`value`",
					valComparators[0], String.valueOf(values[0]) }));
		}

		if (howRecent > 0) {
			String minTime = String.valueOf(System.currentTimeMillis()
					- howRecent);
			strArr.addAll(Arrays.asList(new String[] { conditionCmd,
					conditionCol, timeComparator, minTime }));
		}

		String query = TextUtils.join(" ", strArr);
		return query;
	}

	//
	private String[] submitQuery(String query) {
		
		String[] guidStrings = null;

		HttpURLConnection conn = null;
		// HttpsURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		// String responseFromServer = "";

		String urlString = "http://dolan.bounceme.net/submit_query.php";
		// String urlString = "192.168.1.45/submit_query.php";
		// String urlString = "https://dolan.bounceme.net/submit_query.php";

		try {
			// ------------------ CLIENT REQUEST
			Log.d("submitter", "Inside second Method");

			// open a URL connection to the Servlet
			URL url = new URL(urlString);

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();

			/*
			 * // Verify all hosts / certs try { HostnameVerifier hv = new
			 * HostnameVerifier() { public boolean verify(String urlHostName,
			 * SSLSession session) { System.out.println("Warning: URL Host: " +
			 * urlHostName + " vs. " + session.getPeerHost()); return true; } };
			 * trustAllHttpsCertificates();
			 * HttpsURLConnection.setDefaultHostnameVerifier(hv); } catch
			 * (Exception e) { // nothing }
			 */

			// Open HTTP connection
			// conn = (HttpsURLConnection) url.openConnection();
			conn = (HttpURLConnection) url.openConnection();
			// conn.setSSLSocketFactory(context.getSocketFactory());/////////////////
			// conn.setSSLSocketFactory(newSslSocketFactory(this));/////////////////

			// Allow Inputs
			conn.setDoInput(true);

			// Allow Outputs
			conn.setDoOutput(true);

			// Don't use a cached copy.
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			// System.setProperty("http.keepAlive", "false");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			dos = new DataOutputStream(conn.getOutputStream());

			// Post the query

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=" + "query"
					+ lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(query);
			dos.writeBytes(lineEnd);

			// Send multi-part form data necessary after all data
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Close streams
			Log.d("submitter", "Query is submitted");
			dos.flush();
			dos.close();

		}

		catch (MalformedURLException ex) {
			Log.e("submitter", "error: " + ex.getMessage(), ex);
		}

		catch (IOException ioe) {
			Log.e("submitter", "error: " + ioe.getMessage(), ioe);
		}

		// Read the SERVER RESPONSE
		try {
			inStream = new DataInputStream(conn.getInputStream());
			String str;
			String json = null;
			while ((str = inStream.readLine()) != null) {
				Log.d("submitter", "Server Response: " + str);
				json = str;
			}

			// Strip BOM's
			json = json.replaceAll("[\uFEFF-\uFFFF]", "");

			try {
				JSONArray jarray = (JSONArray) new JSONTokener(json).nextValue();
				Log.d("submitter", "tokenized");
				guidStrings = new String[jarray.length()];
				for (int i = 0; i < jarray.length(); i++) {
					guidStrings[i] = jarray.getString(i);
				}

			} catch (JSONException e) {
				// handle JSON parsing exceptions...
				Log.d("submitter", e.toString());
			}

			inStream.close();
		}

		catch (IOException ioex) {
			Log.d("submitter", "error: " + ioex.getMessage(), ioex);
		}
		Log.d("submitter", "Done submitting");
		return guidStrings;
	}
	

}