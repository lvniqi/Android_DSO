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

    //值
    private static final float values[] =
            {0.1f, 0.2f, 0.5f, 1.0f,
                    2.0f, 5.0f, 10.0f, 20.0f,
                    50.0f, 100.0f, 200.0f, 500.0f};
    //显示字符
    private static final String strings[] =
            {"0.1 ms", "0.2 ms", "0.5 ms",
                    "1.0 ms", "2.0 ms", "5.0 ms",
                    "10 ms", "20 ms", "50 ms",
                    "0.1 sec", "0.2 sec", "0.5 sec"};
    //采样率？
    private static final int counts[] =
            {256, 512, 1024, 2048,
                    4096, 8192, 16384, 32768,
                    65536, 131072, 262144, 524288};


    //常量SIZE = 20
    protected static final int SIZE = 20;
    protected static final float SMALL_SCALE = 200;
    protected static final float LARGE_SCALE = 200000;

    //采样率选择
    protected int timebase;
    //曲线
    private Wave wave;
    //数据源
    private Data_In data_in;
    //应用程序上浮动显示信息给用户
    private Toast toast;
    //子菜单
    private SubMenu submenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data_in = new Data_In();
        wave = (Wave)findViewById(R.id.waves);
        //如果找到wave
        if(wave != null)
        {
            wave.main = this;
            wave.data_in = data_in;
        }
        //设置时间间隔
        timebase = 3;
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

    //显示警告
    void showAlert(int appName, int errorBuffer)
    {
        //新建一个 警告框
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        // 设置 标题
        builder.setTitle(appName);
        // 设置 消息
        builder.setMessage(errorBuffer);
        //设置 按钮
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
        //建立
        AlertDialog dialog = builder.create();
        //显示
        dialog.show();
    }
    //数据输入
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
        //采样率
        private static final int SAMPLES = 524288;
        //缓冲长度
        private static final int FRAMES = 4096;

        private static final int INIT = 0;
        private static final int FIRST = 1;
        private static final int NEXT = 2;
        private static final int LAST = 3;
        //音频输入
        private AudioRecord audioRecord;
        //缓冲区
        private short buffer[];

        //数据输入
        protected Data_In() {
            buffer = new short[FRAMES];
            data = new short[SAMPLES];
        }


        //开始信号采集
        protected void start() {

            thread = new Thread(this, "Audio");
            thread.start();
        }


        //重写运行
        @Override
        public void run() {
            processAudio();
        }

        //停止运行
        protected void stop() {
            Thread t = thread;
            thread = null;

            // Wait for the thread to exit

            while (t != null && t.isAlive())
                Thread.yield();
        }

        // 处理信号
        protected void processAudio() {
            // Assume the output sample will work on the input as
            // there isn't an AudioRecord.getNativeInputSampleRate()

            sample =
                    AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

            // 得到缓冲区大小
            int size =
                    AudioRecord.getMinBufferSize(sample,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);

            // 出错 放弃
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

            // 创建线程

            audioRecord =
                    new AudioRecord(input, sample,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            size);

            // 校验

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

            // 校验状态

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

            // 开始录音

            audioRecord.startRecording();
            int index = 0;
            int count = 0;
            state = INIT;
            short last = 0;

            //进程结束前选择
            while (thread != null) {
                //读取缓冲
                size = audioRecord.read(buffer, 0, FRAMES);
                //如果大小为0 退出
                if (size == 0) {
                    thread = null;
                    break;
                }
                //状态机
                switch (state) {
                    //INIT 等待同步
                    case INIT:
                        index = 0;
                        if (bright)
                            state++;
                        else {
                            if (single && !trigger)
                                break;

                            //初始化同步
                            int dx = 0;

                            //同步极性
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

                        // 未同步 下次再说

                        if (state == INIT)
                            break;

                        // 重置触发

                        if (single && trigger)
                            trigger = false;

                    //第一次
                    case FIRST:

                        // 更新计数器

                        count = counts[timebase];
                        length = count;

                        //复制数据
                        System.arraycopy(buffer, index, data, 0, size - index);
                        index = size - index;

                        // 完成 重新等待
                        if (index >= count)
                            state = INIT;


                        else
                            //转到下一次
                            state++;
                        break;

                    //下一次采集
                    case NEXT:

                        // 复制数据
                        System.arraycopy(buffer, 0, data, index, size);
                        index += size;

                        // 完成 等待下次同步
                        if (index >= count)
                            state = INIT;

                    // Else if last but one chunk, get last chunk next time

                        else if (index + size >= count)
                            state++;
                        break;

                    // 最后一个数据块
                    case LAST:
                        // 复制数据
                        System.arraycopy(buffer, 0, data, index, count - index);
                        //等待下次同步
                        state = INIT;
                        break;
                }

                // 更新显示

                wave.postInvalidate();
            }

            // 停止 释放麦克风

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
        }
    }
}
