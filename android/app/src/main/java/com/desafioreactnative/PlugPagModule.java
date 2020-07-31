package com.desafioreactnative;

import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlugPagModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;

    PlugPagModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "PlugPagModule";
    }

    @ReactMethod
    public void getApplicationVersion(Promise promise) {
        final WritableMap map = Arguments.createMap();
        PlugPag plugpag = new PlugPag(getReactApplicationContext(), new PlugPagAppIdentification("AppDemo","1.0"));
        PlugPagAppIdentification identification = plugpag.getAppIdentification();
        String version = identification.getVersion();
        promise.resolve(version);
    }

      @ReactMethod
    public void getLibVersion(Promise promise) {
        final WritableMap map = Arguments.createMap();
        PlugPag plugpag = new PlugPag(getReactApplicationContext(), new PlugPagAppIdentification("AppDemo","1.0"));
        String libVersion = plugpag.getLibVersion();
        promise.resolve(libVersion);
    }


    @ReactMethod
    public void show(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}