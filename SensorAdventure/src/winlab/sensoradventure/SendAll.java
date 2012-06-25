package winlab.sensoradventure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.umass.cs.gcrs.server.HTTPClient;

public class SendAll extends Activity {

	private Map<String, String> id_data = new HashMap<String, String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new uploadTask().execute();
	}

	private class uploadTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			doUpload();
			return null;
		}
	}

	private void doUpload() {

		putParams();

		// HttpURLConnection conn = null;
		HttpsURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;

		String dirPath = "/sdcard/sensorData/";
		File dir = new File(dirPath);
		File[] fileList = dir.listFiles();

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		// String responseFromServer = "";

		// String urlString = "http://dolan.bounceme.net/file_tosql.php";
		String urlString = "https://dolan.bounceme.net/file_tosql.php";

		for (File f : fileList) {

			try {
				// ------------------ CLIENT REQUEST
				Log.d("MediaPlayer", "Inside second Method");
				FileInputStream fileInputStream = new FileInputStream(f);

				// open a URL connection to the Servlet
				URL url = new URL(urlString);

				// Open a HTTP connection to the URL
				// conn = (HttpURLConnection) url.openConnection();

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

				// Open HTTPS connection
				conn = (HttpsURLConnection) url.openConnection();
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
				Log.d("MediaPlayer", "Headers are written");

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
				Log.d("MediaPlayer", "File is written");
				fileInputStream.close();
				dos.flush();
				dos.close();

			}

			catch (MalformedURLException ex) {
				Log.d("MediaPlayer", "error: " + ex.getMessage(), ex);
			}

			catch (IOException ioe) {
				Log.d("MediaPlayer", "error: " + ioe.getMessage(), ioe);
			}

			// Read the SERVER RESPONSE
			try {
				inStream = new DataInputStream(conn.getInputStream());
				String str;

				while ((str = inStream.readLine()) != null) {
					Log.d("MediaPlayer", "Server Response: " + str);
				}

				inStream.close();
			}

			catch (IOException ioex) {
				Log.d("MediaPlayer", "error: " + ioex.getMessage(), ioex);
			}
		}
		Log.d("MediaPlayer", "Done uploading everything");
	}

	private String getID() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	private void putParams() {
		// Parameters to send
		id_data.put("id", getID());
		try {
			id_data.put("guid", HTTPClient.registerNewUser(getID()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
