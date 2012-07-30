package winlab.sensoradventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import winlab.SensorGUI.StartGUI;
import winlab.sql.Mic_SQL;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/* This is a class that implements continuous time recording.
 * It makes use of the AudioTrack & AudioRecord classes available
 * in the Android SDK. Buffer data can be logged in two ways:
 * one is the file stored on the disc and the other is an internal
 * SQLite database.
 * This class requires the following permissions:
 * 	android.permission.RECORD_AUDIO
 android.permission.WRITE_EXTERNAL_STORAGE
 * Written by G.D.C.
 */ 
public class ContinuousRecorder {

	private int MIC; // The audio source for recording
	private int SAMPLE; // Sampling rate: Typically 8000,16000,44100,etc.
	private int CHANNELI; // Channel Input configuration (Mono, Stereo)
	private int CHANNELO; // Channel output configuration (Mono, Stereo)
	private int FORMAT; // Encoding format. Can select PCM 8Bit or 16Bit.
	private int BUFFERSIZE; /*
							 * Buffersize. This is only used currently to create
							 * AudioTrack & Record objects. The actual buffer
							 * used is of constant size 256.
							 */

	private int STREAM; // Output streaming. Default is music.
	private int MODE; // Output mode. Set to stream.
	private int i = 0; /*
						 * An updating index number modularly acted upon by
						 * buffer length. Also used to keep track of how many
						 * buffers are written to the file "PCM.txt"
						 */
	private AudioRecord recorder; // Used to record audio into a byte buffer.
	private AudioTrack track; // Used to play audio from a byte buffer.
	private AsyncTask<Void, Void, Void> asyncTask; // Asynchronous task.
	private AsyncTask<Void, Void, Void> ast; // Asynchronous task.
	private Mic_SQL sqla; // SQLite Database helper.
	private Context context; // Program context needed to create SQLite helper.
	private String fileName = "PCM.txt"; // Filename of the raw audio data.
	private File path; // Location
																					// of
																					// file.
	private File file; // Raw audio data file.
	private FileOutputStream output; // Used to write to above file.
	
	private boolean SQLite = false;

	// Creates the default C.R. with optimal settings.
	public ContinuousRecorder(Context con) {
		path=SensorAdventureActivity.DataPath;
		file = new File(path,fileName);
		setMic(AudioSource.MIC);
		setSamplingRate(44100);
		setChannelInput(AudioFormat.CHANNEL_IN_MONO);
		setChannelOutput(AudioFormat.CHANNEL_OUT_MONO);
		setEncodingFormat(AudioFormat.ENCODING_PCM_16BIT);
		setBufferSize(AudioRecord.getMinBufferSize(SAMPLE, CHANNELI, FORMAT));
		setStream(AudioManager.STREAM_MUSIC);
		setMode(AudioTrack.MODE_STREAM);

		context = con;
		sqla = new Mic_SQL(context);

	}

	// Creates a customized C.R. where all parameters need to be set.
	public ContinuousRecorder(int mic, int sample, int channeli, int channelo,
			int format, int stream, int mode, Context con) {
		path=SensorAdventureActivity.DataPath;
		file = new File(path,fileName);
		setMic(mic);
		setSamplingRate(sample);
		setChannelInput(channeli);
		setChannelOutput(channelo);
		setEncodingFormat(format);
		setBufferSize(AudioRecord.getMinBufferSize(SAMPLE, CHANNELI, FORMAT));
		setStream(stream);
		setMode(mode);

		context = con;
		sqla = new Mic_SQL(context);

	}

	// Below are the set functions which assign values to the private members.
	public void setMic(int mic2) {
		MIC = mic2;
	}

	public void setSamplingRate(int i) {
		SAMPLE = i;
	}

	public void setChannelInput(int channelInMono) {
		CHANNELI = channelInMono;
	}

	public void setChannelOutput(int channelOutMono) {
		CHANNELO = channelOutMono;
	}

	public void setEncodingFormat(int encodingPcm16bit) {
		FORMAT = encodingPcm16bit;

	}

	public void setBufferSize(int BufferSize) {
		BUFFERSIZE = BufferSize;
	}

	public void setStream(int streamMusic) {
		STREAM = streamMusic;
	}

