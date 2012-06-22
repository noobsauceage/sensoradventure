package winlab.sensoradventure;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Environment;

public class ContinuousRecorder {

	private int MIC;
	private int SAMPLE;
	private int CHANNELI;
	private int CHANNELO;
	private int FORMAT;
	private int BUFFERSIZE;
	private int STREAM;
	private int MODE;
	private int i = 0;
	private AudioRecord recorder;
	private AudioTrack track;
	private AsyncTask<Void, Void, Void> asyncTask;
	private AsyncTask<Void,Void,Void> ast;
	private short[] buffer;
	private Sensors_SQLite sqla;
	//private FileWriter output;

	private FileChannel f;
	private boolean flag = true;
	private Context context;

	private String fileName = "PCM.txt";
	private File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	private File file = new File(path, fileName);
	private FileOutputStream output;



	public ContinuousRecorder(Context con) {
		setMic(AudioSource.MIC);
		setSamplingRate(44100);
		setChannelInput(AudioFormat.CHANNEL_IN_MONO);
		setChannelOutput(AudioFormat.CHANNEL_OUT_MONO);
		setEncodingFormat(AudioFormat.ENCODING_PCM_16BIT);
		setBufferSize(AudioRecord.getMinBufferSize(SAMPLE, CHANNELI,
				FORMAT));
		setStream(AudioManager.STREAM_MUSIC);
		setMode(AudioTrack.MODE_STREAM);

		context = con;
		sqla = new Sensors_SQLite(context);




	}

	public ContinuousRecorder(int mic, int sample, int channeli, int channelo,
			int format, int buffersize, int stream, int mode) {
		setMic(mic);
		setSamplingRate(sample);
		setChannelInput(channeli);
		setChannelOutput(channelo);
		setEncodingFormat(format);
		setBufferSize(buffersize);
		setStream(stream);
		setMode(mode);

	}

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

	public int getMic(){
		return MIC;
	}
	public int getSamplingRate(){
		return SAMPLE;
	}
	public int getChannelInput(){
		return CHANNELI;
	}
	public int getChannelOutput(){
		return CHANNELO;
	}
	public int getEncodingFormat(){
		return FORMAT;
	}
	public int getBufferSize(){
		return BUFFERSIZE;
	}
	public int getStream(){
		return STREAM;
	}
	public int getMode(){
		return MODE;
	}
	public short[] getBuffer(){
		return buffer;
	}

	public void record(){
		recorder = new AudioRecord(MIC, SAMPLE, CHANNELI,
				FORMAT, BUFFERSIZE);
		track = new AudioTrack(STREAM, SAMPLE, CHANNELO,
				FORMAT, BUFFERSIZE, MODE);

		sqla.open();
		sqla.deleteTable();

		asyncTask = new start();
		asyncTask.execute();

	}

	public void play(){
		track.play();
	}

	public void stop(){
		track.pause();
	}

	public void cancel(){
		asyncTask.cancel(true);
		recorder.stop();
		recorder.release();
		track.stop();
		track.release();
	}

	private class start extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... n) {

			try {
				output = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			short[][] buffers = new short[3][256];
			byte[] a = new byte[256];
			recorder.startRecording();
			while (!isCancelled()) {

				buffer = buffers[i++ % buffers.length];

				recorder.read(a,0, 256);

				track.write(a,0,256);

				try {
					output.write(a);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (flag && i>=2){
					ast = new fill();
					ast.execute();
					flag = false;
				}

				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				publishProgress();


			}

			return null;
		}
	}
	
	
	private class fill extends AsyncTask<Void,Void,Void>{


		@Override
		protected Void doInBackground(Void... params) {
		
			
			try {
				FileInputStream fin = new FileInputStream(file);
				byte content[] = new byte[256];
				for(int j = 0; j<i;j++){
				fin.read(content);
				sqla.insertMic(content);}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			sqla.copy();
			return null;
			

		}
	}
	
}


