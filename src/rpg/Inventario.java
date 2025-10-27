package rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Estoque da party: guarda, empilha e clona os itens do personagem.
public class Inventario implements Cloneable {
    private final List<Item> itens;

    public Inventario() {
        this.itens = new ArrayList<>();
    }

    public Inventario(Inventario other) {
        this();
        if (other != null) {
            other.itens.stream()
                    .map(Item::new)
                    .forEach(this::adicionarItem);
        }
    }

    public void adicionarItem(Item item) {
        if (item == null) {
            return;
        }
        // Já tiver igual? So soma na pilha.
        Optional<Item> existente = itens.stream()
                .filter(item::equals)
                .findFirst();
        if (existente.isPresent()) {
            existente.get().incrementarQuantidade(item.getQuantidade());
        } else {
            itens.add(new Item(item));
        }
    }

    public boolean removerItem(Item item, int quantidade) {
        if (item == null || quantidade <= 0) {
            return false;
        }
        // Procura o item, tira a quantidade pedida e some com ele se zerar.
        for (int i = 0; i < itens.size(); i++) {
            Item existente = itens.get(i);
            if (existente.equals(item)) {
                if (existente.getQuantidade() < quantidade) {
                    return false;
                }
                existente.decrementarQuantidade(quantidade);
                if (existente.getQuantidade() == 0) {
                    itens.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    public boolean removerItemPorNome(String nome, int quantidade) {
        if (nome == null || quantidade <= 0) {
            return false;
        }
        for (int i = 0; i < itens.size(); i++) {
            Item existente = itens.get(i);
            if (nome.equalsIgnoreCase(existente.getNome())) {
                if (existente.getQuantidade() < quantidade) {
                    return false;
                }
                existente.decrementarQuantidade(quantidade);
                if (existente.getQuantidade() == 0) {
                    itens.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    public List<Item> listarItensOrdenados() {
        List<Item> copia = new ArrayList<>();
        for (Item item : itens) {
            copia.add(item.clone());
        }
        // Ordena bonitinho antes de mostrar pra galera.
        Collections.sort(copia);
        return Collections.unmodifiableList(copia);
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    public Item encontrarItemPorNome(String nome) {
        if (nome == null) {
            return null;
        }
        for (Item item : itens) {
            if (nome.equalsIgnoreCase(item.getNome())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Inventario clone() {
        Inventario copia = new Inventario();
        for (Item item : this.itens) {
            copia.itens.add(item.clone());
        }
        // A copia sai sem nenhum fio preso na lista original.
        return copia;
    }

    @Override
    public String toString() {
        if (itens.isEmpty()) {
            return "Inventario vazio";
        }
        StringBuilder builder = new StringBuilder();
        listarItensOrdenados().forEach(item -> builder.append("- ").append(item).append('\n'));
        return builder.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inventario)) {
            return false;
        }
        Inventario that = (Inventario) o;
        return Objects.equals(listarItensOrdenados(), that.listarItensOrdenados());
    }

    @Override
    public int hashCode() {
        return Objects.hash(listarItensOrdenados());
    }
}
