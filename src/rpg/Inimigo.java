package rpg;

// Base pros monstros todos, do goblin ao chefao.
public class Inimigo extends Personagem {
    private String tipo;

    public Inimigo() {
        this("Goblin", 18, 18, 5, 3, 1, "Horda");
    }

    public Inimigo(String nome, int pontosVida, int pontosVidaMaximo, int ataque, int defesa, int nivel, String tipo) {
        super(nome, pontosVida, pontosVidaMaximo, ataque, defesa, nivel, new Inventario());
        this.tipo = tipo;
    }

    public Inimigo(Inimigo other) {
        super(other);
        this.tipo = other.tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        if (alvo == null) {
            return;
        }
        // Golpe padrao com dado extra pra nao deixar o heroi sossegado.
        int dano = Math.max(1, getAtaque() + rolarDadoSeisLados() - alvo.getDefesa());
        alvo.receberDano(dano);
    }

    @Override
    public String toString() {
        return super.toString() + " Tipo: " + tipo;
    }
}
