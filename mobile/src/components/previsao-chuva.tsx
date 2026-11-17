import { useEffect, useState } from 'react';
import { Linking, Pressable, StyleSheet, Text, View } from 'react-native';

type Previsao = {
  local: string;
  dataPrevisao: string;
  precipitacaoPrevistaMm: number;
  probabilidadeMaximaChuvaPercentual: number;
  consultadoEm: string;
  desatualizado: boolean;
};

const API_URL =
  process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

export default function PrevisaoChuva() {
  const [previsao, setPrevisao] = useState<Previsao | null>(null);
  const [falhou, setFalhou] = useState(false);

  useEffect(() => {
    let ativo = true;
    let proxima: ReturnType<typeof setTimeout> | undefined;
    let limite: ReturnType<typeof setTimeout> | undefined;
    let controller: AbortController | undefined;

    async function consultar() {
      controller = new AbortController();
      limite = setTimeout(() => controller?.abort(), 12000);

      try {
        const resposta = await fetch(`${API_URL}/api/previsao`, {
          signal: controller.signal,
          cache: 'no-store',
        });

        if (!resposta.ok) {
          throw new Error('Previsão indisponível');
        }

        const dados: Previsao = await resposta.json();

        if (
          !dados.dataPrevisao ||
          !Number.isFinite(dados.precipitacaoPrevistaMm) ||
          !Number.isFinite(dados.probabilidadeMaximaChuvaPercentual)
        ) {
          throw new Error('Previsão inválida');
        }

        if (ativo) {
          setPrevisao(dados);
          setFalhou(false);
        }
      } catch {
        if (ativo) setFalhou(true);
      } finally {
        clearTimeout(limite);

        if (ativo) {
          proxima = setTimeout(consultar, 60000);
        }
      }
    }

    consultar();

    return () => {
      ativo = false;
      clearTimeout(proxima);
      clearTimeout(limite);
      controller?.abort();
    };
  }, []);

  return (
    <View style={styles.cartao}>
      <Text style={styles.selo}>FONTE PÚBLICA • PREVISÃO METEOROLÓGICA</Text>
      <Text style={styles.titulo}>Chuva prevista em São Paulo</Text>

      {!previsao ? (
        <Text style={styles.texto}>
          {falhou
            ? 'Previsão indisponível. Tentaremos consultar novamente.'
            : 'Consultando a previsão…'}
        </Text>
      ) : (
        <>
          <Text style={styles.texto}>
            Data: {previsao.dataPrevisao.split('-').reverse().join('/')}
          </Text>

          <Text style={styles.valor}>
            {previsao.precipitacaoPrevistaMm.toLocaleString('pt-BR')} mm
          </Text>

          <Text style={styles.texto}>
            Precipitação total prevista para o dia.
          </Text>

          <Text style={styles.texto}>
            Probabilidade máxima de chuva:{' '}
            {previsao.probabilidadeMaximaChuvaPercentual}%
          </Text>

          {(falhou || previsao.desatualizado) && (
            <Text style={styles.aviso}>
              Não foi possível atualizar. Confira a data da previsão
              e o horário da última consulta.
            </Text>
          )}

          <Text style={styles.detalhe}>
            Consultada em:{' '}
            {new Date(previsao.consultadoEm).toLocaleString('pt-BR')}
          </Text>
        </>
      )}

      <Text style={styles.detalhe}>
        Referência municipal. Não confirma alagamento nos pontos
        fictícios. A probabilidade de chuva não representa a confiança
        do dado; essa confiança não foi quantificada.
      </Text>

      <Pressable
        accessibilityRole="link"
        onPress={() => {
          Linking.openURL('https://open-meteo.com/').catch(() => {});
        }}
      >
        <Text style={styles.link}>Dados meteorológicos: Open-Meteo ↗</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  cartao: {
    backgroundColor: '#EAF2FA',
    padding: 20,
    borderRadius: 16,
    gap: 10,
  },
  selo: {
    color: '#24547A',
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 0.5,
  },
  titulo: { color: '#16344C', fontSize: 20, fontWeight: '700' },
  valor: { color: '#16344C', fontSize: 30, fontWeight: '800' },
  texto: { color: '#334155', fontSize: 14, lineHeight: 21 },
  detalhe: { color: '#475569', fontSize: 12, lineHeight: 19 },
  aviso: { color: '#92400E', fontSize: 13, lineHeight: 20 },
  link: { color: '#175684', fontSize: 13, textDecorationLine: 'underline' },
});