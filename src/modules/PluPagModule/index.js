import { NativeModules } from 'react-native';

const { PlugPagModule } = NativeModules;

export const RNPlugPag = {
    appName: '',
    appVersion: '',
    activationCode: '',

    PAYMENT_CREDIT: PlugPagModule.PAYMENT_CREDIT,
    PAYMENT_DEBIT: PlugPagModule.PAYMENT_DEBIT,
    PAYMENT_VOUCHER: PlugPagModule.PAYMENT_VOUCHER,

    INSTALLMENT_TYPE_A_VISTA: PlugPagModule.INSTALLMENT_TYPE_A_VISTA,
    INSTALLMENT_TYPE_PARC_VENDEDOR: PlugPagModule.INSTALLMENT_TYPE_PARC_VENDEDOR,

    setAppInfo(appName = '', appVersion = '') {
        this.appName = appName;
        this.appVersion = appVersion;
    },

    setActivationCode(activationCode = '') {
        this.activationCode = activationCode;
    },

   
    initializePlugPag() {
        if (!this.appName || !this.appVersion) {
            throw new Error('You must set appName and appVersion before call isAuthenticated method.');
        }

        return PlugPagModule.initializePlugPag({ appName: this.appName, appVersion: this.appVersion });
    },
   async initializeAndActivatePinpad() {
        if (!this.appName || !this.appVersion) {
            throw new Error('You must set appName and appVersion before call isAuthenticated method.');
        }
        return PlugPagModule.initializeAndActivatePinpad({ activationCode: this.activationCode });
    },
    getLibVersion() {
        if (!this.appName || !this.appVersion) {
            throw new Error('You must set appName and appVersion before call isAuthenticated method.');
        }

        return PlugPagModule.getLibVersion();
    },

    async isAuthenticated() {
        if (!this.appName || !this.appVersion) {
            throw new Error('You must set appName and appVersion before call isAuthenticated method.');
        }

        return PlugPagModule.isAuthenticated();
    },
    async doPaymentCreditCrad(request) {

        if (!this.appName || !this.appVersion || !this.activationCode) {
            throw new Error('You must set appName, appVersion and activationCode before call checkout method.');
        }

        return PlugPagModule.doPaymentCreditCrad({ ...request, activationCode: this.activationCode })

    }
};



