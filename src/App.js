import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';

import { RNPlugPag } from './modules/PluPagModule';


const App = () => {

  async function handleApplicationPayment() {
    RNPlugPag.setAppInfo('AppDemo','1.0.7');
    RNPlugPag.setActivationCode('403938');

    const isAuthenticated = await RNPlugPag.isAuthenticated();

    if (isAuthenticated) {
      
      try {
        const request = {
          salesCode: 'RNPlugPag',
          amount: '200',
        }
        await RNPlugPag.doPaymentCreditCrad(request);

        Alert.alert("Sucesso","Pagamento realizado com sucesso!!")
      } catch (error) {
        Alert.alert("Ops", "Não foi possível realizar o pagamento")
      }

    } else {
      Alert.alert(
        'Autenticação inválida',
        'Usuário não autenticado'
      );
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