package rpg;

// Arqueiro joga seguro: dano constante e olho em critico.
public class Arqueiro extends Personagem {

    public Arqueiro() {
        super("Arqueiro", 24, 24, 7, 4, 1, new Inventario());
    }

    public Arqueiro(String nome) {
        super(nome, 24, 24, 7, 4, 1, new Inventario());
    }

    public Arqueiro(Arqueiro other) {
        super(other);
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        if (alvo == null) {
            return;
        }
        System.out.println(getNome() + " executa a Chuva de Flechas!");
        int primeiro = Math.max(3, getAtaque() + rolarDadoSeisLados() - Math.max(0, alvo.getDefesa() - 2));
        int segundo = Math.max(2, getAtaque() / 2 + rolarDadoSeisLados());
        int total = primeiro + segundo;
        alvo.receberDano(total);
        int novaDefesa = Math.max(0, alvo.getDefesa() - 1);
        alvo.setDefesa(novaDefesa);
        adicionarEscudoTemporario(2 + getNivel());
        ativarEsquivaProximoGolpe();
        System.out.println("As flechas perfuram causando " + total + " de dano. A investida reduz a defesa do alvo para " + novaDefesa + " e voce ganha cobertura.");
    }
}
