package rpg;

// Mago vidrado em dano magico: bate muito, aguenta pouco.
public class Mago extends Personagem {

    public Mago() {
        super("Mago", 22, 22, 8, 3, 1, new Inventario());
    }

    public Mago(String nome) {
        super(nome, 22, 22, 8, 3, 1, new Inventario());
    }

    public Mago(Mago other) {
        super(other);
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        if (alvo == null) {
            return;
        }
        System.out.println(getNome() + " libera a Tempestade Arcana!");
        int rolagem = rolarDadoSeisLados();
        int danoPrincipal = getAtaque() + getNivel() * 2 + rolagem;
        alvo.receberDano(danoPrincipal);
        int danoResidual = Math.max(2, getNivel());
        alvo.receberDano(danoResidual);
        ativarEsquivaProximoGolpe();
        System.out.println("A magia causa " + (danoPrincipal + danoResidual) + " de dano total e envolve voce em uma cortina de neblina arcana.");
    }
}
