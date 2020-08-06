package com.desafioreactnative;

import android.util.Log;
import android.widget.Toast;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlugPagModule extends ReactContextBaseJavaModule {

    private static ReactApplicationContext reactContext;
    private PlugPag plugpag;

    PlugPagModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    private void sendEvent(ReactApplicationContext reactContext, String eventName, final WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public String getName() {
        return "PlugPagModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("PAYMENT_CREDIT", PlugPag.TYPE_CREDITO);
        constants.put("PAYMENT_DEBIT", PlugPag.TYPE_DEBITO);
        constants.put("PAYMENT_VOUCHER", PlugPag.TYPE_VOUCHER);
        constants.put("INSTALLMENT_TYPE_A_VISTA", PlugPag.INSTALLMENT_TYPE_A_VISTA);
        constants.put("INSTALLMENT_TYPE_PARC_VENDEDOR", PlugPag.INSTALLMENT_TYPE_PARC_VENDEDOR);
        return constants;
    }

    @ReactMethod
    public void getLibVersion(Promise promise) {

        final WritableMap map = Arguments.createMap();

        String version = this.plugpag.getLibVersion();

        map.putString("version", version);

        promise.resolve(map);

    }

    @ReactMethod
    public void isAuthenticated(Promise promise) {
        Log.d("Authentication", "Authentication");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return plugpag.isAuthenticated();
            }
        };
        Future<Boolean> future = executor.submit(callable);
        executor.shutdown();

        final WritableMap map = Arguments.createMap();
        try {
            map.putBoolean("isAuthenticated", future.get());
            promise.resolve(map);
        } catch (InterruptedException e) {
            Log.d("Authentication Failure", e.getMessage());
            promise.reject("000", e.getMessage());
        } catch (ExecutionException e) {
            Log.d("Authentication Failure", e.getMessage());
            promise.reject("000", e.getMessage());
        }

    }

    @ReactMethod
    public void initializePlugPag(ReadableMap request) {
        Log.d("InitializePlugPag", "Starting initializePlugPag");
        String appName = request.getString("appName");
        String appVersion = request.getString("appVersion");

        // Cria a identificação do aplicativo
        PlugPagAppIdentification appIdentification = new PlugPagAppIdentification(appName, appVersion);

        // Cria a referência do PlugPag
        this.plugpag = new PlugPag(getReactApplicationContext(), appIdentification);
        Log.d("InitializePlugPag", "Finishing initializePlugPag");
    }

    @ReactMethod
    public void initializeAndActivatePinpad(ReadableMap request, Promise promise) {
        Log.d("InitializeAndActivatePinpad", "Starting InitializeAndActivatePinpad");
        String activationCode = request.getString("activationCode");
        PlugPagInitializationResult result = this.plugpag
                .initializeAndActivatePinpad(new PlugPagActivationData(activationCode));
        final WritableMap map = Arguments.createMap();
        if (result != null && result.getResult() == PlugPag.RET_OK) {
            Log.d("InitializeAndActivatePinpad", "Sucesss");
            map.putBoolean("sucess", true);
            promise.resolve(map);
        } else {
            Log.d("InitializeAndActivatePinpad", "Failure");
            map.putBoolean("sucess", false);
            promise.resolve(map);
        }
    }

    private void paymentListner() {
        this.plugpag.setEventListener(new PlugPagEventListener() {
            @Override
            public void onEvent(PlugPagEventData data) {
                final WritableMap map = Arguments.createMap();
                int eventCode = data.getEventCode();
                Log.d("Event", "" + eventCode);
                map.putInt("event", eventCode);
                sendEvent(reactContext, "paymentEvent", map);
            }
        });
    }

    @ReactMethod
    public void doPaymentCreditCrad(ReadableMap request, Promise promise) throws Exception {
        Log.d("doPaymentCreditCrad", "Starting...");
        String amount = request.getString("amount");
        String salesCode = request.getString("salesCode");

        // Define os dados do pagamento
        PlugPagPaymentData paymentData = new PlugPagPaymentData(PlugPag.TYPE_CREDITO, Integer.valueOf(amount),
                PlugPag.INSTALLMENT_TYPE_A_VISTA, 1, salesCode);

        final WritableMap map = Arguments.createMap();

        paymentListner();
        ExecutorService paymentExecutor = Executors.newSingleThreadExecutor();
        Callable<PlugPagTransactionResult> paymentCallable = new Callable<PlugPagTransactionResult>() {
            @Override
            public PlugPagTransactionResult call() {
                Log.d("doPaymentCreditCrad", "Making a call");
                return plugpag.doPayment(paymentData);
            }
        };

        Future<PlugPagTransactionResult> transactionResult = paymentExecutor.submit(paymentCallable);
        paymentExecutor.shutdown();
        Log.d("doPaymentCreditCrad", "Finished a call");

        if (transactionResult.get() != null && transactionResult.get().getResult() == PlugPag.RET_OK) {
            Log.d("doPaymentCreditCrad", "Payment created with successful");
            map.putInt("code", transactionResult.get().getResult());
            map.putString("msg", transactionResult.get().getMessage());
            promise.resolve(map);
        } else {
            Log.d("doPaymentCreditCrad", "Payment created with successful");
            promise.reject("erro", "Something wrong with transaction");
        }

    }

    @ReactMethod
    public void show(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}