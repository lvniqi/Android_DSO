package demos.dso_demo1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final float values[] =
            {0.1f, 0.2f, 0.5f, 1.0f,
                    2.0f, 5.0f, 10.0f, 20.0f,
                    50.0f, 100.0f, 200.0f, 500.0f};

    private static final String strings[] =
            {"0.1 ms", "0.2 ms", "0.5 ms",
                    "1.0 ms", "2.0 ms", "5.0 ms",
                    "10 ms", "20 ms", "50 ms",
                    "0.1 sec", "0.2 sec", "0.5 sec"};

    private static final int counts[] =
            {256, 512, 1024, 2048,
                    4096, 8192, 16384, 32768,
                    65536, 131072, 262144, 524288};


    //常量SIZE = 20
    protected static final int SIZE = 20;
    protected static final float SMALL_SCALE = 200;
    protected static final float LARGE_SCALE = 200000;


    protected int timebase;

    private Wave wave;

    private Data_In data_in;
    private Toast toast;
    private SubMenu submenu;

    //数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showAlert(int appName, int errorBuffer)
    {
        // Create an alert dialog builder

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        // Set the title, message and button

        builder.setTitle(appName);
        builder.setMessage(errorBuffer);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        // Dismiss dialog

                        dialog.dismiss();
                    }
                });
        // Create the dialog

        AlertDialog dialog = builder.create();

        // Show it

        dialog.show();
    }
    public class Data_In implements Runnable {
        // Preferences

        protected boolean bright;
        protected boolean single;
        protected boolean trigger;
        protected boolean polarity;

        protected int input;
        protected int sample;

        // Data

        protected Thread thread;
        protected short data[];
        protected long length;

        // Private data

        private static final int SAMPLES = 524288;
        private static final int FRAMES = 4096;

        private static final int INIT = 0;
        private static final int FIRST = 1;
        private static final int NEXT = 2;
        private static final int LAST = 3;

        private AudioRecord audioRecord;
        private short buffer[];

        // Constructor

        protected Data_In() {
            buffer = new short[FRAMES];
            data = new short[SAMPLES];
        }

        // Start audio

        protected void start() {
            // Start the thread

            thread = new Thread(this, "Audio");
            thread.start();
        }

        // Run

        @Override
        public void run() {
            processAudio();
        }

        // Stop

        protected void stop() {
            Thread t = thread;
            thread = null;

            // Wait for the thread to exit

            while (t != null && t.isAlive())
                Thread.yield();
        }

        // Process Audio

        protected void processAudio() {
            // Assume the output sample will work on the input as
            // there isn't an AudioRecord.getNativeInputSampleRate()

            sample =
                    AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

            // Get buffer size

            int size =
                    AudioRecord.getMinBufferSize(sample,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            // Give up if it doesn't work

            if (size == AudioRecord.ERROR_BAD_VALUE ||
                    size == AudioRecord.ERROR ||
                    size <= 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name,
                                R.string.error_buffer);
                    }
                });

                thread = null;
                return;
            }

            // Create the AudioRecord object

            audioRecord =
                    new AudioRecord(input, sample,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            size);

            // Check audiorecord

            if (audioRecord == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name,
                                R.string.error_init);
                    }
                });

                thread = null;
                return;
            }

            // Check state

            int state = audioRecord.getState();

            if (state != AudioRecord.STATE_INITIALIZED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlert(R.string.app_name,
                                R.string.error_init);
                    }
                });

                audioRecord.release();
                thread = null;
                return;
            }

            // Start recording

            audioRecord.startRecording();

            int index = 0;
            int count = 0;

            state = INIT;
            short last = 0;

            // Continue until the thread is stopped

            while (thread != null) {
                // Read a buffer of data

                size = audioRecord.read(buffer, 0, FRAMES);

                // Stop the thread if no data

                if (size == 0) {
                    thread = null;
                    break;
                }

                // State machine for sync and copying data to display buffer

                switch (state) {
                    // INIT: waiting for sync

                    case INIT:

                        index = 0;

                        if (bright)
                            state++;

                        else {
                            if (single && !trigger)
                                break;

                            // Initialise sync

                            int dx = 0;

                            // Sync polarity

                            if (polarity) {
                                for (int i = 0; i < size; i++) {
                                    dx = buffer[i] - last;

                                    if (dx < 0 && last > 0 && buffer[i] < 0) {
                                        index = i;
                                        state++;
                                        break;
                                    }

                                    last = buffer[i];
                                }
                            } else {
                                for (int i = 0; i < size; i++) {
                                    dx = buffer[i] - last;

                                    if (dx > 0 && last < 0 && buffer[i] > 0) {
                                        index = i;
                                        state++;
                                        break;
                                    }

                                    last = buffer[i];
                                }
                            }
                        }

                        // No sync, try next time

                        if (state == INIT)
                            break;

                        // Reset trigger

                        if (single && trigger)
                            trigger = false;

                        // FIRST: First chunk of data

                    case FIRST:

                        // Update count

                        count = counts[timebase];
                        length = count;

                        // Copy data

                        System.arraycopy(buffer, index, data, 0, size - index);
                        index = size - index;

                        // If done, wait for sync again

                        if (index >= count)
                            state = INIT;

                            // Else get some more data next time

                        else
                            state++;
                        break;

                    // NEXT: Subsequent chunks of data

                    case NEXT:

                        // Copy data

                        System.arraycopy(buffer, 0, data, index, size);
                        index += size;

                        // Done, wait for sync again

                        if (index >= count)
                            state = INIT;

                            // Else if last but one chunk, get last chunk next time

                        else if (index + size >= count)
                            state++;
                        break;

                    // LAST: Last chunk of data

                    case LAST:

                        // Copy data

                        System.arraycopy(buffer, 0, data, index, count - index);

                        // Wait for sync next time

                        state = INIT;
                        break;
                }

                // Update display

                wave.postInvalidate();
            }

            // Stop and release the audio recorder

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
        }
    }

}
