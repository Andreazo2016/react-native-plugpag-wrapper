import React, { useCallback, useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';

import { RNPlugPag, EventEmitter } from './modules/PluPagModule';


const App = () => {




  const handleApplicationPayment = useCallback(async () => {

    try {
      RNPlugPag.setAppInfo('AppDemo', '1.0.7');
      RNPlugPag.setActivationCode('403938');
      RNPlugPag.initializePlugPag();
      const { sucess } = await RNPlugPag.initializeAndActivatePinpad();

      if (!sucess) {
        Alert.alert('Ops', 'Não foi possível iniciar a maquininha!!');
        return;
      }

      const { isAuthenticated } = await RNPlugPag.isAuthenticated();

      if (isAuthenticated) {
        await RNPlugPag.doPaymentCreditCrad({ amount: "200", salesCode: 'RNPlugPag' });
        Alert.alert("Sucesso", "Pagamento realizado com sucesso!!");
      } else {
        Alert.alert('Falha na autentticação', 'Usuário não autenticado!!');
      }

    } catch (error) {
      Alert.alert('Erro', error.message);
    }
  }, [])

  const handlePayment = useCallback(() => {
    Alert.alert(
      'Aviso',
      'Antes de concluir pagamento,\n Insira o cartão na maquininha!!',
      [
        {
          text: 'cancelar',
          onPress: () => { },
          style: 'cancel'
        },
        { text: 'confirmar', onPress: handleApplicationPayment }
      ]
    );
  }, [])


  useState(() => {
    EventEmitter.addListener('paymentEvent',(event) => {
      console.log(event)
    })
   }, [EventEmitter])
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
        onPress={handlePayment}
      >
        <Text>Realizar pagamento de R$ 2</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;