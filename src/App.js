import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';

import { RNPlugPag } from './modules/PluPagModule';


const App = () => {

  async function handleApplicationPayment() {
    RNPlugPag.setAppInfo('AppDemo', '1.0.7');
    RNPlugPag.setActivationCode('403938');

    const { isAuthenticated } = await RNPlugPag.isAuthenticated();

    Alert.alert('Autenticação do usuário',
      ` Usuário autenticado : ${isAuthenticated ? "Sim" : "Não"}`
    )

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
        <Text>Usuário está autenticado?</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;