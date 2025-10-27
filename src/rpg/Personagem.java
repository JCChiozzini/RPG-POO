package rpg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Classe mae: aqui fica tudo que herois e inimigos dividem.
public abstract class Personagem implements Cloneable {
    private static final Random RANDOM = new Random();

    private String nome;
    private int pontosVida;
    private int pontosVidaMaximo;
    private int ataque;
    private int defesa;
    private int nivel;
    private Inventario inventario;
    private int experiencia;
    private int escudoTemporario;
    private boolean esquivaProximoGolpe;

    protected Personagem() {
        this("Sem Nome", 10, 10, 2, 1, 1, new Inventario());
    }

    protected Personagem(String nome, int pontosVida, int pontosVidaMaximo, int ataque, int defesa, int nivel, Inventario inventario) {
        this.nome = nome;
        this.pontosVidaMaximo = Math.max(1, pontosVidaMaximo);
        this.pontosVida = Math.min(this.pontosVidaMaximo, Math.max(0, pontosVida));
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.inventario = inventario == null ? new Inventario() : inventario;
        this.experiencia = 0;
        this.escudoTemporario = 0;
        this.esquivaProximoGolpe = false;
    }

    protected Personagem(Personagem other) {
        this(
                other.nome,
                other.pontosVida,
                other.pontosVidaMaximo,
                other.ataque,
                other.defesa,
                other.nivel,
                other.inventario == null ? null : other.inventario.clone()
        );
        this.experiencia = other.experiencia;
        this.escudoTemporario = other.escudoTemporario;
        this.esquivaProximoGolpe = other.esquivaProximoGolpe;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    // Mantem a vida na faixa certa e bloqueia valores negativos.
    public void setPontosVida(int pontosVida) {
        this.pontosVida = Math.max(0, Math.min(pontosVidaMaximo, pontosVida));
    }

    public int getPontosVidaMaximo() {
        return pontosVidaMaximo;
    }

    public void setPontosVidaMaximo(int pontosVidaMaximo) {
        this.pontosVidaMaximo = Math.max(1, pontosVidaMaximo);
        if (pontosVida > this.pontosVidaMaximo) {
            pontosVida = this.pontosVidaMaximo;
        }
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = Math.max(0, ataque);
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        this.defesa = Math.max(0, defesa);
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario == null ? new Inventario() : inventario;
    }

    // Joga itens direto no inventario sem entregar a lista pra fora.
    public void adicionarItemAoInventario(Item item) {
        inventario.adicionarItem(item);
    }

    public boolean removerItemDoInventario(Item item, int quantidade) {
        return inventario.removerItem(item, quantidade);
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getExperienciaParaProximoNivel() {
        return calcularExperienciaParaProximoNivel();
    }

    // Soma XP e devolve mensagens se rolar level up.
    public List<String> ganharExperiencia(int pontos) {
        List<String> mensagens = new ArrayList<>();
        if (pontos <= 0) {
            return mensagens;
        }
        experiencia += pontos;
        while (experiencia >= calcularExperienciaParaProximoNivel()) {
            experiencia -= calcularExperienciaParaProximoNivel();
            nivel++;
            pontosVidaMaximo += 5;
            ataque += 1;
            defesa += 1;
            pontosVida = pontosVidaMaximo;
            mensagens.add(nome + " alcancou o nivel " + nivel + "!");
            mensagens.add("HP maximo agora: " + pontosVidaMaximo + ", Ataque: " + ataque + ", Defesa: " + defesa + ".");
        }
        return mensagens;
    }

    private int calcularExperienciaParaProximoNivel() {
        return 100 + (nivel - 1) * 50;
    }

    public int getEscudoTemporario() {
        return escudoTemporario;
    }

    public boolean possuiEsquivaPreparada() {
        return esquivaProximoGolpe;
    }

    protected void adicionarEscudoTemporario(int valor) {
        if (valor <= 0) {
            return;
        }
        escudoTemporario += valor;
    }

    protected void ativarEsquivaProximoGolpe() {
        esquivaProximoGolpe = true;
    }

    private boolean consumirEsquiva() {
        if (esquivaProximoGolpe) {
            esquivaProximoGolpe = false;
            return true;
        }
        return false;
    }

    public boolean estaVivo() {
        return pontosVida > 0;
    }

    public void receberDano(int dano) {
        int danoRestante = Math.max(dano, 0);
        if (escudoTemporario > 0 && danoRestante > 0) {
            int absorvido = Math.min(escudoTemporario, danoRestante);
            escudoTemporario -= absorvido;
            danoRestante -= absorvido;
        }
        setPontosVida(pontosVida - danoRestante);
    }

    // Cura, mas sem ultrapassar o teto de HP.
    public void curar(int valor) {
        setPontosVida(pontosVida + Math.max(0, valor));
    }

    // Dado de seis lados nosso de cada turno.
    protected int rolarDadoSeisLados() {
        return RANDOM.nextInt(6) + 1;
    }

    // Resolve um turno inteiro de troca de golpes padroes.
    public TurnoCombate executarTurno(Inimigo inimigo) {
        if (inimigo == null) {
            throw new IllegalArgumentException("Inimigo nao pode ser nulo");
        }
        StringBuilder log = new StringBuilder();

        int rolagemJogador = rolarDadoSeisLados();
        int poderAtaqueJogador = getAtaque() + rolagemJogador;
        log.append(nome).append(" rola ").append(rolagemJogador)
                .append(" -> poder de ataque ").append(poderAtaqueJogador).append(".\n");
        if (poderAtaqueJogador > inimigo.getDefesa()) {
            int dano = Math.max(1, poderAtaqueJogador - inimigo.getDefesa());
            inimigo.receberDano(dano);
            log.append(nome).append(" acerta e causa ").append(dano)
                    .append(" de dano. HP de ").append(inimigo.getNome())
                    .append(": ").append(inimigo.getPontosVida()).append("/")
                    .append(inimigo.getPontosVidaMaximo()).append(".\n");
        } else {
            log.append(nome).append(" erra o ataque.\n");
        }

        if (!inimigo.estaVivo()) {
            log.append(inimigo.getNome()).append(" nao consegue continuar.\n");
            return new TurnoCombate(log.toString(), estaVivo(), false);
        }

        int rolagemInimigo = inimigo.rolarDadoSeisLados();
        int poderAtaqueInimigo = inimigo.getAtaque() + rolagemInimigo;
        log.append(inimigo.getNome()).append(" rola ").append(rolagemInimigo)
                .append(" -> poder de ataque ").append(poderAtaqueInimigo).append(".\n");
        if (poderAtaqueInimigo > getDefesa()) {
            if (consumirEsquiva()) {
                log.append(nome).append(" esquiva do contra-ataque!\n");
            } else {
                int dano = Math.max(1, poderAtaqueInimigo - getDefesa());
                int vidaAntes = getPontosVida();
                receberDano(dano);
                int danoFinal = Math.max(0, vidaAntes - getPontosVida());
                int absorvido = Math.max(0, dano - danoFinal);
                if (danoFinal > 0) {
                    log.append(inimigo.getNome()).append(" acerta e causa ").append(danoFinal)
                            .append(" de dano. HP de ").append(nome).append(": ")
                            .append(getPontosVida()).append("/").append(getPontosVidaMaximo()).append(".\n");
                } else {
                    log.append(inimigo.getNome()).append(" acerta, mas voce nao sofre dano.\n");
                }
                if (absorvido > 0) {
                    log.append("O escudo absorve ").append(absorvido).append(" de dano.\n");
                }
            }
        } else {
            log.append(inimigo.getNome()).append(" erra o ataque.\n");
        }

        if (!estaVivo()) {
            log.append(nome).append(" cai em combate.\n");
        }
        return new TurnoCombate(log.toString(), estaVivo(), inimigo.estaVivo());
    }

    // Log bonitinho do contra-ataque basico do inimigo.
    public String executarContraAtaque(Inimigo inimigo) {
        if (inimigo == null) {
            throw new IllegalArgumentException("Inimigo nao pode ser nulo");
        }
        if (!inimigo.estaVivo()) {
            return inimigo.getNome() + " nao tem forcas para responder.\n";
        }
        StringBuilder log = new StringBuilder();
        int rolagemInimigo = inimigo.rolarDadoSeisLados();
        int poderAtaqueInimigo = inimigo.getAtaque() + rolagemInimigo;
        log.append(inimigo.getNome()).append(" contra-ataca rolando ").append(rolagemInimigo)
                .append(" -> poder ").append(poderAtaqueInimigo).append(".\n");
        if (poderAtaqueInimigo > getDefesa()) {
            if (consumirEsquiva()) {
                log.append(nome).append(" evita o golpe com um passo rapido.\n");
            } else {
                int dano = Math.max(1, poderAtaqueInimigo - getDefesa());
                int vidaAntes = getPontosVida();
                receberDano(dano);
                int danoFinal = Math.max(0, vidaAntes - getPontosVida());
                int absorvido = Math.max(0, dano - danoFinal);
                if (danoFinal > 0) {
                    log.append("O golpe acerta causando ").append(danoFinal).append(" de dano. HP de ")
                            .append(nome).append(": ").append(getPontosVida()).append("/")
                            .append(getPontosVidaMaximo()).append(".\n");
                } else {
                    log.append("O golpe acerta, mas nao causa dano.\n");
                }
                if (absorvido > 0) {
                    log.append("O escudo absorve ").append(absorvido).append(" de dano.\n");
                }
            }
        } else {
            log.append("O ataque nao supera a defesa de ").append(nome).append(".\n");
        }

        if (!estaVivo()) {
            log.append(nome).append(" nao resiste ao contra-ataque.\n");
        }
        return log.toString();
    }

    public abstract void usarHabilidadeEspecial(Personagem alvo);

    @Override
    public Personagem clone() {
        try {
            Personagem copia = (Personagem) super.clone();
            copia.inventario = inventario == null ? null : inventario.clone();
            copia.experiencia = this.experiencia;
            copia.escudoTemporario = this.escudoTemporario;
            copia.esquivaProximoGolpe = this.esquivaProximoGolpe;
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone nao suportado", e);
        }
    }

    @Override
    public String toString() {
        return "Personagem{" +
                "nome='" + nome + '\'' +
                ", pontosVida=" + pontosVida +
                ", pontosVidaMaximo=" + pontosVidaMaximo +
                ", ataque=" + ataque +
                ", defesa=" + defesa +
                ", nivel=" + nivel +
                ", inventario=" + inventario +
                ", experiencia=" + experiencia +
                ", escudoTemporario=" + escudoTemporario +
                ", esquivaProximoGolpe=" + esquivaProximoGolpe +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Personagem that = (Personagem) o;
        return pontosVida == that.pontosVida
                && pontosVidaMaximo == that.pontosVidaMaximo
                && ataque == that.ataque
                && defesa == that.defesa
                && nivel == that.nivel
                && experiencia == that.experiencia
                && escudoTemporario == that.escudoTemporario
                && esquivaProximoGolpe == that.esquivaProximoGolpe
                && Objects.equals(nome, that.nome)
                && Objects.equals(inventario, that.inventario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, pontosVida, pontosVidaMaximo, ataque, defesa, nivel, inventario, experiencia, escudoTemporario, esquivaProximoGolpe);
    }

    // Variante automatica que segue rodando ate alguem cair.
    public ResultadoBatalha batalhar(Inimigo inimigo) {
        if (inimigo == null) {
            throw new IllegalArgumentException("Inimigo nao pode ser nulo");
        }
        StringBuilder registro = new StringBuilder();
        registro.append("A batalha comeca entre ").append(nome).append(" e ").append(inimigo.getNome()).append(".\n");

        while (this.estaVivo() && inimigo.estaVivo()) {
            TurnoCombate turno = executarTurno(inimigo);
            registro.append(turno.getDescricao());
            if (!turno.isJogadorVivo() || !turno.isInimigoVivo()) {
                break;
            }
        }

        boolean jogadorVenceu = estaVivo();
        registro.append(jogadorVenceu ? nome + " vence a batalha!" : inimigo.getNome() + " vence a batalha!").append("\n");

        return new ResultadoBatalha(jogadorVenceu, registro.toString());
    }

    // Resumo do combate completo: quem venceu e texto da luta.
    protected static class ResultadoBatalha {
        private final boolean jogadorVenceu;
        private final String descricaoTurnos;

        ResultadoBatalha(boolean jogadorVenceu, String descricaoTurnos) {
            this.jogadorVenceu = jogadorVenceu;
            this.descricaoTurnos = descricaoTurnos;
        }

        public boolean jogadorVenceu() {
            return jogadorVenceu;
        }

        public String getDescricaoTurnos() {
            return descricaoTurnos;
        }
    }

    // Guardamos aqui o que rolou em um turno isolado.
    public static class TurnoCombate {
        private final String descricao;
        private final boolean jogadorVivo;
        private final boolean inimigoVivo;

        TurnoCombate(String descricao, boolean jogadorVivo, boolean inimigoVivo) {
            this.descricao = descricao;
            this.jogadorVivo = jogadorVivo;
            this.inimigoVivo = inimigoVivo;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isJogadorVivo() {
            return jogadorVivo;
        }

        public boolean isInimigoVivo() {
            return inimigoVivo;
        }
    }
}
