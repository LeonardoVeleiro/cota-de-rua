import { useEffect, useRef, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

type Alerta = {
  pontoId: number;
  nomePonto: string;
  medicaoId: number | null;
  nivelAguaCm: number | null;
  horario: string | null;
  classificacao: string;
  desatualizado: boolean;
};

type Props = {
  alertas: Alerta[];
  online: boolean;
};

export default function AvisoCritico({ alertas, online }: Props) {
  const vistos = useRef(new Set<number>());
  const iniciou = useRef(false);
  const [avisos, setAvisos] = useState<Alerta[]>([]);

  useEffect(() => {
    if (!online) return;

    // Registra as medições que já existiam ao abrir o painel.
    if (!iniciou.current) {
      for (const alerta of alertas) {
        if (alerta.medicaoId !== null) {
          vistos.current.add(alerta.medicaoId);
        }
      }

      iniciou.current = true;
      return;
    }

    const novos: Alerta[] = [];

    for (const alerta of alertas) {
      if (
        alerta.medicaoId === null ||
        vistos.current.has(alerta.medicaoId)
      ) {
        continue;
      }

      // Evita repetir o aviso nas próximas consultas.
      vistos.current.add(alerta.medicaoId);

      // O backend determina se a medição está atualizada.
      if (
        alerta.classificacao === 'CRITICO' &&
        !alerta.desatualizado
      ) {
        novos.push(alerta);
      }
    }

    if (novos.length > 0) {
      setAvisos((anteriores) =>
        [...novos, ...anteriores].slice(0, 5)
      );
    }
  }, [alertas, online]);

  if (avisos.length === 0) {
    return null;
  }

  return (
    <View
      style={styles.caixa}
      accessibilityLiveRegion="assertive"
    >
      <Text style={styles.titulo}>
        Nova medição crítica recebida
      </Text>

      {avisos.map((aviso) => (
        <View key={aviso.medicaoId} style={styles.item}>
          <Text style={styles.nome}>
            {aviso.nomePonto}
          </Text>

          <Text style={styles.texto}>
            Nível registrado:{' '}
            {aviso.nivelAguaCm?.toLocaleString('pt-BR')} cm.
          </Text>

          <Text style={styles.texto}>
            {aviso.horario
              ? new Date(aviso.horario).toLocaleString('pt-BR')
              : ''}
          </Text>

          <Pressable
            accessibilityRole="button"
            accessibilityLabel={
              `Dispensar aviso de ${aviso.nomePonto}`
            }
            onPress={() =>
              setAvisos((anteriores) =>
                anteriores.filter(
                  (item) => item.medicaoId !== aviso.medicaoId
                )
              )
            }
            style={styles.botao}
          >
            <Text style={styles.textoBotao}>
              Entendi
            </Text>
          </Pressable>
        </View>
      ))}

      <Text style={styles.texto}>
        Aviso da demonstração. Confira a atualização do ponto
        no painel.
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  caixa: {
    backgroundColor: '#FEE2E2',
    borderColor: '#B42318',
    borderWidth: 1,
    borderRadius: 16,
    padding: 20,
    gap: 12,
  },
  titulo: {
    color: '#991B1B',
    fontSize: 20,
    fontWeight: '800',
  },
  item: {
    gap: 6,
  },
  nome: {
    color: '#7F1D1D',
    fontSize: 16,
    fontWeight: '700',
  },
  texto: {
    color: '#7F1D1D',
    fontSize: 13,
    lineHeight: 20,
  },
  botao: {
    alignSelf: 'flex-start',
    backgroundColor: '#991B1B',
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: 8,
    marginTop: 4,
  },
  textoBotao: {
    color: '#FFFFFF',
    fontWeight: '700',
  },
});