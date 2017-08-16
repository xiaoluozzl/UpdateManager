package com.xiaoluo.update;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaoluo.updatelib.UpdateManager;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        update = (Button) findViewById(R.id.update);

        UpdateManager.getInstance().init(this)
                .compare(UpdateManager.COMPARE_VERSION_NAME)
                .downloadUrl("http://ps.tupppai.com/wefun_v1.0.1.apk")
                .downloadTitle("正在下载...")
                .lastestVerName("1.1")
                .minVerName("1.0")
                .minVerCode(1)
                .update();

        UpdateManager.getInstance().setListener(new UpdateManager.UpdateListener() {
            @Override
            public void onCheckResult(String result) {
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager.getInstance().init(mContext)
                        .compare(UpdateManager.COMPARE_VERSION_NAME)
                        .downloadUrl("http://ps.tupppai.com/wefun_v1.0.1.apk")
                        .downloadTitle("正在下载...")
                        .lastestVerName("1.1")
                        .minVerName("1.0")
                        .minVerCode(1)
                        .update();
            }
        });
    }

}
