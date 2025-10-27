package rpg;

// Guerreiro raiz: muita armadura e pancada frontal.
public class Guerreiro extends Personagem {

    public Guerreiro() {
        super("Guerreiro", 30, 30, 6, 5, 1, new Inventario());
    }

    public Guerreiro(String nome) {
        super(nome, 30, 30, 6, 5, 1, new Inventario());
    }

    public Guerreiro(Guerreiro other) {
        super(other);
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        if (alvo == null) {
            return;
        }
        System.out.println(getNome() + " desfere o Golpe do Escudo!");
        int rolagem = rolarDadoSeisLados();
        int dano = Math.max(5, getAtaque() + getNivel() + rolagem - Math.max(0, alvo.getDefesa() / 2));
        alvo.receberDano(dano);
        int escudo = 5 + getNivel();
        adicionarEscudoTemporario(escudo);
        int cura = Math.max(2, getNivel());
        curar(cura);
        System.out.println("O impacto causa " + dano + " de dano e ergue um escudo de " + escudo + ". Voce recupera " + cura + " de HP.");
    }
}
