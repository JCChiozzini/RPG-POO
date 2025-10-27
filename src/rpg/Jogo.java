package rpg;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Jogo {
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    private Personagem jogador;
    private int capituloAtual = 1;
    private int etapaHistoria = 0;
    private boolean possuiChaveAntiga = false;
    private boolean bolsaMisteriosaColetada = false;
    private String caminhoEscolhido = "Trilha das Sequoias";
    private boolean jogoEncerrado = false;

    public static void main(String[] args) {
        new Jogo().iniciar();
    }

    // Monta a partida e segura o loop ate o final.
    public void iniciar() {
        exibirBoasVindas();
        criarPersonagemJogador();
        distribuirItensIniciais();
        loopPrincipal();
        encerrarJogo();
    }

    // Mensagem de inicio
    private void exibirBoasVindas() {
        System.out.println("====================================");
        System.out.println("        ECOS DE SILENDARE           ");
        System.out.println(" Um RPG de texto por voce mesmo!");
        System.out.println("====================================\n");
        System.out.println("Uma neblina purpura toma a vila de Silendare.");
        System.out.println("Os aldeoes desapareceram durante a noite e restou ");
        System.out.println("apenas um eco distante implorando por socorro.\n");
    }

    // Aqui a pessoa batiza o heroi e pega a classe.
    private void criarPersonagemJogador() {
        System.out.print("Qual e o nome do seu heroi? ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            nome = "Heroi sem Nome";
        }

        System.out.println("\nEscolha a sua classe:");
        System.out.println("1) Guerreiro - resistente e forte.");
        System.out.println("2) Mago - dominador das artes arcanas.");
        System.out.println("3) Arqueiro - preciso e agil.");

        int escolhaClasse = lerOpcaoMenu(1, 3);
        switch (escolhaClasse) {
            case 1:
                jogador = new Guerreiro(nome);
                break;
            case 2:
                jogador = new Mago(nome);
                break;
            default:
                jogador = new Arqueiro(nome);
                break;
        }

        System.out.println("\n" + jogador.getNome() + " encara o silencio com coragem.");
        exibirStatusJogador();
    }

    // Entrega o kit inicial pra ninguem sair de maos vazias.
    private void distribuirItensIniciais() {
        jogador.adicionarItemAoInventario(new Item("Pocao de Cura",
                "Um liquido rubro que regenera ferimentos leves.",
                "cura",
                3));
        jogador.adicionarItemAoInventario(new Item("Elixir da Furia",
                "Aquece o sangue e aumenta o potencial de ataque permanentemente.",
                "ataque",
                1));
        jogador.adicionarItemAoInventario(new Item("Manto Improvisado",
                "Pedacos de couro reforcado que ajudam a absorver golpes.",
                "defesa",
                1));
    }

    // Menuzao principal rodando enquanto todo mundo estiver vivo.
    private void loopPrincipal() {
        while (jogador.estaVivo() && !jogoEncerrado) {
            System.out.println("\nO que deseja fazer agora?");
            System.out.println("1) Explorar a regiao");
            System.out.println("2) Usar um item do inventario");
            System.out.println("3) Ver status");
            System.out.println("4) Tomar uma decisao na estoria");
            System.out.println("5) Encerrar aventura");

            int escolha = lerOpcaoMenu(1, 5);
            switch (escolha) {
                case 1:
                    explorar();
                    break;
                case 2:
                    usarItemJogador();
                    break;
                case 3:
                    exibirStatusJogador();
                    break;
                case 4:
                    tomarDecisaoHistoria();
                    break;
                case 5:
                    jogoEncerrado = true;
                    break;
                default:
                    break;
            }

            if (capituloAtual >= 4 && !jogoEncerrado && jogador.estaVivo()) {
                enfrentarChefeFinal();
            }
        }
    }

    // Decide o que pinta quando o jogador sai explorando.
    private void explorar() {
        System.out.println("\nVoce avanca pelo " + caminhoEscolhido + ".");

        int evento = random.nextInt(100);
        if (evento < 45) {
            enfrentarInimigo(criarInimigo());
        } else if (evento < 70) {
            encontrarItemEspecial();
        } else if (evento < 85) {
            dispararArmadilha();
        } else {
            System.out.println("A regiao parece calma demais... Voce sente que algo esta observando.");
            capituloAtual = Math.min(capituloAtual + 1, 4);
        }
    }

    // Loop de batalha,atacar, item, habilidade ou fuga.
    private void enfrentarInimigo(Inimigo inimigo) {
        System.out.println("\nUm " + inimigo.getTipo() + " chamado " + inimigo.getNome() + " surge das sombras!");

        boolean batalhaEncerrada = false;

        while (!batalhaEncerrada && jogador.estaVivo() && inimigo.estaVivo()) {
            System.out.println("\nHP do jogador: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMaximo());
            System.out.println("HP do inimigo: " + inimigo.getPontosVida() + "/" + inimigo.getPontosVidaMaximo());
            System.out.println("1) Atacar com tudo");
            System.out.println("2) Usar item");
            System.out.println("3) Usar habilidade especial");
            System.out.println("4) Tentar fugir");

            int opcao = lerOpcaoMenu(1, 4);
            switch (opcao) {
                case 1:
                    Personagem.TurnoCombate turno = jogador.executarTurno(inimigo);
                    System.out.print(turno.getDescricao());
                    if (!turno.isJogadorVivo() || !turno.isInimigoVivo()) {
                        batalhaEncerrada = true;
                    }
                    break;
                case 2:
                    usarItemJogador();
                    break;
                case 3:
                    jogador.usarHabilidadeEspecial(inimigo);
                    System.out.println("HP de " + inimigo.getNome() + ": " + inimigo.getPontosVida() + "/" + inimigo.getPontosVidaMaximo());
                    if (jogador.getEscudoTemporario() > 0) {
                        System.out.println("Um escudo de energia envolve voce e absorvera " + jogador.getEscudoTemporario() + " de dano.");
                    }
                    if (jogador.possuiEsquivaPreparada()) {
                        System.out.println("Voce esta pronto para esquivar do proximo golpe.");
                    }
                    if (inimigo.estaVivo()) {
                        String resposta = jogador.executarContraAtaque(inimigo);
                        System.out.print(resposta);
                    }
                    if (!jogador.estaVivo() || !inimigo.estaVivo()) {
                        batalhaEncerrada = true;
                    }
                    break;
                case 4:
                    if (tentarFuga(inimigo)) {
                        batalhaEncerrada = true;
                    } else {
                        System.out.println("A fuga falhou! O inimigo se aproveita da sua distracao.");
                        inimigo.usarHabilidadeEspecial(jogador);
                        if (!jogador.estaVivo()) {
                            batalhaEncerrada = true;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if (jogador.estaVivo() && !inimigo.estaVivo()) {
            tratarVitoria(inimigo);
            capituloAtual = Math.min(capituloAtual + 1, 4);
        } else if (!jogador.estaVivo()) {
            jogoEncerrado = true;
            System.out.println("\n" + jogador.getNome() + " cai diante da escuridao...");
        }
    }

    // Joga dado pra ver se a fuga rola ou da ruim.
    private boolean tentarFuga(Inimigo inimigo) {
        int rolagemJogador = random.nextInt(6) + 1 + jogador.getNivel();
        int rolagemInimigo = random.nextInt(6) + 1 + inimigo.getNivel();
        System.out.println("Voce rola " + rolagemJogador + " contra " + rolagemInimigo + ".");
        if (rolagemJogador >= rolagemInimigo) {
            System.out.println("Voce consegue escapar por pouco!");
            return true;
        }
        return false;
    }

    // Pega o loot do inimigo que caiu.
    private void coletarSaque(Inimigo inimigo) {
        List<Item> itens = inimigo.getInventario().listarItensOrdenados();
        if (itens.isEmpty()) {
            System.out.println("O inimigo nao carregava nada de valor.");
            return;
        }

        System.out.println("Voce revira os pertences e encontra:");
        for (Item item : itens) {
            jogador.adicionarItemAoInventario(item);
            System.out.println("- " + item.getNome() + " x" + item.getQuantidade());
        }
    }

    // Achado raro pra dar uma animada na jornada.
    private void encontrarItemEspecial() {
        Item tesouro = new Item("Essencia Boreal",
                "Particulas cintilantes que lembram o ceu noturno. Revigora completamente.",
                "curaCompleta",
                1);
        jogador.adicionarItemAoInventario(tesouro);
        System.out.println("Voce encontrou uma " + tesouro.getNome() + "!");
    }

    // Trapaceirinha que arranca HP do nada.
    private void dispararArmadilha() {
        System.out.println("Uma armadilha arcana explode aos seus pes!");
        int dano = random.nextInt(6) + 4;
        jogador.receberDano(dano);
        System.out.println("Voce sofre " + dano + " de dano. HP atual: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMaximo());
        if (!jogador.estaVivo()) {
            jogoEncerrado = true;
            System.out.println("A armadilha foi fatal. Silendare continua amaldicoada...");
        }
    }

    // Sequencia de decisoes que move a historia adiante.
    private void tomarDecisaoHistoria() {
        switch (etapaHistoria) {
            case 0:
                System.out.println("\nDuas rotas levam ao coracao da neblina:");
                System.out.println("1) Seguir pela Trilha das Sequoias (mais segura, porem lenta).");
                System.out.println("2) Descer pela Passagem Subterranea (rapida, mas instavel).");
                int escolha = lerOpcaoMenu(1, 2);
                if (escolha == 1) {
                    caminhoEscolhido = "Trilha das Sequoias";
                    System.out.println("Voce sente o cheiro de resina e ouve sussurros antigos entre as arvores.");
                } else {
                    caminhoEscolhido = "Passagem Subterranea";
                    System.out.println("O ar fica denso e umido. As paredes tremem com algo que desperta.");
                }
                etapaHistoria++;
                break;
            case 1:
                System.out.println("\nUma porta de pedra bloqueia a passagem. Um relevo mostra uma chave entalhada.");
                System.out.println("1) Forcar a porta com o poder bruto.");
                System.out.println("2) Procurar uma chave escondida nas redondezas.");
                int decisaoPorta = lerOpcaoMenu(1, 2);
                if (decisaoPorta == 1) {
                    int rolagem = random.nextInt(6) + 1 + jogador.getAtaque();
                    if (rolagem >= 12) {
                        System.out.println("Com um grito, voce arromba a porta! Atras dela ha um altar e uma chave antiga.");
                        possuiChaveAntiga = true;
                        jogador.adicionarItemAoInventario(new Item("Chave Antiga", "Permite acessar a torre envolta em nevoa.", "historia", 1));
                    } else {
                        System.out.println("A porta resiste e uma onda de energia o repulsa!");
                        jogador.receberDano(5);
                        System.out.println("HP atual: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMaximo());
                    }
                } else {
                    System.out.println("Voce encontra um esconderijo na base de uma estatua.");
                    possuiChaveAntiga = true;
                    jogador.adicionarItemAoInventario(new Item("Chave Antiga", "Permite acessar a torre envolta em nevoa.", "historia", 1));
                }
                etapaHistoria++;
                break;
            case 2:
                System.out.println("\nUma bolsa puida repousa no chao coberta de cinzas.");
                System.out.println("1) Coletar a bolsa.");
                System.out.println("2) Ignorar e seguir em frente.");
                int decisaoBolsa = lerOpcaoMenu(1, 2);
                if (decisaoBolsa == 1) {
                    bolsaMisteriosaColetada = true;
                    Item bolsa = new Item("Bolsa Misteriosa", "Contem artefatos variados dos aldeoes.", "saque", 2);
                    jogador.adicionarItemAoInventario(bolsa);
                    System.out.println("Dentro da bolsa voce encontra suprimentos e um bilhete pedindo por ajuda na torre.");
                } else {
                    System.out.println("Voce decide nao arriscar. Talvez algo amaldicoado esteja ali.");
                }
                etapaHistoria++;
                break;
            default:
                System.out.println("Voce ja tomou todas as decisoes importantes por agora.");
                break;
        }
    }

    // Boss final: so aparece se a chave ja estiver com o jogador.
    private void enfrentarChefeFinal() {
        if (!possuiChaveAntiga) {
            System.out.println("\nUma torre envolta em neblina surge a frente, mas voce ainda nao possui a chave.");
            return;
        }
        System.out.println("\nChegou o momento decisivo. Uma sombra colossal se materializa na torre.");

        Inimigo eco = new Inimigo("Eco da Nevoa", 40, 40, 9, 6, 3, "Manifestacao Arcana");
        eco.adicionarItemAoInventario(new Item("Pocao de Cura Avancada",
                "Elixir raro que restaura uma grande quantidade de vitalidade.",
                "cura",
                2));
        eco.adicionarItemAoInventario(new Item("Coroa de Nevoa",
                "Reliquia ancestral que dissipa a maldicao de Silendare.",
                "vitoria",
                1));

        enfrentarInimigo(eco);

        if (jogador.estaVivo() && !eco.estaVivo()) {
            jogoEncerrado = true;
            System.out.println("\nA nevoa se dissipa e os ecos encontram descanso.");
            if (bolsaMisteriosaColetada) {
                System.out.println("Os aldeoes resgatados lhe agradecem pela coragem e pelas memorias devolvidas.");
            }
            System.out.println("Silendare sorri novamente gracas a " + jogador.getNome() + ".");
        }
    }

    private void tratarVitoria(Inimigo inimigo) {
        System.out.println("Voce derrota " + inimigo.getNome() + "!");
        int experienciaGanha = calcularExperienciaGanha(inimigo);
        System.out.println("Voce ganha " + experienciaGanha + " de experiencia.");
        List<String> mensagensNivel = jogador.ganharExperiencia(experienciaGanha);
        mensagensNivel.forEach(System.out::println);
        coletarSaque(inimigo);
    }

    private int calcularExperienciaGanha(Inimigo inimigo) {
        int base = 40;
        int bonusNivel = inimigo.getNivel() * 20;
        int bonusCapitulo = capituloAtual * 10;
        return base + bonusNivel + bonusCapitulo;
    }

    // Mostra os itens e dispara o efeito do que for usado.
    private void usarItemJogador() {
        List<Item> itens = jogador.getInventario().listarItensOrdenados();
        if (itens.isEmpty()) {
            System.out.println("Seu inventario esta vazio.");
            return;
        }

        System.out.println("\nItens disponiveis:");
        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);
            System.out.println((i + 1) + ") " + item);
        }
        System.out.println((itens.size() + 1) + ") Cancelar");

        int escolha = lerOpcaoMenu(1, itens.size() + 1);
        if (escolha == itens.size() + 1) {
            return;
        }

        Item selecionado = itens.get(escolha - 1);
        Item real = jogador.getInventario().encontrarItemPorNome(selecionado.getNome());
        if (real == null) {
            System.out.println("Algo deu errado ao acessar o item.");
            return;
        }

        if (aplicarEfeitoItem(real)) {
            boolean removido = jogador.removerItemDoInventario(real, 1);
            if (!removido) {
                System.out.println("Nao foi possivel consumir o item.");
            }
        } else {
            System.out.println("Voce decide nao utilizar o item agora.");
        }
    }

    // Converte a tag do item em beneficio real pro heroi.
    private boolean aplicarEfeitoItem(Item item) {
        switch (item.getEfeito()) {
            case "cura":
                jogador.curar(15);
                System.out.println("Voce sente a energia percorrer o corpo. HP atual: "
                        + jogador.getPontosVida() + "/" + jogador.getPontosVidaMaximo());
                return true;
            case "curaCompleta":
                jogador.curar(jogador.getPontosVidaMaximo());
                System.out.println("Toda a dor desaparece. Voce esta em plena forma!");
                return true;
            case "ataque":
                jogador.setAtaque(jogador.getAtaque() + 2);
                System.out.println("Seu ataque aumenta para " + jogador.getAtaque() + ".");
                return true;
            case "defesa":
                jogador.setDefesa(jogador.getDefesa() + 2);
                System.out.println("Sua defesa aumenta para " + jogador.getDefesa() + ".");
                return true;
            case "historia":
            case "saque":
            case "vitoria":
                System.out.println("Este item parece importante demais para ser usado agora.");
                return false;
            default:
                System.out.println("Voce nao sabe como usar este item.");
                return false;
        }
    }

    // Mostra status resumido do jogador.
    private void exibirStatusJogador() {
        System.out.println("\n--- Status de " + jogador.getNome() + " ---");
        System.out.println("Classe: " + jogador.getClass().getSimpleName());
        System.out.println("HP: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMaximo());
        System.out.println("Ataque: " + jogador.getAtaque());
        System.out.println("Defesa: " + jogador.getDefesa());
        System.out.println("Nivel: " + jogador.getNivel());
        System.out.println("Experiencia: " + jogador.getExperiencia() + "/" + jogador.getExperienciaParaProximoNivel());
        System.out.println("Inventario:");
        System.out.println(jogador.getInventario());
    }

    // Despedida que varia conforme a forma como terminou.
    private void encerrarJogo() {
        if (jogador == null) {
            return;
        }
        if (jogador.estaVivo() && jogoEncerrado) {
            System.out.println("\nObrigado por jogar! Relembre este momento com orgulho.");
        } else if (!jogador.estaVivo()) {
            System.out.println("\nAs lendas dirao que " + jogador.getNome() + " lutou ate o fim.");
        } else {
            System.out.println("\nVoce decide retornar para planejar melhor a proxima jornada.");
        }
        scanner.close();
    }

    // Le o input numerico do menu com validacao.
    private int lerOpcaoMenu(int minimo, int maximo) {
        while (true) {
            System.out.print("Escolha (" + minimo + "-" + maximo + "): ");
            String entrada = scanner.nextLine();
            try {
                int valor = Integer.parseInt(entrada.trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Opcao invalida. Tente novamente.");
        }
    }

    // Cria inimigo ajustando dificuldade conforme o capitulo
    private Inimigo criarInimigo() {
        int escolha = random.nextInt(3);
        Inimigo inimigo;
        switch (escolha) {
            case 0:
                inimigo = new Inimigo("Lobo das Sombras", 20 + capituloAtual * 2, 20 + capituloAtual * 2, 6 + capituloAtual, 4 + capituloAtual / 2, capituloAtual, "Fera Sombria");
                inimigo.adicionarItemAoInventario(new Item("Presa Afiada", "Pode ser vendida ou usada em rituais.", "saque", 1));
                inimigo.adicionarItemAoInventario(new Item("Pocao de Cura", "Mistura que restaura vitalidade.", "cura", 1));
                break;
            case 1:
                inimigo = new Inimigo("Acolito da Nevoa", 18 + capituloAtual * 2, 18 + capituloAtual * 2, 7 + capituloAtual, 3 + capituloAtual, capituloAtual, "Cultista");
                inimigo.adicionarItemAoInventario(new Item("Codice Fragmentado", "Fragmentos de conhecimento sombrio.", "saque", 1));
                inimigo.adicionarItemAoInventario(new Item("Essencia Rutilante", "Restabelece energia vital.", "cura", 1));
                break;
            default:
                inimigo = new Inimigo("Sentinela Opaca", 24 + capituloAtual * 3, 24 + capituloAtual * 3, 5 + capituloAtual, 5 + capituloAtual, capituloAtual, "Constructo");
                inimigo.adicionarItemAoInventario(new Item("Runa de Aco", "Fortalece a armadura quando estudada.", "defesa", 1));
                break;
        }
        return inimigo;
    }
}
