package com.digdream.androidrubiksolver.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.android.RubiksCubeGLActivity;
import com.digdream.androidrubiksolver.ui.CameraActivity;
import com.digdream.androidrubiksolver.ui.CameraSurfaceActivity;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by dubuqingfeng on 16/03/15.
 */
public class SolverFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private static final String TAG = "SolverFragment";
    private View mView;
    TextView tv_state;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_solver, null);
        findViewsById();
        return mView;
    }

    private void findViewsById() {

        CardView cv_camera = (CardView) mView.findViewById(R.id.cv_camera);
        cv_camera.setOnClickListener(this);

        CardView cv_connect = (CardView) mView.findViewById(R.id.cv_connect);
        cv_connect.setOnClickListener(this);

        TextView tv_connect = (TextView) mView.findViewById(R.id.tv_connect);
        TextView tv_camera = (TextView) mView.findViewById(R.id.tv_camera);

        tv_state = (TextView) mView.findViewById(R.id.tv_state);
        tv_state.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_camera:
                Intent intentCamera = new Intent();
                intentCamera.setClass(SolverFragment.this.getActivity(), CameraActivity.class);
                startActivity(intentCamera);
                break;
            case R.id.cv_connect:
                searchBluetooth();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
            tv_state.setText(R.string.tv_bluetooth_state_enable);
        }
    }


    public void searchBluetooth(){
        //if fly
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();

            try {
                Method setDiscoverableTimeout = null;
                setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
                setDiscoverableTimeout.setAccessible(true);
                Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
                setScanMode.setAccessible(true);

                setDiscoverableTimeout.invoke(mBluetoothAdapter, 3600);
                setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,3600);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
            //startActivity(enable);
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        } else {
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        }
    }

}