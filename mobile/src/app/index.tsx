import { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import FormularioRelato from '@/components/formulario-relato';
import PrevisaoChuva from '@/components/previsao-chuva';
import AvisoCritico from '@/components/aviso-critico';

type Alerta = {
  pontoId: number;
  nomePonto: string;
  bairro: string;
  medicaoId: number | null;
  nivelAguaCm: number | null;
  horario: string | null;
  origem: string | null;
  confianca: number | null;
  classificacao: string;
  pontuacao: number;
  motivo: string;
  desatualizado: boolean;
  simulado: boolean;
};

const API_URL =
  process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

const categorias: Record<string, { nome: string; cor: string }> = {
  CRITICO: { nome: 'Crítico', cor: '#B42318' },
  ALTO: { nome: 'Alto', cor: '#9A3412' },
  ATENCAO: { nome: 'Atenção', cor: '#854D0E' },
  BAIXO: { nome: 'Baixo', cor: '#166534' },
  SEM_DADOS: { nome: 'Sem dados', cor: '#475569' },
};

export default function HomeScreen() {
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  const [conexao, setConexao] = useState<
    'conectando' | 'online' | 'offline'
  >('conectando');
  const [ultimaConsulta, setUltimaConsulta] = useState<Date | null>(null);
  const [agora, setAgora] = useState(Date.now());

  useEffect(() => {
    let ativo = true;
    let proximaConsulta: ReturnType<typeof setTimeout> | undefined;
    let limite: ReturnType<typeof setTimeout> | undefined;
    let requisicao: AbortController | undefined;

    async function atualizar() {
      const controller = new AbortController();
      requisicao = controller;
      limite = setTimeout(() => controller.abort(), 4000);

      try {
        const resposta = await fetch(`${API_URL}/api/alertas`, {
          signal: controller.signal,
          headers: { Accept: 'application/json' },
          cache: 'no-store',
        });

        if (!resposta.ok) {
          throw new Error('Falha ao consultar os alertas');
        }

        const dados = await resposta.json();

        if (!Array.isArray(dados)) {
          throw new Error('Resposta inesperada');
        }

        if (ativo) {
          setAlertas(dados);
          setConexao('online');
          setUltimaConsulta(new Date());
        }
      } catch {
        if (ativo) {
          // Preserva os últimos dados enquanto tenta reconectar.
          setConexao('offline');
        }
      } finally {
        clearTimeout(limite);

        if (ativo) {
          proximaConsulta = setTimeout(atualizar, 5000);
        }
      }
    }

    atualizar();

    const relogio = setInterval(() => setAgora(Date.now()), 1000);

    return () => {
      ativo = false;
      clearTimeout(proximaConsulta);
      clearTimeout(limite);
      clearInterval(relogio);
      requisicao?.abort();
    };
  }, []);

  const offline = conexao === 'offline';

  const criticos = alertas.filter(
    (alerta) => alerta.classificacao === 'CRITICO'
  ).length;

  return (
    <SafeAreaView style={styles.tela}>
      <ScrollView
        contentContainerStyle={styles.conteudo}
        keyboardShouldPersistTaps="handled"
      >
        <Text style={styles.selo}>
          PROJETO ACADÊMICO • PONTOS E SENSORES SIMULADOS
        </Text>

        <Text style={styles.titulo}>Cota de Rua</Text>

        <Text style={styles.subtitulo}>
          Acompanhe os pontos de alagamento do bairro.
        </Text>

        <View style={[styles.conexao, offline && styles.semConexao]}>
          <Text
            accessibilityLiveRegion="polite"
            style={styles.conexaoTitulo}
          >
            {conexao === 'conectando'
              ? 'Conectando ao servidor…'
              : offline
                ? 'Sem conexão com o servidor'
                : 'Conectado • atualização automática'}
          </Text>

          <Text style={styles.texto}>
            {offline
              ? 'Tentando reconectar. Os dados abaixo podem estar desatualizados.'
              : 'Consulta dos alertas a cada 5 segundos após a resposta.'}
          </Text>

          <Text style={styles.detalhe}>
            Última consulta com sucesso:{' '}
            {ultimaConsulta
              ? ultimaConsulta.toLocaleTimeString('pt-BR')
              : 'nenhuma'}
          </Text>
        </View>

        <View style={styles.resumo}>
          <View style={styles.indicador}>
            <Text style={styles.numero}>{alertas.length}</Text>
            <Text style={styles.texto}>Pontos recebidos</Text>
          </View>

          <View style={styles.indicador}>
            <Text style={[styles.numero, { color: '#B42318' }]}>
              {criticos}
            </Text>
            <Text style={styles.texto}>
              Críticos no último registro
            </Text>
          </View>
        </View>

        <AvisoCritico alertas={alertas} online={conexao === 'online'} />
<PrevisaoChuva />

        <FormularioRelato
          pontos={alertas}
          offline={conexao !== 'online'}
        />

        <Text style={styles.secao}>
          Prioridade de acompanhamento
        </Text>

        {conexao === 'conectando' && (
          <ActivityIndicator size="large" color="#115E59" />
        )}

        {alertas.length === 0 && conexao !== 'conectando' && (
          <Text style={styles.texto}>
            {offline
              ? 'Nenhum dado disponível. Aguardando conexão.'
              : 'Nenhum ponto monitorado cadastrado.'}
          </Text>
        )}

        {alertas.map((alerta) => {
          const categoria =
            categorias[alerta.classificacao] || categorias.SEM_DADOS;

          const medicaoAntiga =
            alerta.desatualizado ||
            (alerta.horario !== null &&
              agora - new Date(alerta.horario).getTime() > 300000);

          return (
            <View
              key={alerta.pontoId}
              style={[
                styles.cartao,
                { borderLeftColor: categoria.cor },
              ]}
            >
              <View style={styles.linha}>
                <Text
                  style={[
                    styles.classificacao,
                    { color: categoria.cor },
                  ]}
                >
                  {categoria.nome}
                </Text>

                <Text style={styles.detalhe}>
                  {alerta.pontuacao} pontos
                </Text>
              </View>

              <Text style={styles.nome}>{alerta.nomePonto}</Text>
              <Text style={styles.detalhe}>{alerta.bairro}</Text>

              <Text style={styles.nivel}>
                {alerta.nivelAguaCm === null
                  ? 'Sem medição'
                  : `${alerta.nivelAguaCm.toLocaleString('pt-BR')} cm`}
              </Text>

              <Text style={styles.texto}>{alerta.motivo}</Text>

              {(offline || medicaoAntiga) && (
                <Text style={styles.aviso}>
                  {offline
                    ? 'Sem conexão: situação atual não confirmada.'
                    : 'Medição antiga: aguardando novos dados.'}
                </Text>
              )}

              <View style={styles.fonte}>
                <Text style={styles.detalhe}>
                  Origem:{' '}
                  {alerta.origem === 'SENSOR_SIMULADO'
                    ? 'Sensor simulado'
                    : alerta.origem || 'Sem registro'}
                </Text>

                <Text style={styles.detalhe}>
                  Confiança:{' '}
                  {alerta.confianca === null
                    ? 'não informada'
                    : `${alerta.confianca}/100 — pontuação do protótipo`}
                </Text>

                <Text style={styles.detalhe}>
                  Medição:{' '}
                  {alerta.horario
                    ? new Date(alerta.horario).toLocaleString('pt-BR')
                    : 'não recebida'}
                </Text>
              </View>
            </View>
          );
        })}

        <Text style={styles.rodape}>
          Leonardo Veleiro • RM 557773{'\n'}
          Demonstração acadêmica. Não representa alertas oficiais.
        </Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  tela: {
    flex: 1,
    backgroundColor: '#F3F6F4',
  },
  conteudo: {
    width: '100%',
    maxWidth: 800,
    alignSelf: 'center',
    padding: 24,
    paddingBottom: 100,
    gap: 16,
  },
  selo: {
    color: '#115E59',
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1,
  },
  titulo: {
    color: '#142D29',
    fontSize: 36,
    fontWeight: '800',
  },
  subtitulo: {
    color: '#475569',
    fontSize: 16,
    lineHeight: 24,
  },
  conexao: {
    backgroundColor: '#DCFCE7',
    borderRadius: 16,
    padding: 18,
    gap: 8,
  },
  semConexao: {
    backgroundColor: '#FEF3C7',
  },
  conexaoTitulo: {
    color: '#1E293B',
    fontSize: 16,
    fontWeight: '700',
  },
  texto: {
    color: '#334155',
    fontSize: 14,
    lineHeight: 21,
  },
  detalhe: {
    color: '#475569',
    fontSize: 12,
    lineHeight: 19,
  },
  resumo: {
    flexDirection: 'row',
    gap: 12,
    flexWrap: 'wrap',
  },
  indicador: {
    flex: 1,
    minWidth: 130,
    backgroundColor: '#FFFFFF',
    padding: 18,
    borderRadius: 16,
    gap: 6,
  },
  numero: {
    color: '#115E59',
    fontSize: 32,
    fontWeight: '800',
  },
  secao: {
    color: '#142D29',
    fontSize: 20,
    fontWeight: '700',
    marginTop: 8,
  },
  cartao: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    borderLeftWidth: 5,
    padding: 20,
    gap: 8,
  },
  linha: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  classificacao: {
    fontSize: 14,
    fontWeight: '800',
  },
  nome: {
    color: '#142D29',
    fontSize: 20,
    fontWeight: '700',
  },
  nivel: {
    color: '#142D29',
    fontSize: 30,
    fontWeight: '800',
    marginVertical: 4,
  },
  aviso: {
    color: '#92400E',
    fontSize: 13,
    fontWeight: '700',
    lineHeight: 20,
  },
  fonte: {
    borderTopWidth: 1,
    borderTopColor: '#E2E8F0',
    marginTop: 6,
    paddingTop: 12,
    gap: 3,
  },
  rodape: {
    color: '#64748B',
    fontSize: 12,
    textAlign: 'center',
    lineHeight: 20,
  },
});