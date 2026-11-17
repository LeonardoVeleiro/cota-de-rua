import { useRef, useState } from 'react';
import {
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

type Props = {
  pontos: { pontoId: number; nomePonto: string }[];
  offline: boolean;
};

const API_URL =
  process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

export default function FormularioRelato({ pontos, offline }: Props) {
  const [pontoId, setPontoId] = useState<number | null>(null);
  const [descricao, setDescricao] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [mensagem, setMensagem] = useState('');
  const envioEmAndamento = useRef(false);

  async function enviar() {
    if (envioEmAndamento.current) return;

    const texto = descricao.trim();

    if (pontoId === null) {
      setMensagem('Selecione o ponto da ocorrência.');
      return;
    }

    if (texto.length < 10 || texto.length > 500) {
      setMensagem('Escreva uma descrição entre 10 e 500 caracteres.');
      return;
    }

    if (offline) {
      setMensagem('Aguarde a conexão voltar para enviar.');
      return;
    }

    envioEmAndamento.current = true;
    setEnviando(true);
    setMensagem('');

    const controller = new AbortController();
    const limite = setTimeout(() => controller.abort(), 8000);

    try {
      const resposta = await fetch(`${API_URL}/api/relatos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pontoId, descricao: texto }),
        signal: controller.signal,
      });

      if (!resposta.ok) {
        setMensagem(
          `O servidor recusou o envio (${resposta.status}). Revise os dados.`
        );
        return;
      }

      setDescricao('');
      setMensagem('Relato registrado! Ele ainda não foi verificado.');
    } catch {
      setMensagem(
        'Não foi possível confirmar o envio. O texto foi mantido. ' +
        'Confira o histórico antes de reenviar para evitar duplicidade.'
      );
    } finally {
      clearTimeout(limite);
      envioEmAndamento.current = false;
      setEnviando(false);
    }
  }

  return (
    <View style={styles.caixa}>
      <Text style={styles.titulo}>O que você observou na rua?</Text>
      <Text style={styles.texto}>
        Selecione o local e descreva a ocorrência. Não inclua nomes,
        telefones ou outros dados pessoais.
      </Text>

      <View style={styles.opcoes}>
        {pontos.map((ponto) => (
          <Pressable
            key={ponto.pontoId}
            accessibilityRole="button"
            accessibilityState={{
              selected: pontoId === ponto.pontoId,
              disabled: enviando,
            }}
            disabled={enviando}
            onPress={() => {
              setPontoId(ponto.pontoId);
              setMensagem('');
            }}
            style={[
              styles.opcao,
              pontoId === ponto.pontoId && styles.selecionada,
            ]}
          >
            <Text
              style={[
                styles.nomeOpcao,
                pontoId === ponto.pontoId && styles.nomeSelecionado,
              ]}
            >
              {ponto.nomePonto}
            </Text>
          </Pressable>
        ))}
      </View>

      {pontos.length === 0 && (
        <Text style={styles.texto}>
          Aguardando a lista de pontos monitorados.
        </Text>
      )}

      <TextInput
        accessibilityLabel="Descrição da ocorrência"
        placeholder="Ex.: A água está acumulando na entrada das lojas."
        placeholderTextColor="#64748B"
        multiline
        maxLength={500}
        editable={!enviando}
        value={descricao}
        onChangeText={setDescricao}
        style={styles.campo}
      />

      <Text style={styles.contador}>{descricao.length}/500 caracteres</Text>

      <Pressable
        accessibilityRole="button"
        accessibilityState={{
          disabled: enviando || offline || pontos.length === 0,
        }}
        disabled={enviando || offline || pontos.length === 0}
        onPress={enviar}
        style={[
          styles.botao,
          (enviando || offline || pontos.length === 0) && styles.desabilitado,
        ]}
      >
        <Text style={styles.textoBotao}>
          {enviando
            ? 'Enviando…'
            : offline
              ? 'Aguardando conexão'
              : 'Enviar relato'}
        </Text>
      </Pressable>

      {mensagem !== '' && (
        <Text accessibilityLiveRegion="polite" style={styles.mensagem}>
          {mensagem}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  caixa: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    borderRadius: 16,
    gap: 12,
  },
  titulo: { color: '#142D29', fontSize: 20, fontWeight: '700' },
  texto: { color: '#475569', fontSize: 14, lineHeight: 21 },
  opcoes: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  opcao: {
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#CBD5E1',
    backgroundColor: '#F8FAFC',
  },
  selecionada: { backgroundColor: '#115E59', borderColor: '#115E59' },
  nomeOpcao: { color: '#334155', fontSize: 13, fontWeight: '600' },
  nomeSelecionado: { color: '#FFFFFF' },
  campo: {
    minHeight: 110,
    borderWidth: 1,
    borderColor: '#94A3B8',
    borderRadius: 10,
    padding: 12,
    color: '#142D29',
    fontSize: 15,
    textAlignVertical: 'top',
  },
  contador: { color: '#64748B', fontSize: 12, textAlign: 'right' },
  botao: {
    backgroundColor: '#115E59',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
  },
  desabilitado: { opacity: 0.5 },
  textoBotao: { color: '#FFFFFF', fontWeight: '700', fontSize: 15 },
  mensagem: { color: '#334155', fontSize: 14, lineHeight: 21 },
});