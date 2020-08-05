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
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;

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
    public void getLibVersion(ReadableMap request, Promise promise) throws Exception {

        final WritableMap map = Arguments.createMap();

        String appName = request.getString("appName");

        String appVersion = request.getString("appVersion");
        // Cria a identificação do aplicativo
        PlugPagAppIdentification appIdentification = new PlugPagAppIdentification(appName, appVersion);

        // Cria a referência do PlugPag
        PlugPag plugpag = new PlugPag(getReactApplicationContext(), appIdentification);

        // Obtém a versão da biblioteca
      
        String version = plugpag.getLibVersion();

        map.putString("version", version);

        promise.resolve(map);

    }

    @ReactMethod
    public void isAuthenticated(ReadableMap request, Promise promise) {
        String appName = request.getString("appName");
        String appVersion = request.getString("appVersion");

        PlugPagAppIdentification appIdentification = new PlugPagAppIdentification(appName, appVersion);
        final PlugPag plugpag = new PlugPag(getReactApplicationContext(), appIdentification);

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
            promise.reject("000", e.getMessage());
        } catch (ExecutionException e) {
            promise.reject("000", e.getMessage());
        }

    }

    @ReactMethod
    public void doPaymentCreditCrad(ReadableMap request, Promise promise) throws Exception {
        String appName = request.getString("appName");
        String appVersion = request.getString("appVersion");
        String activationCode = request.getString("activationCode");
        String amount = request.getString("amount");
        String SalesCode = request.getString("salesCode");

        // Define os dados do pagamento
        PlugPagPaymentData paymentData = new PlugPagPaymentData(PlugPag.TYPE_CREDITO, Integer.valueOf(amount),
                PlugPag.INSTALLMENT_TYPE_A_VISTA, 1, SalesCode);

        // Cria a identificação do aplicativo
        PlugPagAppIdentification appIdentification = new PlugPagAppIdentification(appName, appVersion);

        // Cria a referência do PlugPag
        PlugPag plugpag = new PlugPag(reactContext, appIdentification);

        ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
        Callable<PlugPagInitializationResult> connectionCallable = new Callable<PlugPagInitializationResult>() {
            @Override
            public PlugPagInitializationResult call() {
                return plugpag.initializeAndActivatePinpad(new PlugPagActivationData(activationCode));

            }
        };
        Future<PlugPagInitializationResult> initResult = connectionExecutor.submit(connectionCallable);
        connectionExecutor.shutdown();

        // Ativa terminal e faz o pagamento
        PlugPagInitializationResult initResultCode = initResult.get();

        final WritableMap map = Arguments.createMap();

        if (initResult.get() != null && initResultCode.getResult() == PlugPag.RET_OK) {

            ExecutorService paymentExecutor = Executors.newSingleThreadExecutor();
            Callable<PlugPagTransactionResult> paymentCallable = new Callable<PlugPagTransactionResult>() {
                @Override
                public PlugPagTransactionResult call() {
                    return plugpag.doPayment(paymentData);
                }
            };

            Future<PlugPagTransactionResult> transactionResult = paymentExecutor.submit(paymentCallable);

            if (transactionResult.get() != null && transactionResult.get().getResult() == PlugPag.RET_OK) {

                if (transactionResult.get() != null) {
                    map.putInt("code", transactionResult.get().getResult());
                    map.putString("msg", transactionResult.get().getMessage());
                    promise.resolve(map);
                }
            } else {
                promise.reject("erro", "Something wrong with transaction");
            }

        } else {
            promise.reject("erro", "Can't conect the device");
        }
    }

    @ReactMethod
    public void show(String message) {
        Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}