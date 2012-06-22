package edu.umass.cs.gcrs.server;

import static edu.umass.cs.gcrs.server.Defs.KEYSEP;
import static edu.umass.cs.gcrs.server.Defs.QUERYPREFIX;
import static edu.umass.cs.gcrs.server.Defs.VALSEP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import edu.umass.cs.gcrs.utilities.URIEncoderDecoder;
import edu.umass.cs.gcrs.utilities.Utils;

//import edu.umass.cs.gcrs.gcrs.GCRS;
//import edu.umass.cs.gcrs.server.HOST;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class HTTPClient {

	public static String HOST = "http://umassmobilityfirst.net";

	/**
	 * Register a new username on the GCRS server. A guid is returned by the
	 * server. Generates a public / private key pair which is saved in
	 * preferences and sent to the server with the username.
	 * 
	 * Query format: registerEntity?name=<userName>&publickey=<publickey>
	 * 
	 * @param username
	 * @return guid
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public static String registerNewUser(String username) throws IOException,
			NoSuchAlgorithmException {

		KeyPair keyPair = KeyPairGenerator.getInstance(Protocol.RASALGORITHM)
				.generateKeyPair();
		// saveKeyPairToPreferences(username, keyPair);

		PublicKey publicKey = keyPair.getPublic();
		byte[] publicKeyBytes = publicKey.getEncoded();
		String publicKeyString = Utils.toHex(publicKeyBytes);

		String command = createQuery(Protocol.REGISTERENTITY, Protocol.NAME,
				URIEncoderDecoder.quoteIllegal(username, ""),
				Protocol.PUBLICKEY, publicKeyString);
		String response = sendGetCommand(command);

		// saveKeyPairToPreferences(response, keyPair);

		if (response.startsWith(Protocol.BADRESPONSE)) {
			// throw (new RuntimeException("Bad response to command: " +
			// command));
			return lookupUserGuid(username);
		} else {
			return response;
		}
	}

	/**
	 * Obtains the guid of the username from the GCRS server.
	 * 
	 * Query format: lookupEntity?name=<userName>
	 * 
	 * @param username
	 * @return guid
	 * @throws IOException
	 * 
	 */
	public static String lookupUserGuid(String username) throws IOException {
		String command = createQuery(Protocol.LOOKUPENTITY, Protocol.NAME,
				URIEncoderDecoder.quoteIllegal(username, ""));
		String response = sendGetCommand(command);

		if (response.startsWith(Protocol.BADRESPONSE)) {
			throw (new RuntimeException("Bad response: " + response
					+ " Command: " + command));
		} else {
			return response;
		}
	}

	/**
	 * Creates a http query string from the given action string and a variable
	 * number of key and value pairs.
	 * 
	 * @param action
	 * @param keysAndValues
	 * @return the query string
	 * @throws IOException
	 */
	public static String createQuery(String action, String... keysAndValues)
			throws IOException {
		String key;
		String value;
		StringBuilder result = new StringBuilder(action + QUERYPREFIX);

		for (int i = 0; i < keysAndValues.length; i = i + 2) {
			key = keysAndValues[i];
			value = keysAndValues[i + 1];
			result.append(URIEncoderDecoder.quoteIllegal(key, "") + VALSEP
					+ URIEncoderDecoder.quoteIllegal(value, "")
					+ (i + 2 < keysAndValues.length ? KEYSEP : ""));
		}
		return result.toString();
	}

	/**
	 * Sends a HTTP get with given queryString to the host specified by the
	 * {@link HOST} field.
	 * 
	 * @param queryString
	 * @return result of get as a string
	 */
	public static String sendGetCommand(String queryString) {
		HttpURLConnection connection = null;
		OutputStreamWriter wr = null;
		BufferedReader rd = null;
		StringBuilder sb = null;

		URL serverAddress = null;

		try {
			String urlString = HOST + "/GCRS/" + queryString;
			// GCRS.getLogger().finer("URL String = " + urlString);
			serverAddress = new URL(urlString);
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			// get the output stream writer and write the output to the server
			// not needed in this example
			// wr = new OutputStreamWriter(connection.getOutputStream());
			// wr.write("");
			// wr.flush();

			// read the result from the server
			rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			sb = new StringBuilder();

			String response = rd.readLine(); // we only expect one line to be
												// sent
			if (response != null) {
				return response;
			} else {
				throw (new RuntimeException("No response to command: "
						+ queryString));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// close the connection, set all objects to null
			connection.disconnect();
			rd = null;
			sb = null;
			wr = null;
			connection = null;
		}
		return "";
	}

}