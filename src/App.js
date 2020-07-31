import React,{useState} from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

import PlugPageModule from './modules/PluPagModule';


const App = () => {

  const [applicationVersion,setApplicationVersion] = useState('');

  async function handleApplicationVersion() {
    const response = await PlugPageModule.getApplicationVersion();
    setApplicationVersion(response);
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
        onPress={handleApplicationVersion}
      >
        <Text>Obter a versão da aplicação</Text>
      </TouchableOpacity>
    <Text>{applicationVersion}</Text>
    </View>
  )
}

export default App;