	public void setMode(int modeStream) {
		MODE = modeStream;
	}
    
	// Below are the get functions which allow the user to retrieve the data of
	// the private members.
	public int getMic() {
		return MIC;
	}

	public int getSamplingRate() {
		return SAMPLE;
	}

	public int getChannelInput() {
		return CHANNELI;
	}

	public int getChannelOutput() {
		return CHANNELO;
	}

	public int getEncodingFormat() {
		return FORMAT;
	}

	public int getBufferSize() {
		return BUFFERSIZE;
	}

	public int getStream() {
		return STREAM;
	}

	public int getMode() {
		return MODE;
	}

	/*
	 * Record method instantiates the AudioRecord & AudioTrack objects as well
	 * as the SQLite Helper and the asynchronous task used to do background
	 * recording & playing.
	 */

	public void record() {
		recorder = new AudioRecord(MIC, SAMPLE, CHANNELI, FORMAT, BUFFERSIZE);
		track = new AudioTrack(STREAM, SAMPLE, CHANNELO, FORMAT, BUFFERSIZE,
				MODE);
        if (SQLite)
        {
		sqla.open();
		sqla.deleteTable();
        }
		asyncTask = new start(); // See below for the start class definition.
		asyncTask.execute();

	}

	// Enables continuous audio playback.
	public void play() {
		track.play();
	}

	// Disables continuous audio playback.
	public void stop() {
		track.pause();
	}

	// Cancels recording operation.
	public void cancel() {
		asyncTask.cancel(true);
		recorder.stop();
		recorder.release();
		track.stop();
		track.release();
	}
	
	public void writeToSQLite(){
		SQLite = true;
	}

	private class start extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... n) {

			// Create the F.O.S. to write the byte buffer to the file.
			try {
				output = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			byte[] a = new byte[256]; // Holding place for raw audio data.
			recorder.startRecording(); // Method of AudioRecorder; begins
										// recording.

			long begin = System.currentTimeMillis();
			long end = 0;
			// Loop will continue until the AsyncTask is terminated.
			// This is most quickly achieved by calling cancel().
			while (!isCancelled()) {

				recorder.read(a, 0, 256); // Reads the raw audio data into byte
											// buffer a.
				
				track.write(a, 0, 256); // Writes the byte buffer of raw audio
										// data to the speakers.
				i++;

				// Write the byte buffer to "PCM.txt"
				try {
					output.write(a);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Publish progress of AsyncTask on the main thread.
				publishProgress();

			}
			end = System.currentTimeMillis();
			Log.e("APP", Long.toString(end - begin));
			System.out.println(end - begin);

			// Close the output file.
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Begin the asynchronous task of writing to the SQLite database.
			if(SQLite){
			ast = new fill();
			ast.execute();}
			System.out.println("end");

			return null;
		}
	}

	// AsyncTask that writes to SQLite database.
	private class fill extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			sqla.prepareTransaction(); // Prepares the SQLite database for batch
										// inputs.
			long begin = System.currentTimeMillis();

			try {
				FileInputStream fin = new FileInputStream(file); // Create a FIS
																	// to read
																	// from the
																	// file.
				byte content[] = new byte[256]; // Array to store the data from
												// the file.
				for (int j = 0; j < i; j++) { // Loop through the number of byte
												// arrays stored in file.
					fin.read(content); // Read file data into buffer.
					sqla.insertMic(content); // Insert buffer as a blob into
												// SQLite database.
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sqla.endTransaction(); // Ends the batch transaction.
			long end = System.currentTimeMillis();
			Log.e("SQL", Long.toString(end - begin));
			sqla.copy(); // Copies SQLite database to SDCard.
			sqla.close(); // Closes SQLite database.
			SQLite = false;
			return null;

		}
	}
	
	//the function is for debugging
	public void debug(){
		//MIC,SAMPLE,CHANNELI,CHANNELO,FORMAT,BUFFERSIZE; 

	//STREAM,MODE 
		Toast.makeText(context, "debug: "+MIC+" "+SAMPLE+" "+CHANNELI+" "+CHANNELO+" "
				+FORMAT+" "+BUFFERSIZE+" "+STREAM+" "+MODE,Toast.LENGTH_LONG).show();
	}

}
