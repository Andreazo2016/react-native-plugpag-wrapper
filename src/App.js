import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';

import { RNPlugPag } from './modules/PluPagModule';


const App = () => {

  async function handleApplicationPayment() {
    RNPlugPag.setAppInfo('AppDemo', '1.0.7');
    RNPlugPag.setActivationCode('403938');

    try {

      const { isAuthenticated } = await RNPlugPag.isAuthenticated();

      if (isAuthenticated) {
        await RNPlugPag.doPaymentCreditCrad({ amount: "200", salesCode: 'RNPlugPag' });
        Alert.alert("Sucesso","Pagamento realizado com sucesso!!");
      } else {
        Alert.alert('Falha na autentticação', 'Usuário não autenticado!!');
      }

    } catch (error) {
      Alert.alert('Erro', error.message);
    }
  }

  return (
    <View
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <TouchableOpacity
        style={{
          backgroundColor: '#ff9000',
          padding: 16,
          borderRadius: 10
        }}
        onPress={handleApplicationPayment}
      >
        <Text>Realizar pagamento de R$ 200</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;