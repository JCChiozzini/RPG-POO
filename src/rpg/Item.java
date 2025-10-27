package rpg;

import java.util.Objects;

// Modela qualquer item do jogo: poções, armas, chaves, etc.
public class Item implements Cloneable, Comparable<Item> {
    private String nome;
    private String descricao;
    private String efeito;
    private int quantidade;

    public Item() {
        this("Item Desconhecido", "Sem descricao", "Nenhum efeito", 0);
    }

    public Item(String nome, String descricao, String efeito, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = Math.max(0, quantidade);
    }

    public Item(Item other) {
        this(other.nome, other.descricao, other.efeito, other.quantidade);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEfeito() {
        return efeito;
    }

    public void setEfeito(String efeito) {
        this.efeito = efeito;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = Math.max(0, quantidade);
    }

    // Somente valores positivos contam, entao so empilha pra cima.
    public void incrementarQuantidade(int valor) {
        setQuantidade(quantidade + Math.max(0, valor));
    }

    // Tira unidades, mas nunca deixa cair abaixo de zero.
    public void decrementarQuantidade(int valor) {
        setQuantidade(quantidade - Math.max(0, valor));
    }

    @Override
    public Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone nao suportado", e);
        }
    }

    @Override
    public String toString() {
        return nome + " (" + quantidade + "): " + descricao + " -> " + efeito;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(nome, item.nome)
                && Objects.equals(descricao, item.descricao)
                && Objects.equals(efeito, item.efeito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, descricao, efeito);
    }

    @Override
    public int compareTo(Item other) {
        // Ordena primeiro pelo nome; em caso de empate, olha o efeito.
        int comparacaoNome = this.nome.compareToIgnoreCase(other.nome);
        if (comparacaoNome != 0) {
            return comparacaoNome;
        }
        return this.efeito.compareToIgnoreCase(other.efeito);
    }
}
