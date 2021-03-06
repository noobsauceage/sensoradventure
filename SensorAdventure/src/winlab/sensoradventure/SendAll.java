package winlab.sensoradventure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.umass.cs.gcrs.gcrs.GCRS;
import edu.umass.cs.gcrs.server.HTTPClient;

public class SendAll extends Activity {

	private String LOG_TAG = "Uploader";
	private Map<String, String> id_data = new HashMap<String, String>();
	private HTTPClient gcrsClient = new HTTPClient();
	//private static Preferences userPreferencess = Preferences.userRoot().node(HTTPClient.class.getName());
	// in the HTTPClient..else it wont work
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new uploadTask().execute();
	}

	private class uploadTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			putParams();
			doUpload();
			Log.d(LOG_TAG, "Done uploading everything");
			
			
			//Really... we shouldnt have to do this... 
			gcrsClient.sendGetCommand("demo?passkey=umass"); // turn on demo mode
		      // When running this as a test we need to clear the database otherwise the users will already exist
			gcrsClient.sendGetCommand("clear"); // clear the database
			gcrsClient.sendGetCommand("demo?passkey=off"); // turn off demo mode
			
//			writeIP(getGuid(getID()));
			try {
				writeIP(id_data.get("guid"));
			} catch (RuntimeException e) {
				Log.d(LOG_TAG, "Failed to write IP");
			}
			
			return null;
		}
	}

	private void doUpload() {

		HttpURLConnection conn = null;
//		HttpsURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;

		
		/*File dir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS
						+ "/sensorData/");
		
		File[] fileList = dir.listFiles();*/
		
		String dirPath = Environment.getExternalStorageDirectory().getPath() + "/sensorData/";
		File dir = new File(dirPath);
		File[] fileList = dir.listFiles();

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		// String responseFromServer = "";

		String urlString = "http://dolan.bounceme.net/file_tosql.php";
//		String urlString = "https://dolan.bounceme.net/file_tosql.php";
		//String urlString = "https://192.168.1.45/file_tosql.php";

		for (File f : fileList) {

			try {
				// ------------------ CLIENT REQUEST
				Log.d(LOG_TAG, "Inside second Method");
				FileInputStream fileInputStream = new FileInputStream(f);

				// open a URL connection to the Servlet
				URL url = new URL(urlString);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
/*
				// Verify all hosts / certs
				try {
					HostnameVerifier hv = new HostnameVerifier() {
						public boolean verify(String urlHostName,
								SSLSession session) {
							System.out.println("Warning: URL Host: "
									+ urlHostName + " vs. "
									+ session.getPeerHost());
							return true;
						}
					};
					trustAllHttpsCertificates();
					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				} catch (Exception e) {
					// nothing
				}
*/
				
				// Open HTTPS connection
//				conn = (HttpsURLConnection) url.openConnection();
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

				// Post the parameters (ID/GUID)

				Set<String> keys = id_data.keySet();
				Iterator<String> keyIter = keys.iterator();
				// for(int i=0; keyIter.hasNext(); i++) {
				while (keyIter.hasNext()) {
					Object key = keyIter.next();
					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name="
							+ key + lineEnd);
					dos.writeBytes(lineEnd);
					dos.writeBytes(id_data.get(key));
					dos.writeBytes(lineEnd);
				}

				// Upload the actual File
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
						+ f.getAbsolutePath() + "\"" + lineEnd);
				dos.writeBytes(lineEnd);
				Log.d(LOG_TAG, "Headers are written");

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file and write it into form
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				// Send multipart form data necesssary after file data
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Close streams
				Log.d(LOG_TAG, "File is written");
				fileInputStream.close();
				dos.flush();
				dos.close();

			}

			catch (MalformedURLException ex) {
				Log.d(LOG_TAG, "error: " + ex.getMessage(), ex);
			}

			catch (IOException ioe) {
				Log.d(LOG_TAG, "error: " + ioe.getMessage(), ioe);
			}

			// Read the SERVER RESPONSE
			try {
				inStream = new DataInputStream(conn.getInputStream());
				String str;

				while ((str = inStream.readLine()) != null) {
					Log.d(LOG_TAG, "Server Response: " + str);
				}

				inStream.close();
			}

			catch (IOException ioex) {
				Log.d(LOG_TAG, "error: " + ioex.getMessage(), ioex);
			}
		}
		Log.d(LOG_TAG, "Done uploading everything");
	}

	private String getID() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	private String getGuid(String username) throws RuntimeException{
		Log.d(LOG_TAG, "Grabbing GUID");
		String guid = null;

		try {
			guid =  gcrsClient.lookupUserGuid(username);
		} catch (RuntimeException e) {
			try {
				guid =  gcrsClient.registerNewUser(username);
			} catch (NoSuchAlgorithmException e1) {
				GCRS.getLogger().severe(e1.toString());
			} catch (IOException e1) {
				GCRS.getLogger().severe(e1.toString());
			}
		} catch (IOException e) {
			GCRS.getLogger().severe(e.toString());
		}
		if (guid.contains("+BADUSER+")) {
			try {
				guid =  gcrsClient.registerNewUser(username);
			} catch (NoSuchAlgorithmException e1) {
				GCRS.getLogger().severe(e1.toString());
			} catch (IOException e1) {
				GCRS.getLogger().severe(e1.toString());
			}
		}
		return guid;
	}

	// Parameters to send
	private void putParams() {
		String ourId = getID();
		String ourGuid = "";
		
		try {
			ourGuid = getGuid(ourId);
		} catch (RuntimeException e) {
			ourGuid = "GCRS Fails";;
		}
		

		id_data.put("id", ourId);
		id_data.put("guid", ourGuid);
	}

	// Write the IP as a key-value pair to GCRS
	public void writeIP(String guid) throws RuntimeException {
		Log.d(LOG_TAG, "Attempting to write ip to: " + guid);
		HttpURLConnection conn = null;
		DataInputStream inStream = null;

		// String responseFromServer = "";

		String urlString = "http://dolan.bounceme.net/ip.php";
		//String urlString = "http://192.168.206.31/ip.php";

		try {
			Log.d(LOG_TAG, "Attempting connection");
			// open a URL connection to the Servlet
			URL url = new URL(urlString);

			// Open HTTP connection
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("GET");
		}
		catch (MalformedURLException ex) {
			Log.d(LOG_TAG, "error: " + ex.getMessage(), ex);
		}
		catch (IOException ioe) {
			Log.d(LOG_TAG, "error: " + ioe.getMessage(), ioe);
		}

		// Read the SERVER RESPONSE
		// and submit the key value pair
		try {
			inStream = new DataInputStream(conn.getInputStream());
			String str;
			String extIP = null;
			while ((str = inStream.readLine()) != null) {
				Log.d(LOG_TAG, "Server Response: " + str);
				extIP = str;
			}
			inStream.close();
			extIP = extIP.replaceAll("[\uFEFF-\uFFFF]", "");  // Strip UTF-8 BOM char
			
			// If we ever need to write several fields
			//JSONObject jsonObject = new JSONObject();
			//jsonObject.put("ip", extIP);
			//gcrsClient.writeFields(getGuid(getID()), jsonObject.toString());
			
			try {
				gcrsClient.writeField(guid, "ip", extIP);
				
				String result = gcrsClient.readField(guid, "ip", guid);
				Log.d(LOG_TAG, "Read field: " + result);
				
			} catch (InvalidKeyException e) {
				GCRS.getLogger().severe(e.toString());
			} catch (NoSuchAlgorithmException e) {
				GCRS.getLogger().severe(e.toString());
			} catch (SignatureException e) {
				GCRS.getLogger().severe(e.toString());
			}
			
		}

		catch (IOException ioex) {
			Log.d(LOG_TAG, "error: " + ioex.getMessage(), ioex);
		}
	}

	/*
	 * private SSLSocketFactory newSslSocketFactory(Context context) { try {
	 * KeyStore trusted = KeyStore.getInstance("BKS"); InputStream in =
	 * context.getResources().openRawResource( R.raw.mystore); try {
	 * trusted.load(in, "goobypls".toCharArray()); } finally { in.close(); }
	 * //SSLSocketFactory sf = new SSLSocketFactory(trusted); return new
	 * SSLSocketFactory(trusted); } catch (Exception e) { throw new
	 * AssertionError(e); } }
	 */
	/*
	 * private SSLSocketFactory newSslSocketFactory() { File pKeyFile = new
	 * File("/d1/cvs_all/aidapuser_1f5d_2011_03_1192.pfx"); String pKeyPassword
	 * = "UB#20abba"; KeyManagerFactory keyManagerFactory = KeyManagerFactory
	 * .getInstance("SunX509"); KeyStore keyStore =
	 * KeyStore.getInstance("PKCS12"); InputStream keyInput = new
	 * FileInputStream(pKeyFile); keyStore.load(keyInput,
	 * pKeyPassword.toCharArray()); keyInput.close();
	 * keyManagerFactory.init(keyStore, pKeyPassword.toCharArray()); SSLContext
	 * context = SSLContext.getInstance("TLS");
	 * context.init(keyManagerFactory.getKeyManagers(), null, new
	 * SecureRandom()); SSLSocketFactory sockFact = context.getSocketFactory();
	 * return sockFact;
	 * 
	 * }
	 */
	/*
	 * private SSLSocketFactory newSslSocketFactory(Context context) { //
	 * KeyStore keyStore = ...; try { KeyStore trusted =
	 * KeyStore.getInstance("BKS"); InputStream in =
	 * context.getResources().openRawResource( R.raw.mystore); try {
	 * trusted.load(in, "goobypls".toCharArray()); } finally { in.close(); } //
	 * SSLSocketFactory sf = new SSLSocketFactory(trusted); TrustManagerFactory
	 * tmf = TrustManagerFactory.getInstance("X509"); tmf.init(trusted);
	 * SSLContext sslcontext = SSLContext.getInstance("TLS");
	 * sslcontext.init(null, tmf.getTrustManagers(), null); return
	 * sslcontext.getSocketFactory();
	 * 
	 * } catch (Exception e) { throw new AssertionError(e); }
	 * 
	 * }
	 */

	public static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	private static void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());

	}

}

