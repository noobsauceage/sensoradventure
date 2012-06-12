package winlab.sensoradventure;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;

public class ContinuousRecorder {

	private int MIC;
	private int SAMPLE;
	private int CHANNELI;
	private int CHANNELO;
	private int FORMAT;
	private int BUFFERSIZE;
	private int STREAM;
	private int MODE;
	private AudioRecord recorder;
	private AudioTrack track;
	private AsyncTask<Void, Void, Void> asyncTask;
	private short[] buffer;
	boolean play = true;

	public ContinuousRecorder() {
		setMic(AudioSource.MIC);
		setSamplingRate(44100);
		setChannelInput(AudioFormat.CHANNEL_IN_MONO);
		setChannelOutput(AudioFormat.CHANNEL_OUT_MONO);
		setEncodingFormat(AudioFormat.ENCODING_PCM_16BIT);
		setBufferSize(AudioRecord.getMinBufferSize(SAMPLE, CHANNELI,
				FORMAT));
		setStream(AudioManager.STREAM_MUSIC);
		setMode(AudioTrack.MODE_STREAM);

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
			play = false;
			short[][] buffers = new short[3][256];
			int i = 0;
			recorder.startRecording();
			//track.play();
			while (!isCancelled()) {

				buffer = buffers[i++ % buffers.length];
				recorder.read(buffer, 0, buffer.length);
				track.write(buffer, 0, buffer.length);
				publishProgress();
			}
			
			return null;

		}
	}
	

};
