package com.qy.j4u;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.qy.j4u.eventmessages.RaspberryIp;
import com.qy.j4u.global.ForUApplication;
import com.qy.j4u.pojo.DaoSession;
import com.qy.j4u.pojo.GreenDaoTestPojo;
import com.qy.j4u.services.RemoteCoreService;
import com.qy.j4u.utils.JLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_raspberry_ip)
    TextView mTvIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            JLog.d("MainActivity","onCreate intent flag FLAG_ACTIVITY_BROUGHT_TO_FRONT");
            finish();
            return;
        }

        DaoSession daoSession = ForUApplication.getInstance().getGreenDaoSession();
        GreenDaoTestPojo pojo = new GreenDaoTestPojo();
        pojo.setText("这是第二次要插入到数据库的");
        pojo.setDate(new Date());
        daoSession.insert(pojo);
        List<GreenDaoTestPojo> greenDaoTestPojos = daoSession.loadAll(GreenDaoTestPojo.class);
//        JLog.i("id为:" + pojo.getId());
        for (int i = 0; i < greenDaoTestPojos.size(); i++) {
            JLog.i(greenDaoTestPojos.get(i));
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.tv_hello)).setText(new TestMyAnnotation().getContent());
//                ARouter.getInstance().build("/testARouter/activity")
//                        .withString("passStr","你好,ARouter")
//                        .navigation();
            }
        });


        bindService(new Intent(this, RemoteCoreService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                CoreBinder coreBinder = CoreBinder.Stub.asInterface(service);
                try {
                    JLog.d(coreBinder.getContent());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onRaspIp(RaspberryIp raspberryIp) {
        mTvIp.setText(raspberryIp.getIp());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
