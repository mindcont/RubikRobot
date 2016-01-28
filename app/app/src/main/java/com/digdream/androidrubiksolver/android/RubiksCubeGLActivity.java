package com.digdream.androidrubiksolver.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.digdream.androidrubiksolver.R;

public class RubiksCubeGLActivity extends Activity {
    private RubiksCubeGLSurfaceView glView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.activity_rubiks_cube_gl, null);//设置视图显示
        setContentView(mView);
        glView = new RubiksCubeGLSurfaceView(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainLayout);
        linearLayout.addView(glView);
        findViewsById();
    }

    private void findViewsById() {
        LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);
        LinearLayout buttonsLayout2 = (LinearLayout) findViewById(R.id.buttonsLayout2);
        LinearLayout buttonsLayout3 = (LinearLayout) findViewById(R.id.buttonsLayout3);
        final String[] names = {
                "U",
                "U-",
                "R",
                "R-",
                "F",
                "F-",
                "D",
                "D-",
                "L-",
                "L-",
                "B",
                "B-"
        };
        for (int i = 0; i < 12; i++) {
            Button btn = new Button(this);
            btn.setText(names[i]);
            final int id = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    glView.getRenderer().rotating(id);
                }
            });
            if (id < 4) {
                buttonsLayout.addView(btn);
            } else if (id < 8) {
                buttonsLayout2.addView(btn);
            } else {
                buttonsLayout3.addView(btn);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
         case R.id.menu_settings:
                return true;
            case R.id.menu_scramble:
                glView.getRenderer().toggleScrambleCube();
                return true;
            case R.id.menu_solve:
                glView.getRenderer().toggleSolveCube();
                return true;
            case R.id.menu_reset_camera:
                glView.getRenderer().resetCamera();
                return true;
            case R.id.menu_reset_cube:
                glView.getRenderer().resetCube();
                return true;
        }
        return false;
    }*/
}
