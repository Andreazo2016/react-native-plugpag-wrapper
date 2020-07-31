import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

import PlugPageModule from './modules/PluPagModule';


const App = () => {

  async function handleAuthentication() {
    PlugPageModule.getApplicationCode().then((result) => {

      console.log(result); 
    })
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
        onPress={handleAuthentication}
      >
        <Text>Desafio de React Native</Text>
      </TouchableOpacity>
    </View>
  )
}

export default App;